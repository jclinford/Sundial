package com.sjsu;

import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;


/**
 * 
 * @author John Linford, Shailesh Benake
 * 
 * Responds to gestures
 * 
 * (Double click hides information)
 * (swipping to the right shows information)
 *
 */
public class GestureReader extends SimpleOnGestureListener implements OnDoubleTapListener
{
	private SundialActivity activity;

	public GestureReader(SundialActivity a)
	{
		activity = a;
	}

	// Gesture listener methods //
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		Log.i("Sundial", "Showing information");
		
		// on right fling, show information
		if (velocityX > 0)
		{
			activity.setShowing(true);

			return true;
		}

		return false;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e)
	{
		return true;
	}
	
	
	
	// Double tap Listener methods //
	@Override
	public boolean onDoubleTap(MotionEvent e) 
	{
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) 
	{
		// on double tap, hide the information
		activity.setShowing(false);
		Log.i("Sundial", "Hiding information");

		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) 
	{
		return false;
	}
}
