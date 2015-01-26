package com.blogspot.mathjoy.hypervision;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HyperView extends View
{

	double[] angles = { 0, 0, 45, 135 };
	Paint paint = new Paint();
	ArrayList<Point> points = new ArrayList<Point>();

	public HyperView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		points.add(new Point(new double[] { -1, -1, -1, -1 }));
		points.add(new Point(new double[] { -1, -1, 1, -1 }));
		points.add(new Point(new double[] { -1, 1, -1, -1 }));
		points.add(new Point(new double[] { -1, 1, 1, -1 }));
		points.add(new Point(new double[] { 1, -1, -1, -1 }));
		points.add(new Point(new double[] { 1, -1, 1, -1 }));
		points.add(new Point(new double[] { 1, 1, -1, -1 }));
		points.add(new Point(new double[] { 1, 1, 1, -1 }));

		points.add(new Point(new double[] { -1, -1, -1, 1 }));
		points.add(new Point(new double[] { -1, -1, 1, 1 }));
		points.add(new Point(new double[] { -1, 1, -1, 1 }));
		points.add(new Point(new double[] { -1, 1, 1, 1 }));
		points.add(new Point(new double[] { 1, -1, -1, 1 }));
		points.add(new Point(new double[] { 1, -1, 1, 1 }));
		points.add(new Point(new double[] { 1, 1, -1, 1 }));
		points.add(new Point(new double[] { 1, 1, 1, 1 }));
	}

	@Override
	protected void onDraw(Canvas c)
	{
		long startTime = System.currentTimeMillis();
		super.onDraw(c);
		drawBackground(c);
		rotateX3D(1);
		rotateY3D(1);
		rotateZ3D(1);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(10);
		for (Point p : points)
		{
			drawPoint(c, p);
		}
		long timeTook = System.currentTimeMillis() - startTime;
		if (timeTook < 1000.0 / 60.0)
		{
			try
			{
				Thread.sleep((long) (1000.0 / 60.0 - timeTook));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		invalidate();
	}

	private void drawBackground(Canvas c)
	{
		c.drawColor(Color.BLACK);
	}

	private void drawPoint(Canvas c, Point p)
	{
		int size = 50;
		int pan = 200;
		c.drawPoint((float) (pan + size * p.getCoord(0)), (float) (pan + size * p.getCoord(1)), paint);
	}

	private void rotateX3D(double theta)
	{
		double sin_t = Math.sin(Math.toRadians(theta));
		double cos_t = Math.cos(Math.toRadians(theta));

		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
			double y = point.getCoord(1);
			double z = point.getCoord(2);
			point.setCoord(1, y * cos_t - z * sin_t);
			point.setCoord(2, z * cos_t + y * sin_t);
		}
	}

	private void rotateY3D(double theta)
	{
		double sin_t = Math.sin(Math.toRadians(theta));
		double cos_t = Math.cos(Math.toRadians(theta));

		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
			double x = point.getCoord(0);
			double z = point.getCoord(2);
			point.setCoord(0, x * cos_t - z * sin_t);
			point.setCoord(2, z * cos_t + x * sin_t);
		}
	}

	private void rotateZ3D(double theta)
	{
		double sin_t = Math.sin(Math.toRadians(theta));
		double cos_t = Math.cos(Math.toRadians(theta));

		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
			double x = point.getCoord(0);
			double y = point.getCoord(1);
			point.setCoord(0, x * cos_t - y * sin_t);
			point.setCoord(1, y * cos_t + x * sin_t);
		}
	}
}
