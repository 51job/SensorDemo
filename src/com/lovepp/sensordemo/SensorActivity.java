package com.lovepp.sensordemo;

import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SensorActivity extends Activity implements SensorEventListener  {
	private SensorManager sensorManager;
	private SoundPool soundPool;
	private int soundID;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();   
	boolean loaded = false;
	private boolean color = false;
	private View view;
	private long lastUpdate;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);
        //Set the hardware buttons to control the music
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        //load the sound
        soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
        	@Override
        	public void onLoadComplete(SoundPool soundPool,int sampleId,int status){
        		loaded = true;
        	}
        });
        
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sensor, menu);
        return true;
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			getAccelerometer(event);
		}
	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		//Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];
		
		float accelationSquareRoot = (x*x+y*y+z*z)/(SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if(accelationSquareRoot >= 2)
		{
			if(actualTime - lastUpdate < 1000){
				return;
			}
			//loadSound();
			//Getting the user sound settings
			AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
			float actualVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = actualVolume/maxVolume;
			//Is the sound loaded already?
			if(loaded){
				soundID = soundPool.load(this,R.raw.shake_sound_male,1);
				soundPool.play(soundID, volume, volume, 1, 0, 1f);
				Log.e("Test", "Played sound");
			}
			
			lastUpdate = actualTime;
			Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT).show();
			if(color){
				view.setBackgroundColor(Color.GREEN);
			}else{
				view.setBackgroundColor(Color.RED);
			}
			color=!color;
		}
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//register this class as a listener for the orientation and 
		//accelerometer sensors 
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause(){
		//unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
	}
	
	 @SuppressWarnings("unused")
	private void loadSound() {   		  
		 soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);   
	        new Thread() {   
	            public void run() {   
	                try {   
	                    soundPoolMap.put(   
	                            0,   
	                            soundPool.load(getAssets().openFd(   
	                                            "shake_sound_male.mp3"), 1));   
	  
	                    soundPoolMap.put(   
	                            1,   
	                            soundPool.load(getAssets().openFd(   
	                                            "phonering.mp3"), 1));   
	                } catch (IOException e) {   
	                    e.printStackTrace();   
	                }   
	            }   
	        }.start();   
	    }   
	
}
