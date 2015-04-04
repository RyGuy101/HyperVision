package com.blogspot.mathjoy.hypervision;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity
{
	HyperView hp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hp = (HyperView) findViewById(R.id.hyperView);
	}

	public void rotate3D(View v)
	{
		hp.rotateDim = 3;
	}

	public void rotate4D(View v)
	{
		hp.rotateDim = 2;
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
