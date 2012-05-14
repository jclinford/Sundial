
package com.sjsu;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater.Filter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * 
 * @author John Linford, Shailesh Benake
 * 
 * Creates a "Sundial", which is simply a pole that 
 * simulates the shadow that would be cast if it were 
 * an actual physical pole.
 * 
 * ALso acts as the orientation sensor, location sensor, and gesture sensor
 *
 */
public class SundialActivity extends Activity implements SensorEventListener, LocationListener, OnClickListener
{
	private static int WIDTH;				// height of phone in pixels
	private static int HEIGHT;				// width of phone in pixels

	private Display display;				// to get width and height
	private GraphicsView graphicsView;		// view to draw on
	private SensorManager sensorManager;	// to manage orientation sensor
	private Sensor orientationSensor;		// to get tilts and north facing
	private LocationManager locManager;		// to manage GPS
	private View.OnTouchListener gestList;	// performs actions gestures
	private GestureReader gestureReader;	
	private GestureDetector gestureDetector;// listens to gestures

	private int azimuth = 0;
	private int roll = 0;
	private int pitch = 0;

	private double latitude = 0;
	private double longitude = 0;

	private boolean showInfo = false;		// set true by swipe, false by double tap

	// called when activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		graphicsView = new GraphicsView(this);
		setContentView(graphicsView);

		// get screen dimensions
		display = getWindowManager().getDefaultDisplay();
		WIDTH = display.getWidth();
		HEIGHT = display.getHeight();

		// for compass
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// for gps
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		// for gestures
		gestureReader = new GestureReader(this);
		gestureDetector = new GestureDetector(gestureReader);
		gestureDetector.setOnDoubleTapListener(gestureReader);
		gestList = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				return gestureDetector.onTouchEvent(event);
			}
		};

		// attach listeners to view
		graphicsView.setOnClickListener(SundialActivity.this);
		graphicsView.setOnTouchListener(gestList);
	}

	// re add listeners when we resume
	protected void onResume() 
	{
		super.onResume();
		sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	// when the phone is idle, remove listeners
	protected void onPause() 
	{
		super.onPause();
		sensorManager.unregisterListener(this);
	}


	// ============================ //
	// OnClickListener     Methods  //
	// =============================//
	@Override
	public void onClick(View v) 
	{
		//Filter f = (Filter) v.getTag();
		//FilterFullscreenActivity.show(this, input, f);

	}

	// ============================ //
	// SensorEventListener Methods  //
	// =============================//

	// when accelerometer or magnetic field is changed, update parameters
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION)
			return;

		// orientation, all in degrees
		azimuth = (int) event.values[0];
		pitch = (int) event.values[1];
		roll = (int) event.values[2];

		graphicsView.invalidate();            
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}


	// ============================ //
	// LocationListener Methods     //
	// =============================//
	@Override
	public void onLocationChanged(Location location) 
	{
		Log.i("Sundial", "updating location");

		longitude = location.getLongitude();
		latitude = location.getLatitude();
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		Log.i("Sundial", "GPS OFF");		
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		Log.i("Sundial", "GPS ON");	
		longitude = this.getLongitude();
		latitude = this.getLatitude();
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}	



	//============================//
	// ==  GETTERS AND SETTERS == //
	//============================//

	// width in pixels of actual phone
	public int getWidth()
	{
		return WIDTH;
	}

	// height in pixels of actual phone
	public int getHeight()
	{
		return HEIGHT;
	}

	// azimuth based on magnetic field direction
	public int getAzimuth()
	{
		return azimuth;
	}

	// roll of phone
	public int getRoll()
	{
		return roll;
	}

	// pitch of phone
	public int getPitch()
	{
		return pitch;
	}

	// longitude of current location
	public double getLongitude()
	{
		return longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	// return whether we are showing GPS info or not
	public boolean isShowing()
	{
		return showInfo;
	}

	public void setShowing(boolean s)
	{
		showInfo = s;
	}
}
