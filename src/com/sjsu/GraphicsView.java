
package com.sjsu;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.view.View;


/**
 * 
 * @author John Linford, Shailesh Benake
 * 
 * View class, calculates the correct rotation to place
 * on the shadow, and draws the shadow, sundial and pole
 *
 */
public class GraphicsView extends View
{
	private static final float PI = 3.1415926f;	
	private static final float NOON = 12;		// time when the sun is directly over head, assuming 12pm

	private SundialActivity activity;			// our main activity
	private Date date;							// to get the time
	private float time;

	public GraphicsView (Context c) 
	{
		super(c);
		activity = (SundialActivity) c;

		setFocusable(true);

		date = new Date();
		
		time = (float)date.getHours() + ((float)date.getMinutes() / 100.0f);
	}

	// called every refresh, grabs the correct rotation and draws our sundial, pole and shadow
	@Override
	protected void onDraw(Canvas canvas)
	{
		int x = activity.getWidth() / 2;				// width of phone
		int y = activity.getHeight() / 2;				// height of phone (pixels)
		int sundialRadius = activity.getWidth() / 2;	// radius of sundial
		int poleRadius = 20;							// radius of pole

		// draw the right finger swipe information
		if (activity.isShowing())
			drawInformation(canvas);

		// rotate canvas about the center, based on time and how the phone is titled
		canvas.rotate(getRotation(), x, y);

		// sundial is big circle, pole is center in middle, and shadow is rectangle that is cast
		ArcShape arc = new ArcShape(0, 360);
		ArcShape arc2 = new ArcShape(0, 360);
		ShapeDrawable sundial = new ShapeDrawable(arc);
		ShapeDrawable pole = new ShapeDrawable(arc2);
		ShapeDrawable shadow = new ShapeDrawable();

		// set colours for each shape
		Paint pShadow = shadow.getPaint();
		Paint pSundial = sundial.getPaint();
		Paint pPole = pole.getPaint();
		pSundial.setColor(0xFF003333);
		pPole.setColor(0xFF996600);
		pShadow.setColor(0x66000000);

		// set the bounds of each drawable, have both circle in middle, and have the shadow extend from the pole out to the height
		sundial.setBounds(0, activity.getHeight() / 2 - sundialRadius, activity.getWidth(), activity.getHeight() / 2 + sundialRadius); 
		pole.setBounds(activity.getWidth() / 2 - poleRadius, activity.getHeight() / 2 - poleRadius, activity.getWidth() / 2 + poleRadius, activity.getHeight() / 2 + poleRadius);
		shadow.setBounds(x - poleRadius, y, x + poleRadius, (int) (y - getShadowLength()));

		// draw onto canvas
		sundial.draw(canvas);
		shadow.draw(canvas);
		pole.draw(canvas);

	}

	// gets the rotation based on time and phone orientation
	public float getRotation()
	{
		// get current hour and minute
		time = (float)date.getHours() + ((float)date.getMinutes() / 100.0f);

		// get azimuth (ie direction we need to rotate to face north)
		float azimuth = activity.getAzimuth();

		// add time rotation which is [pi / 2 * sin(time * PI / 12)] in radians, so convert to degrees
		// we assume the sun rises at 6am (Pi/2 rotation) and sets at 6pm (-pi/2 rotation), so that the sun is in the air for 12 hours, and at 12Noon it is directly overhead
		float timeRotation = (float) ( ((PI/2f) * Math.sin(time * PI / NOON)) * (180f/PI) );

		// add the two rotations together to get total
		float rotation = -azimuth - timeRotation;

		return rotation;
	}

	public float getShadowLength()
	{
		float shadowLength;
		float baseLength = activity.getWidth() / 6;			// base length of shadow, we just use the radius of sundial
		

		// height is based on pitch, roll, and time
		// first accounting for pitch, take cosine to get the "x" value along the "surface" of the sundial
		shadowLength = (float) (baseLength * Math.cos(activity.getPitch() * PI/180f)); 
		// same thing for pitch
		shadowLength +=	baseLength * Math.cos(activity.getRoll() * PI/180f); 
		// account for time, the sun will cast longer shadows at 6am/6pm, shortest at noon, so sine(time*pi / hightime)
		shadowLength +=	Math.abs(baseLength * (Math.sin( time * PI / NOON)) );
		
		return shadowLength; 
	}

	// draws the GPS and acelerometer information
	public void drawInformation(Canvas canvas)
	{		
		// orientation parameters
		int azimuth = activity.getAzimuth();
		int pitch = activity.getPitch();
		int roll = activity.getRoll();
		
		// GPS parameters
		float longitude = (float) activity.getLongitude();
		float latitude = (float) activity.getLatitude();
		
		Paint paint = new Paint();
		paint.setColor(0xFFFFFFFF);
		paint.setTextSize(30);
		
		// display orientation
		canvas.drawText("Azimuth: " + azimuth + "\tPitch: " + pitch + "\tRoll: " + roll, 10, 60, paint);
		// display GPS
		canvas.drawText("Latitude: " + latitude + "\tLongitude: " + longitude, 10, 60 + 50, paint);
	}
}