package com.example.native_gallery;

import static com.example.native_gallery.Constants.VIDEO_LOCATION;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullScreenVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_of_video);

        VideoView fullScreenVideo = (VideoView) findViewById(R.id.videoView);

        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent != null) {
            String videoStr = callingActivityIntent.getStringExtra(VIDEO_LOCATION);

            if (videoStr != null && fullScreenVideo != null) {
                fullScreenVideo.setVideoPath(videoStr);
                fullScreenVideo.start();

                MediaController mediaController = new MediaController(this);
                fullScreenVideo.setMediaController(mediaController);
                mediaController.setAnchorView(fullScreenVideo);
            }
        }
    }
}
