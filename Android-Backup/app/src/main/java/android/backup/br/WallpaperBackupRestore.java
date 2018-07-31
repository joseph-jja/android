package android.backup.br;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.backup.utils.xml.XMLDocumentWriter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class WallpaperBackupRestore {

    private static final String WALLPAPER_FILE_NAME = "wallpaper.png";

    public static final Object restoreData(Activity acty, boolean restore) {

        final WallpaperManager wpmgr = WallpaperManager.getInstance(acty);

        Bitmap bmp = null;
        File sdcard = XMLDocumentWriter.getSDCardLocation();
        if ( sdcard != null  ) {
            final ImageView iView = getImageView(acty);
            final String filename = sdcard.getAbsolutePath() + System.getProperty("file.separator") + WALLPAPER_FILE_NAME;
            //Log.d(LOGGER_TAG, "Wallpaper filename: " + filename);
            bmp = BitmapFactory.decodeFile(filename);
            if ( iView != null && bmp != null ) {
                iView.setImageBitmap(bmp);
                //Log.d(LOGGER_TAG, "We got a bmp and file! ");

                // need to set the background wallpaper now
                try {
                    if ( restore ) {
                        wpmgr.setBitmap(bmp);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return (Object)bmp;
    }

    public static final Object saveData(Activity acty) {

        final WallpaperManager wpmgr = WallpaperManager.getInstance(acty);
        
        WallpaperInfo wpInfo = wpmgr.getWallpaperInfo();
        if ( wpInfo != null ) {
            // live wallpaper
        } 

        // get drawable
        Drawable img = wpmgr.getDrawable();
        Bitmap bmp = null;
        //Log.d(LOGGER_TAG, "Do we have the drawable? " + img);

        // find the image view
        final ImageView iView = getImageView(acty);
        if ( iView != null && img != null ) {
            iView.setImageDrawable(img);
            iView.setBackgroundDrawable(img);

            BitmapDrawable bmpDraw = (BitmapDrawable)img;
            //Log.d(LOGGER_TAG, "Do we have the bitmap? " + bmpDraw.getBitmap());
            bmp = bmpDraw.getBitmap();
        }

        //Log.d(LOGGER_TAG, "Do we have a none null bmp? " + bmp);
        File sdcard = XMLDocumentWriter.getSDCardLocation();
        if ( bmp != null && sdcard != null  ) {
            //Log.d(LOGGER_TAG, "going to write the file. ");
            File write = new File(sdcard, WALLPAPER_FILE_NAME);
            try {
                FileOutputStream ostream = new FileOutputStream(write);
                bmp.compress(CompressFormat.PNG, 100, ostream);
                ostream.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }
        return (Object)bmp;
    }
    
    private static final ImageView getImageView(Activity acty) { 

        ImageView imageView = new ImageView(acty);
        imageView.setDrawingCacheEnabled(true);
        
        return imageView;
    }
}
