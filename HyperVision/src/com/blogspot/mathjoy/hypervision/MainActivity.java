package com.blogspot.mathjoy.hypervision;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity
{
	HyperView hp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hp = (HyperView) findViewById(R.id.hyperView);
		final RadioGroup rotateDimRG = (RadioGroup) findViewById(R.id.rotateDimRG);
		rotateDimRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				((RadioButton) findViewById(checkedId)).setBackgroundResource(R.drawable.light_gray);
				for (int i = 0; i < rotateDimRG.getChildCount(); i++)
				{
					try
					{
						RadioButton temp = (RadioButton) rotateDimRG.getChildAt(i);
						if (rotateDimRG.getChildAt(i).getId() != checkedId)
						{
							rotateDimRG.getChildAt(i).setBackgroundResource(R.drawable.transparent);
						}
					} catch (Exception e)
					{
					}
				}
				if (checkedId == R.id.rotate3D)
				{
					hp.rotateDim = 3;
				} else if (checkedId == R.id.rotate4D)
				{
					hp.rotateDim = 2;
				}
			}
		});

		final RadioGroup stereo = (RadioGroup) findViewById(R.id.stereoRG);
		stereo.setClickable(true);
		stereo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				((RadioButton) findViewById(checkedId)).setBackgroundResource(R.drawable.light_gray);
				for (int i = 0; i < stereo.getChildCount(); i++)
				{
					try
					{
						if (stereo.getChildAt(i).getId() != checkedId)
						{
							stereo.getChildAt(i).setBackgroundResource(R.drawable.transparent);
						}
					} catch (Exception e)
					{
					}
				}
				if (checkedId == R.id.off3D)
				{
					hp.stereo3D = HyperView.OFF_3D;
					hp.setup = true;
				} else if (checkedId == R.id.redCyan)
				{
					hp.stereo3D = HyperView.RED_CYAN_3D;
					hp.setup = true;
				} else if (checkedId == R.id.crossEye)
				{
					hp.stereo3D = HyperView.CROSS_EYE_3D;
					hp.setup = true;
				} else if (checkedId == R.id.parallel)
				{
					hp.stereo3D = HyperView.PARALLEL_3D;
					hp.setup = true;
				}
			}
		});
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
