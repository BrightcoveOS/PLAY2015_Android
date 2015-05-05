package com.brightcove.player.sample.play;

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.captioning.BrightcoveCaptionFormat;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.samples.play.R;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic demo for the Brightcove Native Player for Android that demonstrates media playback with
 * custom controls, captioning and ad playback.
 */
public class MainActivity extends BrightcovePlayer {
    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private GoogleIMAComponent googleIMAComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);

        // Add custom controls.
        BrightcoveMediaController mediaController = new BrightcoveMediaController(brightcoveVideoView);
        brightcoveVideoView.setMediaController(mediaController);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        super.onCreate(savedInstanceState);

        // Add a test video from the res/raw directory to the BrightcoveVideoView.
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Uri video = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.sintel_trailer);
        brightcoveVideoView.add(Video.createVideo(video.toString()));

        // Add captions for source video.
        BrightcoveCaptionFormat captionFormat = BrightcoveCaptionFormat.createCaptionFormat("text/vtt", "en");
        try {
            brightcoveVideoView.addSubtitleSource(new URI("https://raw.githubusercontent.com/BrightcoveOS/android-player-samples/master/WebVTTSampleApp/src/main/res/raw/sintel_trailer_en.vtt"), captionFormat);
            captionFormat = BrightcoveCaptionFormat.createCaptionFormat("text/vtt", "de");
            brightcoveVideoView.addSubtitleSource(new URI("https://raw.githubusercontent.com/BrightcoveOS/android-player-samples/master/WebVTTSampleApp/src/main/res/raw/sintel_trailer_de.vtt"), captionFormat);
            captionFormat = BrightcoveCaptionFormat.createCaptionFormat("text/vtt", "es");
            brightcoveVideoView.addSubtitleSource(new URI("https://raw.githubusercontent.com/BrightcoveOS/android-player-samples/master/WebVTTSampleApp/src/main/res/raw/sintel_trailer_es.vtt"), captionFormat);
            captionFormat = BrightcoveCaptionFormat.createCaptionFormat("text/vtt", "fr");
            brightcoveVideoView.addSubtitleSource(new URI("https://raw.githubusercontent.com/BrightcoveOS/android-player-samples/master/WebVTTSampleApp/src/main/res/raw/sintel_trailer_fr.vtt"), captionFormat);
            captionFormat = BrightcoveCaptionFormat.createCaptionFormat("text/vtt", "it");
            brightcoveVideoView.addSubtitleSource(new URI("https://raw.githubusercontent.com/BrightcoveOS/android-player-samples/master/WebVTTSampleApp/src/main/res/raw/sintel_trailer_it.vtt"), captionFormat);
            captionFormat = BrightcoveCaptionFormat.createCaptionFormat("text/vtt", "nl");
            brightcoveVideoView.addSubtitleSource(new URI("https://raw.githubusercontent.com/BrightcoveOS/android-player-samples/master/WebVTTSampleApp/src/main/res/raw/sintel_trailer_nl.vtt"), captionFormat);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error with captions occurred: " + e.getMessage());
        }

        setupGoogleIMA();

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }

    /**
     * Provide a sample illustrative ad.
     */
    private String[] googleAds = {
            // Sample ad: Plato running locally, valid VAST response
            "http://10.1.12.245:9090/formats/IMA3/responses/local-mp4-response.handlebars"
    };

    /**
     * Specify where the ad should interrupt the main video.  This code provides a procedural
     * abastraction for the Google IMA Plugin setup code.
     */
    private void setupCuePoints(Source source) {

        Log.v(TAG, "Setting up Cue Points");
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> details = new HashMap<String, Object>();

        CuePoint cuePoint = null;

        // preroll
        cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

        // midroll at 20 seconds.
        cuePoint = new CuePoint(20 * (int) DateUtils.SECOND_IN_MILLIS, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

        // postroll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);
        Log.v(TAG, "Done setting up Cue Points");
    }

    /**
     * Setup the Brightcove IMA Plugin: add some cue points; establish a factory object to
     * obtain the Google IMA SDK instance.
     */
    private void setupGoogleIMA() {

        // Defer adding cue points until the set video event is triggered.
        eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                setupCuePoints((Source) event.properties.get(Event.SOURCE));
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging of ad starts
        eventEmitter.on(GoogleIMAEventType.DID_START_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of ad completions.
        eventEmitter.on(GoogleIMAEventType.DID_COMPLETE_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Create a container object for the ads to be presented.
                AdDisplayContainer container = sdkFactory.createAdDisplayContainer();
                container.setPlayer(googleIMAComponent.getVideoAdPlayer());
                container.setAdContainer(brightcoveVideoView);

                // Build the list of ads request objects, one per ad
                // URL, and point each to the ad display container
                // created above.
                ArrayList<AdsRequest> adsRequests = new ArrayList<AdsRequest>(googleAds.length);
                for (String adURL : googleAds) {
                    AdsRequest adsRequest = sdkFactory.createAdsRequest();
                    adsRequest.setAdTagUrl(adURL);
                    adsRequest.setAdDisplayContainer(container);
                    adsRequests.add(adsRequest);
                }

                // Respond to the event with the new ad requests.
                event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
                eventEmitter.respond(event);
            }
        });

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = new GoogleIMAComponent(brightcoveVideoView, eventEmitter);
    }

}
