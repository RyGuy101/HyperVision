package com.blogspot.mathjoy.hypervision;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void rotateDim(View v)
	{
		if (HyperView.rotateDim == 3)
		{
			HyperView.rotateDim = 2;
			HyperView.pointPaint.setColor(Color.GREEN);
		} else if (HyperView.rotateDim == 2)
		{
			HyperView.rotateDim = 3;
			HyperView.pointPaint.setColor(Color.RED);
		}
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu)
	//	{
	//		getMenuInflater().inflate(R.menu.main, menu);
	//		return true;
	//	}
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item)
	//	{
	//		int id = item.getItemId();
	//		if (id == R.id.action_settings)
	//		{
	//			return true;
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}
}
