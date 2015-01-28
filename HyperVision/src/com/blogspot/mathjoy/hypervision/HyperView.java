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
	Paint pointPaint = new Paint();
	Paint linePaint = new Paint();
	ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Line> lines = new ArrayList<Line>();
	int dimension = 4;
	int numPoints = (int) Math.pow(2, dimension);
	int size = 50;
	int pan = 200;

	public HyperView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		for (int i = 0; i < numPoints; i++)
		{
			double[] coords = new double[dimension];
			for (int j = 1; j <= dimension; j++)
			{
				if (i % Math.pow(2, j) < Math.pow(2, j - 1))
				{
					coords[dimension - j] = -1;
				} else
				{
					coords[dimension - j] = 1;
				}
			}
			points.add(new Point(coords));
		}
		for (int i = 1; i <= dimension; i++)
		{
			for (int j = 0; j < numPoints; j += Math.pow(2, i))
			{
				for (int k = 0; k < Math.pow(2, i - 1); k++)
				{
					lines.add(new Line(j + k, (int) (j + k + Math.pow(2, i - 1))));
				}
			}
		}
		pointPaint.setColor(Color.RED);
		pointPaint.setStrokeWidth(10);
		linePaint.setColor(Color.GRAY);
		linePaint.setStrokeWidth(5);
	}

	@Override
	protected void onDraw(Canvas c)
	{
		long startTime = System.currentTimeMillis();
		super.onDraw(c);
		drawBackground(c);
		rotate(new int[] { 0, 3 }, 1);
		rotate(new int[] { 1, 3 }, 1);
		//		rotate(new int[] { 2, 3 }, 1);
		rotate(new int[] { 0, 1 }, 1);

		for (Line l : lines)
		{
			drawLine(c, l);
		}
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

	private void drawLine(Canvas c, Line l)
	{
		c.drawLine((float) (pan + size * points.get(l.getStartIndex()).getCoord(0)), (float) (pan + size * points.get(l.getStartIndex()).getCoord(1)), (float) (pan + size * points.get(l.getEndIndex()).getCoord(0)), (float) (pan + size * points.get(l.getEndIndex()).getCoord(1)), linePaint);
	}

	private void drawBackground(Canvas c)
	{
		c.drawColor(Color.BLACK);
	}

	private void drawPoint(Canvas c, Point p)
	{
		c.drawPoint((float) (pan + size * p.getCoord(0)), (float) (pan + size * p.getCoord(1)), pointPaint);
	}

	private void rotateXW(double theta)
	{
		double sin_t = Math.sin(Math.toRadians(theta));
		double cos_t = Math.cos(Math.toRadians(theta));

		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
			double y = point.getCoord(1);
			double z = point.getCoord(2);
			points.get(n).setCoord(1, y * cos_t - z * sin_t);
			points.get(n).setCoord(2, z * cos_t + y * sin_t);
		}
	}

	private void rotateYW(double theta)
	{
		double sin_t = Math.sin(Math.toRadians(theta));
		double cos_t = Math.cos(Math.toRadians(theta));

		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
			double z = point.getCoord(2);
			double x = point.getCoord(0);
			points.get(n).setCoord(2, z * cos_t - x * sin_t);
			points.get(n).setCoord(0, x * cos_t + z * sin_t);
		}
	}

	private void rotateZW(double theta)
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

	private void rotate(int[] axes, double degrees)
	{
		double sin_t = Math.sin(Math.toRadians(degrees));
		double cos_t = Math.cos(Math.toRadians(degrees));
		for (Point point : points)
		{
			double[] coords = point.getCoords();
			int[] affectedAxes = new int[2];
			int index = 0;
			for (int i = 0; i < axes.length + 2; i++)
			{
				boolean match = false;
				for (int j = 0; j < axes.length; j++)
				{
					if (axes[j] == i)
					{
						match = true;
					}
				}
				if (!match)
				{
					affectedAxes[index] = i;
					index++;
				}
			}
			point.setCoord(affectedAxes[0], point.getCoord(affectedAxes[0]) * cos_t - point.getCoord(affectedAxes[1]) * sin_t);
			point.setCoord(affectedAxes[1], point.getCoord(affectedAxes[1]) * cos_t + point.getCoord(affectedAxes[0]) * sin_t);
		}
	}
}
