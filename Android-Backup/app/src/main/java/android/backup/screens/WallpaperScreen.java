package android.backup.screens;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class WallpaperScreen extends Activity {

    private String LOGGER_TAG = getClass().getName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 

        // set view
        setContentView(R.layout.wallpaper);

        Object results = TabScreen.getResultData().get(this.getClass().getName());
        
        this.renderWallpaperView(results);

    }
    
    public void renderWallpaperView(Object data) {
        
        Bitmap bmp = (Bitmap)data;
        ImageView imageView = null;
        final View iView = findViewById(R.id.wallpaperImage);
        if ( iView != null ) {
            Log.d(LOGGER_TAG, "Got an image view? " + iView);
            imageView = (ImageView)iView;
            imageView.setDrawingCacheEnabled(true);
            imageView.setImageBitmap(bmp);
        }
    }
}
