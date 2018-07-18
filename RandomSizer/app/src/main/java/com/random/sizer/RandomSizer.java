package com.random.sizer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class RandomSizer extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private WebView wv; 

    private String LOG_NAME = this.getClass().getName();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        wv = (WebView)this.findViewById(R.id.randomSizerWebView);

        WebSettings wvcws = wv.getSettings();
        wvcws.setJavaScriptEnabled(true);
        wvcws.setLightTouchEnabled(true);
        wvcws.setAppCacheEnabled(false);
        wvcws.setCacheMode(WebSettings.LOAD_NO_CACHE);

        wv.setClickable(true);
        wv.setFocusable(true);
        wv.loadUrl("file:///android_asset/index.html");

        // get the sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if ( mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null ) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(LOG_NAME,"Accelerometer");
        } else if ( mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null ) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            Log.d(LOG_NAME,"Gravity");
        } else if ( mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null ) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            Log.d(LOG_NAME,"Rotation");
        } else if ( mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null ) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            Log.d(LOG_NAME,"Linear Accelerometer");
        } else if ( mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null ) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            Log.d(LOG_NAME,"Orientation");
        } else if ( mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null ) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            Log.d(LOG_NAME,"Rotation Vector");
        }

        if ( mSensor != null ) {
            Log.d(LOG_NAME,"Sensor name ? " + mSensor.getName());
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onSensorChanged(SensorEvent event){

        final Sensor mSensor = event.sensor;

        float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];
        
        // sensors change enough then we update the display
        // shake has occurred AFAIK
        float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        if (accelationSquareRoot >= 2) {
            wv.reload();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO something?
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
