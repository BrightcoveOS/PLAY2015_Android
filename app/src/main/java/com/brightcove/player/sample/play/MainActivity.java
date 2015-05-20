package com.brightcove.player.sample.play;

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.DeliveryType;
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
//        BrightcoveMediaController mediaController = new BrightcoveMediaController(brightcoveVideoView);
//        brightcoveVideoView.setMediaController(mediaController);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        super.onCreate(savedInstanceState);

        // Add a test video from the res/raw directory to the BrightcoveVideoView.
//        String PACKAGE_NAME = getApplicationContext().getPackageName();
//        Uri video = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.sintel_trailer);
//        brightcoveVideoView.add(Video.createVideo(video.toString()));
//        Video video = Video.createVideo("http://devimages.apple.com/samplecode/adDemo/ad.m3u8");
        Video video = Video.createVideo("http://discidevflash-f.akamaihd.net/i/mfxtest/digmed/dp/2015-01/23/141135.002.02.002-,500k,200k,350k,800k,1200k,1600k,2200k,3000k,4500k,.mp4.csmil/master.m3u8", DeliveryType.HLS);
        brightcoveVideoView.add(video);


        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }
}
