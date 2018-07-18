package com.ja.sbi.map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;

public class BartMapManager {

    private final String LOG_NAME = this.getClass().getName();

    private static final int maxZoomSize = 8;

    private int zoomFactor = 1;
    private int defaultImageWidth = 500;
    private int defaultImageHeight = 500;

    private static LinearLayout parent;
    private static ScrollView vscroll;
    private static HorizontalScrollView hscroll;
    private static ImageView bartMapImage;
    private static Button zoomIn;
    private static Button zoomOut;

    public BartMapManager(Context context) {

        final Activity activity = (Activity) context;

        // get image view
        BartMapManager.bartMapImage = (ImageView) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_image);
        BartMapManager.bartMapImage.setAdjustViewBounds(true);

        BartMapManager.parent = (LinearLayout)((SimpleBARTInfo)context).findViewById(R.id.bart_map_container);

        BartMapManager.hscroll = (HorizontalScrollView)((SimpleBARTInfo)context).findViewById(R.id.bart_map_hscroll);
        BartMapManager.vscroll = (ScrollView)((SimpleBARTInfo)context).findViewById(R.id.bart_map_vscroll);

        BartMapManager.zoomIn = (Button) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_zoom_in);
        BartMapManager.zoomOut = (Button) ((SimpleBARTInfo) context).findViewById(R.id.bart_map_zoom_out);

        BartMapManager.zoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if ( zoomFactor >= BartMapManager.maxZoomSize ) {
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
                if ( zoomFactor <= 1 ) {
                    zoomFactor = 1;
                    return;
                }
                zoomFactor--;
                resizeImage(zoomFactor);
            }
        });
    }

    public void resizeImage(int zoomFactorLocal) {

        final int width = defaultImageWidth * zoomFactorLocal;
        final int height = defaultImageHeight * zoomFactorLocal;

        BartMapManager.bartMapImage.layout(0, 0, width, height);

        final int top = BartMapManager.hscroll.getTop();
        final int vheight = BartMapManager.vscroll.getHeight();

        int pwidth = BartMapManager.parent.getWidth();
        BartMapManager.hscroll.layout(0, top, pwidth, vheight);
        //BartMapManager.vscroll.layout(0, top, pwidth, vheight);
        //Log.d(LOG_NAME,  BartMapManager.hscroll.getWidth()+ " " +  BartMapManager.hscroll.getMeasuredWidth());
        //BartMapManager.hscroll.layout(0, top, width, height);
        //BartMapManager.vscroll.layout(0, top, width, vheight);
        //Log.d(LOG_NAME,  BartMapManager.hscroll.getWidth()+ " " +  BartMapManager.hscroll.getMeasuredWidth());
        //Log.d(LOG_NAME, "Resized image to " + BartMapManager.bartMapImage.getWidth() + "x" + BartMapManager.bartMapImage.getHeight());
    }

 }
