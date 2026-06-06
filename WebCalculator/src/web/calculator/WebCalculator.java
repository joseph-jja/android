package web.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebCalculator extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        WebView wv = (WebView)this.findViewById(R.id.calculator);

        WebSettings wvcws = wv.getSettings();
        wvcws.setJavaScriptEnabled(true);
        wvcws.setLightTouchEnabled(true);
        wvcws.setAppCacheEnabled(false);
        wvcws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wvcws.setDomStorageEnabled(true);
        
        wv.setClickable(true);
        wv.setFocusable(true);
        wv.loadUrl("file:///android_asset/index.html");
    }
}
