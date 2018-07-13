package com.ja.sbi.map;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ja.activity.BaseActivity;
import com.ja.sbi.R;

public class BartMapActivity extends BaseActivity {

    private final String LOG_NAME = this.getClass().getName();

    public static final int IMAGE_HEIGHT = 500;
    public static final int IMAGE_WIDTH = 500;
    public static final int MAX_DIMENSIONS = 1000;
    public static final int MIN_DIMENSIONS = 250;
    private int zoomFactor = 0;

    //public static final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(
     //       ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_NAME, "Initializing!");
        super.onCreate(savedInstanceState);

        Log.d(LOG_NAME, "Super called!");
        super.setContentView(R.layout.bart_map);

        initializeActivity();
    }


    public void initializeActivity() {

        // get image view
        final ImageView image = (ImageView) this.findViewById(R.id.bart_map_image);

        // get the web view
        final WebView web = (WebView) this.findViewById(R.id.bart_map_web);

        // iniailize the web view
        /*setWebView(web, image);

        // zoom
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        setupZoomControls(this, web, image);*/
    }
/*
    public void resizeImage(Activity activity, WebView web, ImageView image, int width, int height) {

        image.layout(0, 0, width, height);
        setWebView(web, image);

        Log.d(LOG_NAME, "Resized image to " + image.getWidth() + "x" + image.getHeight());
    }

    public void setWebView(WebView web, ImageView image) {

        int width = image.getWidth();
        int height = image.getHeight();
        if (zoomFactor == 0) {
            width = 0;
            height = 0;
        }

        final String container = "<div style=\"position:absolute;top: 0px; left:0px;height:" + height + "px;width:" + width + "px;\"></div>";
        final String content = "<html><body>" + container + "</body></html>";
        web.loadData(content, "text/html", "UTF-8");
        web.setBackgroundColor(0);

        Log.d(LOG_NAME, "Got width and height = " + height + "x" + width);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setupZoomControls(Activity activity, WebView webView, ImageView imageView) {

        final Activity webActivity = activity;
        final WebView web = webView;
        final WebSettings webViewSettings = webView.getSettings();
        final ImageView image = imageView;

        webViewSettings.setDisplayZoomControls(true);
        ArrayList<View> touchables = image.getTouchables();

        if (touchables != null) {
            final int tsize = touchables.size();
            if (tsize >= 1) {
                final View zoomOut = touchables.get(0);
                if (zoomOut != null) {
                    zoomOut.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            zoomFactor -= 1;
                            zoomFactor = (zoomFactor >= -1) ? zoomFactor : -1;
                            int dimension = image.getWidth() - Math.abs(250 * zoomFactor);
                            dimension = (dimension >= BartMapActivity.MIN_DIMENSIONS) ? dimension : BartMapActivity.MIN_DIMENSIONS;
                            resizeImage(webActivity, web, image, dimension, dimension);
                        }
                    });
                }
            }
            if (tsize >= 2) {
                final View zoomIn = touchables.get(1);
                if (zoomIn != null) {
                    zoomIn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            zoomFactor += 1;
                            zoomFactor = (zoomFactor <= 2) ? zoomFactor : 2;
                            int dimension = image.getWidth() + Math.abs(250 * zoomFactor);
                            dimension = (dimension <= BartMapActivity.MAX_DIMENSIONS) ? dimension : BartMapActivity.MAX_DIMENSIONS;
                            resizeImage(webActivity, web, image, dimension, dimension);
                        }
                    });
                }
            }
            if (tsize >= 3) {
                final View zoomView = touchables.get(2);
                if (zoomView != null) {
                    zoomView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            zoomFactor = 0;
                            setWebView(web, image);
                            final Display display = webActivity.getWindowManager().getDefaultDisplay();
                            int dimension = (display.getWidth() <= display.getHeight()) ? display.getWidth() : display.getHeight();
                            resizeImage(webActivity, web, image, dimension, dimension);
                        }
                    });
                }
            }
        }

        //final FrameLayout mContentView = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        //mContentView.addView(zoomControls, BartMapActivity.ZOOM_PARAMS);
    }*/
}
