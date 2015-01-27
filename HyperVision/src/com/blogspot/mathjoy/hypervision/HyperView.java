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
	Paint paint = new Paint();
	ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Line> lines = new ArrayList<Line>();

	public HyperView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		int dimension = 3;
		int numPoints = (int) Math.pow(2, dimension);
		for (int i = 0; i < numPoints; i++)
		{
			double[] coords = new double[dimension];
			for (int j = 1; j <= dimension; j++)
			{
				if (i % Math.pow(2, j) < Math.pow(2, j - 1))
				{
					coords[3 - j] = -1;
				} else
				{
					coords[3 - j] = 1;
				}
			}
			points.add(new Point(coords));
		}
		for (int i = 0; i < 8; i += 2)
		{
			int next = i + 1;
			if (next == 8)
			{
				next = 0;
			}
			lines.add(new Line(points.get(i), points.get(next)));
		}
		for (int i = 0; i < 4; i += 2)
		{
			int next = i + 1;
			if (next == 4)
			{
				next = 0;
			}
			lines.add(new Line(lines.get(i).getStart(), lines.get(i).getStart()));
			lines.add(new Line(lines.get(i).getEnd(), lines.get(i).getEnd()));
		}
	}

	@Override
	protected void onDraw(Canvas c)
	{
		long startTime = System.currentTimeMillis();
		super.onDraw(c);
		drawBackground(c);
		rotateX3D(2);
		rotateY3D(1);
		//rotateZ3D(1);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(10);
		for (Point p : points)
		{
			drawPoint(c, p);
		}
		lines.clear();
		for (int i = 0; i < 8; i += 2)
		{
			lines.add(new Line(points.get(i), points.get(i + 1)));
		}
		for (int i = 0; i < 8; i += 4)
		{
			lines.add(new Line(points.get(i), points.get(i + 2)));
			lines.add(new Line(points.get(i + 1), points.get(i + 3)));
		}
		lines.add(new Line(points.get(0), points.get(4)));
		lines.add(new Line(points.get(1), points.get(5)));
		lines.add(new Line(points.get(2), points.get(6)));
		lines.add(new Line(points.get(3), points.get(7)));

		for (Line l : lines)
		{
			int size = 50;
			int pan = 200;
			c.drawLine((float) (pan + size * l.getStart().getCoord(0)), (float) (pan + size * l.getStart().getCoord(1)), (float) (pan + size * l.getEnd().getCoord(0)), (float) (pan + size * l.getEnd().getCoord(1)), paint);
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
