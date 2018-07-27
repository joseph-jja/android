package com.ja.sbi.map;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;

public class BartMapManager {

    private final String LOG_NAME = this.getClass().getName();

    private static final int maxZoomSize = 3;

    private int zoomFactor = 1;
    private int defaultImageWidth = 1000;
    private int defaultImageHeight = 948;

    private int initialHeightOfHScroll = 0;
    private int initialWidthOfVScroll = 0;

    private ScrollView vscroll;
    private HorizontalScrollView hscroll;
    private ImageView bartMapImage;
    private Button zoomIn;
    private Button zoomOut;

    private static Activity activity;

    private static boolean isInitialized = false;

    public BartMapManager(Context context) {

        if (activity == null) {
            activity = (Activity) context;
        }

        // get image view
        this.bartMapImage = (ImageView) BartMapManager.activity.findViewById(R.id.bart_map_image);
        //BartMapManager.bartMapImage.setAdjustViewBounds(true);

        this.hscroll = (HorizontalScrollView) BartMapManager.activity.findViewById(R.id.bart_map_hscroll);
        this.vscroll = (ScrollView) BartMapManager.activity.findViewById(R.id.bart_map_vscroll);

        this.zoomIn = (Button) BartMapManager.activity.findViewById(R.id.bart_map_zoom_in);
        this.zoomOut = (Button) BartMapManager.activity.findViewById(R.id.bart_map_zoom_out);

        // attache some events below
        this.bartMapImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // doing this allows us to override the size of the image
                if (!isInitialized) {
                    final ImageView imageMap = (ImageView) BartMapManager.activity.findViewById(R.id.bart_map_image);
                    imageMap.layout(0, 0, defaultImageWidth, defaultImageHeight);
                    isInitialized = true;
                }
            }
        });

        this.hscroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                final ImageView imageMap = (ImageView) BartMapManager.activity.findViewById(R.id.bart_map_image);

                final int curentImageWidth = imageMap.getWidth();

                int maxScrollX = curentImageWidth - 1000;

                final HorizontalScrollView hscrollLocal = (HorizontalScrollView) BartMapManager.activity.findViewById(R.id.bart_map_hscroll);

                //Log.i(LOG_NAME, Integer.valueOf(curentImageWidth).toString() + " " + Integer.valueOf(scrollX).toString() + " " + Integer.valueOf(oldScrollX).toString());
                if (zoomFactor == 1) {
                    hscrollLocal.scrollTo(0, scrollY);
                } else if (scrollX > maxScrollX) {
                    hscrollLocal.scrollTo(maxScrollX, scrollY);
                }
            }
        });

        this.zoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (zoomFactor >= BartMapManager.maxZoomSize) {
                    zoomFactor = BartMapManager.maxZoomSize;
                    return;
                }
                zoomFactor++;
                resizeImage(zoomFactor);
            }
        });

        this.zoomOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (zoomFactor <= 1) {
                    zoomFactor = 1;
                    return;
                }
                zoomFactor--;
                resizeImage(zoomFactor);
            }
        });
    }

    public void resizeImage(int zoomFactorLocal) {

        // these are the size of the image at the time this is called
        final int width = this.bartMapImage.getWidth();
        final int height = this.bartMapImage.getHeight();

        // no height or width of the image then we do nothing
        // this is called when the image loads, and the sizes are 0
        if (width <= 0 || height <= 0) {
            return;
        }

        // figure out the zoom size of the image
        final int xWidth = defaultImageWidth * zoomFactorLocal;
        final int xHeight = defaultImageHeight * zoomFactorLocal;

        // zoom the image
        this.bartMapImage.layout(0, 0, xWidth, xHeight);

        // figure out how big the horizontal scroll should be
        // and where
        final int hTop = this.hscroll.getTop();
        final int hLeft = this.hscroll.getLeft();
        final int hHeight = this.hscroll.getHeight();
        final int hWidth = this.hscroll.getWidth();

        // save initial value of the horizontal scroll
        if (initialHeightOfHScroll == 0) {
            initialHeightOfHScroll = hHeight;
        }

        // so the horizontal scroll wraps the vertical scroll
        // this sets the height of the horizontal scroll so that
        // the contained vertical scroll can scroll
        if (zoomFactorLocal == 1) {
            this.hscroll.layout(hLeft, hTop, hWidth, initialHeightOfHScroll);
        } else {
            this.hscroll.layout(hLeft, hTop, hWidth, xHeight);
        }
    }
}
