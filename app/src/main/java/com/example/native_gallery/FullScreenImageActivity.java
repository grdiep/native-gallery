package com.example.native_gallery;

import static com.example.native_gallery.Constants.IMAGE_LOCATION;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_of_image);

        ImageView fullScreenImage = (ImageView) findViewById(R.id.fullScreenImageView);

        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent != null) {
            String imageStr = callingActivityIntent.getStringExtra(IMAGE_LOCATION);

            if (imageStr != null && fullScreenImage != null) {
                Glide.with(this)
                        .load(imageStr)
                        .into(fullScreenImage);
            }
        }
    }
}
