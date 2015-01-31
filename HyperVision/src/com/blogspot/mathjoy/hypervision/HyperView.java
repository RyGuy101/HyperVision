package com.blogspot.mathjoy.hypervision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HyperView extends View
{
	private static final double DEPTH = 1.2;
	double frameRate = 60.0;
	Paint pointPaint = new Paint();
	Paint linePaint = new Paint();
	ArrayList<Point> originalPoints = new ArrayList<Point>();
	ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Line> lines = new ArrayList<Line>();
	int dimension = 4;
	int numPoints = (int) Math.pow(2, dimension);
	int size = 50;
	int pan = 200;
	int currentAngle = 0;

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
			originalPoints.add(new Point(coords));
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
		rotate(new int[] { 0, 3 }, 30);
		rotate(new int[] { 1, 3 }, 30);
		rotate(new int[] { 2, 3 }, 30);
	}

	@Override
	protected void onDraw(Canvas c)
	{
		long startTime = System.currentTimeMillis();
		super.onDraw(c);
		drawBackground(c);
		points.clear();
		for (Point p : originalPoints)
		{
			points.add(p.clone());
		}

		//			double numAxes = 2;
		double totalAngle = 1;
		//			double angle = totalAngle * (Math.pow(numAxes, 1 / 2.0) / (double) numAxes);
		currentAngle += totalAngle;

		if (currentAngle == 360)
		{
			currentAngle = 0;
		}
//				rotate(new int[] { 0, 1 }, currentAngle);
		rotate(new int[] { 0, 2 }, currentAngle);
//				rotate(new int[] { 0, 3 }, currentAngle);
//				rotate(new int[] { 1, 2 }, currentAngle);
		//		rotate(new int[] { 1, 3 }, currentAngle);
		//		rotate(new int[] { 2, 3 }, currentAngle);

		for (Line l : lines)
		{
			drawLine(c, l);
		}
		for (Point p : points)
		{
			drawPoint(c, p);
		}
		long timeTook = System.currentTimeMillis() - startTime;
		if (timeTook < 1000.0 / frameRate)
		{
			try
			{
				Thread.sleep((long) (1000.0 / frameRate - timeTook));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		invalidate();
	}

	private void drawLine(Canvas c, Line l)
	{
		double m1 = Math.pow(DEPTH, points.get(l.getStartIndex()).getCoord(2) + points.get(l.getStartIndex()).getCoord(3));
		double m2 = Math.pow(DEPTH, points.get(l.getEndIndex()).getCoord(2) + points.get(l.getEndIndex()).getCoord(3));

		c.drawLine((float) (pan + m1 * size * points.get(l.getStartIndex()).getCoord(0)), (float) (pan + m1 * size * points.get(l.getStartIndex()).getCoord(1)), (float) (pan + m2 * size * points.get(l.getEndIndex()).getCoord(0)), (float) (pan + m2 * size * points.get(l.getEndIndex()).getCoord(1)), linePaint);
	}

	private void drawBackground(Canvas c)
	{
		c.drawColor(Color.BLACK);
	}

	private void drawPoint(Canvas c, Point p)
	{
		double m = Math.pow(DEPTH, p.getCoord(2) + p.getCoord(3));
		c.drawPoint((float) (pan + m * size * p.getCoord(0)), (float) (pan + m * size * p.getCoord(1)), pointPaint);
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
			points.get(n).setCoord(1, y * Math.cos(Math.toRadians(theta)) - z * Math.sin(Math.toRadians(theta)));
			points.get(n).setCoord(2, z * Math.cos(Math.toRadians(theta)) + y * Math.sin(Math.toRadians(theta)));
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
			point.setCoord(2, z * Math.cos(Math.toRadians(theta)) - x * Math.sin(Math.toRadians(theta)));
			point.setCoord(0, x * Math.cos(Math.toRadians(theta)) + z * Math.sin(Math.toRadians(theta)));
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
			point.setCoord(0, x * Math.cos(Math.toRadians(theta)) - y * Math.sin(Math.toRadians(theta)));
			point.setCoord(1, y * Math.cos(Math.toRadians(theta)) + x * Math.sin(Math.toRadians(theta)));
		}
	}

	private void rotateXY(double theta)
	{
		double sin_t = Math.sin(Math.toRadians(theta));
		double cos_t = Math.cos(Math.toRadians(theta));

		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
			double z = point.getCoord(2);
			double w = point.getCoord(3);
			point.setCoord(2, z * Math.cos(Math.toRadians(theta)) - w * Math.sin(Math.toRadians(theta)));
			point.setCoord(3, w * Math.cos(Math.toRadians(theta)) + z * Math.sin(Math.toRadians(theta)));
		}
	}

	private void rotate(int[] axes, double degrees)
	{
		double sin_t = Math.sin(Math.toRadians(degrees));
		double cos_t = Math.cos(Math.toRadians(degrees));
		for (int n = 0; n < points.size(); n++)
		{
			Point point = points.get(n);
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
			double first = point.getCoord(affectedAxes[0]);
			double second = point.getCoord(affectedAxes[1]);
			point.setCoord(affectedAxes[0], first * cos_t - second * sin_t);
			point.setCoord(affectedAxes[1], second * cos_t + first * sin_t);
		}
	}
}
