package com.ja.sbi.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;

public class BartMapManager {

    private final String LOG_NAME = this.getClass().getName();

    private Drawable image;
    private int zoomFactor = 0;

    public void initializeActivity(Context context) {

        final Activity activity = (Activity) context;

        // get image view
        final ImageView image = (ImageView) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_image);
    }

    public void resizeImage(Activity activity, WebView web, ImageView image, int width, int height) {

        image.layout(0, 0, width, height);

        Log.d(LOG_NAME, "Resized image to " + image.getWidth() + "x" + image.getHeight());
    }

 }
