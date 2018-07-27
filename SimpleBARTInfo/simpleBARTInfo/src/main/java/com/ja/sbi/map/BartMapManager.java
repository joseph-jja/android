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

    private static ScrollView vscroll;
    private static HorizontalScrollView hscroll;
    private static ImageView bartMapImage;
    private static Button zoomIn;
    private static Button zoomOut;

    private static boolean isInitialized = false;

    public BartMapManager(Context context) {

        final Activity activity = (Activity) context;

        // get image view
        BartMapManager.bartMapImage = (ImageView) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_image);
        //BartMapManager.bartMapImage.setAdjustViewBounds(true);

        BartMapManager.hscroll = (HorizontalScrollView) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_hscroll);
        BartMapManager.vscroll = (ScrollView) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_vscroll);

        BartMapManager.zoomIn = (Button) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_zoom_in);
        BartMapManager.zoomOut = (Button) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_zoom_out);

        // attache some events below
        BartMapManager.bartMapImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // doing this allows us to override the size of the image
                if (!isInitialized) {
                    BartMapManager.bartMapImage.layout(0, 0, defaultImageWidth, defaultImageHeight);
                    isInitialized = true;
                }
            }
        });

        BartMapManager.hscroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                final int curentImageWidth = BartMapManager.bartMapImage.getWidth();

                int maxScrollX = curentImageWidth - 1000;

                //Log.i(LOG_NAME, Integer.valueOf(curentImageWidth).toString() + " " + Integer.valueOf(scrollX).toString() + " " + Integer.valueOf(oldScrollX).toString());
                if ( zoomFactor == 1 ) {
                    BartMapManager.hscroll.scrollTo(0,scrollY);
                } else if ( scrollX > maxScrollX){
                    BartMapManager.hscroll.scrollTo(maxScrollX,scrollY);
                }
            }
        });

        BartMapManager.zoomIn.setOnClickListener(new View.OnClickListener() {
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

        BartMapManager.zoomOut.setOnClickListener(new View.OnClickListener() {
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
        final int width = BartMapManager.bartMapImage.getWidth();
        final int height = BartMapManager.bartMapImage.getHeight();

        // no height or width of the image then we do nothing
        // this is called when the image loads, and the sizes are 0
        if (width <= 0 || height <= 0) {
            return;
        }

        // figure out the zoom size of the image
        final int xWidth = defaultImageWidth * zoomFactorLocal;
        final int xHeight = defaultImageHeight * zoomFactorLocal;

        // zoom the image
        BartMapManager.bartMapImage.layout(0, 0, xWidth, xHeight);

        // figure out how big the horizontal scroll should be
        // and where
        final int hTop = BartMapManager.hscroll.getTop();
        final int hLeft = BartMapManager.hscroll.getLeft();
        final int hHeight = BartMapManager.hscroll.getHeight();
        final int hWidth = BartMapManager.hscroll.getWidth();

        // save initial value of the horizontal scroll
        if (initialHeightOfHScroll == 0) {
            initialHeightOfHScroll = hHeight;
        }

        // so the horizontal scroll wraps the vertical scroll
        // this sets the height of the horizontal scroll so that
        // the contained vertical scroll can scroll
        if (zoomFactorLocal == 1) {
            BartMapManager.hscroll.layout(hLeft, hTop, hWidth, initialHeightOfHScroll);
        } else {
            BartMapManager.hscroll.layout(hLeft, hTop, hWidth, xHeight);
        }
    }
}
