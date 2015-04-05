package com.blogspot.mathjoy.hypervision;

import java.security.Policy.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HyperView extends View implements OnTouchListener
{
	double shift = 0;
	private static final double DEPTH_3D = 1.125;
	private static final double DEPTH_4D = 1.5;
	double frameRate = 60.0;
	Paint pointPaint = new Paint();
	Paint linePaint = new Paint();
	Paint pointPaint2 = new Paint();
	Paint linePaint2 = new Paint();
	ArrayList<Point> originalPoints = new ArrayList<Point>();
	ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Point> points2 = new ArrayList<Point>();
	ArrayList<Line> lines = new ArrayList<Line>();
	int dimension = 4;
	int numPoints = (int) Math.pow(2, dimension);
	double size;
	double panX;
	double panY;
	int currentAngle = 0;
	double totalAngle = 1;
	double xChange = 0;
	double yChange = 0;
	double prevX;
	double prevY;
	double currentX;
	double currentY;
	int down = 0;
	public int rotateDim = 3;
	public static final int OFF_3D = 0;
	public static final int RED_CYAN_3D = 1;
	public static final int CROSS_EYE_3D = 2;
	public static final int PARALLEL_3D = 3;
	int stereo3D = OFF_3D;
	double rotate3DMagnitude = 7;
	double rotate3D;
	double rotate3DAdjust = 0;
	boolean setup = true;
	DrawPoint2 drawPoint2;
	DrawLine2 drawLine2;

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
		}
		for (Point p : originalPoints)
		{
			points.add(p.clone());
		}
		for (Point p : originalPoints)
		{
			points2.add(p.clone());
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
	}

	@Override
	protected void onDraw(Canvas c)
	{
		long startTime = System.currentTimeMillis();
		if (setup)
		{
			setup();
			setup = false;
		}
		super.onDraw(c);
		drawBackground(c);
		double rotateX = down * (-currentX - -prevX) * (c.getWidth() / 1440.0);
		double rotateY = down * (-currentY - -prevY) * (c.getWidth() / 1440.0);
		rotate(new int[] { 1, rotateDim }, rotateX);
		rotate(new int[] { 0, rotateDim }, rotateY);
		points2.clear();
		for (Point p : points)
		{
			points2.add(p.clone());
		}
		rotate2(new int[] { 1, 3 }, rotate3D);
		prevX = currentX;
		prevY = currentY;
		down = 1;
		//			double numAxes = 2;
		//			double angle = totalAngle * (Math.pow(numAxes, 1 / 2.0) / (double) numAxes);

		//		if (currentAngle == 360)
		//		{
		//			currentAngle = 0;
		//			points.clear();
		//			for (Point p : originalPoints)
		//			{
		//				points.add(p.clone());
		//			}
		//			totalAngle = 0;
		//		} else
		//		{
		//			currentAngle += totalAngle;
		//		}

		//				rotate(new int[] { 0, 1 }, currentAngle);
		//		rotate(new int[] { 0, 2 }, totalAngle);
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
		for (Line l : lines)
		{
			drawLine2.doo(c, l);
		}
		for (Point p : points2)
		{
			drawPoint2.doo(c, p);
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

	private void setup()
	{
		if (stereo3D == OFF_3D || stereo3D == RED_CYAN_3D)
		{
			if (this.getWidth() < this.getHeight())
			{
				size = this.getWidth() / 4.0;
			} else
			{
				size = this.getHeight() / 4.0;
			}
			panX = this.getWidth() / 2.0;
			shift = 0;
		} else if (stereo3D == CROSS_EYE_3D || stereo3D == PARALLEL_3D)
		{
			if (this.getWidth() / 2.0 < this.getHeight())
			{
				size = this.getWidth() / 8.0;
				shift = this.getWidth() / 2.0;
			} else
			{
				size = this.getHeight() / 4.0;
				shift = this.getHeight();
			}
			panX = (this.getWidth() - shift) / 2.0;
		}
		panY = this.getHeight() / 2.0;

		if (stereo3D == CROSS_EYE_3D)
		{
			rotate3D = -rotate3DMagnitude;
		} else if (stereo3D != OFF_3D)
		{
			rotate3D = rotate3DMagnitude;
		} else
		{
			rotate3D = 0;
		}
		System.out.println();

		if (stereo3D != OFF_3D)
		{
			drawPoint2 = new DrawPoint2()
			{
				@Override
				public void doo(Canvas c, Point p)
				{
					double m = Math.pow(DEPTH_3D, p.getCoord(2)) * Math.pow(DEPTH_4D, p.getCoord(3) - 1);
					c.drawCircle((float) ((panX + m * size * p.getCoord(0)) + shift), (float) (panY + m * size * p.getCoord(1)), (float) ((Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, p.getCoord(3) - 1) * p.getCoord(2))) * 5), pointPaint2);
				}
			};
			drawLine2 = new DrawLine2()
			{
				@Override
				public void doo(Canvas c, Line l)
				{
					double m1 = Math.pow(DEPTH_3D, points2.get(l.getStartIndex()).getCoord(2)) * Math.pow(DEPTH_4D, points2.get(l.getStartIndex()).getCoord(3) - 1);
					double m2 = Math.pow(DEPTH_3D, points2.get(l.getEndIndex()).getCoord(2)) * Math.pow(DEPTH_4D, points2.get(l.getEndIndex()).getCoord(3) - 1);
					c.drawLine((float) ((panX + m1 * size * points2.get(l.getStartIndex()).getCoord(0)) + shift), (float) (panY + m1 * size * points2.get(l.getStartIndex()).getCoord(1)), (float) ((panX + m2 * size * points2.get(l.getEndIndex()).getCoord(0)) + shift), (float) (panY + m2 * size * points2.get(l.getEndIndex()).getCoord(1)), linePaint2);
				}
			};
		} else
		{
			drawPoint2 = new DrawPoint2()
			{
				@Override
				public void doo(Canvas c, Point p)
				{
				}
			};
			drawLine2 = new DrawLine2()
			{
				@Override
				public void doo(Canvas c, Line l)
				{
				}
			};
		}

		if (stereo3D == RED_CYAN_3D)
		{
			pointPaint.setColor(Color.argb(63, 255, 0, 0));
			linePaint.setColor(Color.argb(63, 255, 0, 0));
			pointPaint2.setColor(Color.argb(63, 0, 255, 255));
			linePaint2.setColor(Color.argb(63, 0, 255, 255));
		} else
		{
			pointPaint.setColor(Color.RED);
			linePaint.setColor(Color.GRAY);
			pointPaint2.setColor(Color.RED);
			linePaint2.setColor(Color.GRAY);
		}
		linePaint.setStrokeWidth(5);
		linePaint2.setStrokeWidth(5);

		rotate(new int[] { 1, 3 }, -rotate3D / 2.0 - rotate3DAdjust);
		rotate3DAdjust = -rotate3D / 2.0;
	}

	private void drawLine(Canvas c, Line l)
	{
		double m1 = Math.pow(DEPTH_3D, points.get(l.getStartIndex()).getCoord(2)) * Math.pow(DEPTH_4D, points.get(l.getStartIndex()).getCoord(3) - 1);
		double m2 = Math.pow(DEPTH_3D, points.get(l.getEndIndex()).getCoord(2)) * Math.pow(DEPTH_4D, points.get(l.getEndIndex()).getCoord(3) - 1);

		c.drawLine((float) (panX + m1 * size * points.get(l.getStartIndex()).getCoord(0)), (float) (panY + m1 * size * points.get(l.getStartIndex()).getCoord(1)), (float) (panX + m2 * size * points.get(l.getEndIndex()).getCoord(0)), (float) (panY + m2 * size * points.get(l.getEndIndex()).getCoord(1)), linePaint);
		//		double width1 = (Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, points.get(l.getStartIndex()).getCoord(3)) * points.get(l.getStartIndex()).getCoord(2))) * 5;
		//		double width2 = (Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, points.get(l.getEndIndex()).getCoord(3)) * points.get(l.getEndIndex()).getCoord(2))) * 5;
		//		double x1 = pan + m1 * size * points.get(l.getStartIndex()).getCoord(0);
		//		double y1 = pan + m1 * size * points.get(l.getStartIndex()).getCoord(1);
		//		double x2 = pan + m2 * size * points.get(l.getEndIndex()).getCoord(0);
		//		double y2 = pan + m2 * size * points.get(l.getEndIndex()).getCoord(1);
		//		double theta = Math.atan((y2 - y1) / (x2 - x1));
		//		Path path = new Path();
		//		path.moveTo((float) (x1 + width1 * Math.sin(theta)), (float) (y1 - width1 * Math.cos(theta)));
		//		path.lineTo((float) (x1 - width1 * Math.sin(theta)), (float) (y1 + width1 * Math.cos(theta)));
		//		path.lineTo((float) (x2 - width2 * Math.sin(theta)), (float) (y2 + width2 * Math.cos(theta)));
		//		path.lineTo((float) (x2 + width2 * Math.sin(theta)), (float) (y2 - width2 * Math.cos(theta)));
		//		c.drawPath(path, linePaint);
	}

	//	private void drawLine2(Canvas c, Line l)
	//	{
	//		double m1 = Math.pow(DEPTH_3D, points2.get(l.getStartIndex()).getCoord(2)) * Math.pow(DEPTH_4D, points2.get(l.getStartIndex()).getCoord(3) - 1);
	//		double m2 = Math.pow(DEPTH_3D, points2.get(l.getEndIndex()).getCoord(2)) * Math.pow(DEPTH_4D, points2.get(l.getEndIndex()).getCoord(3) - 1);
	//
	//		c.drawLine((float) ((panX + m1 * size * points2.get(l.getStartIndex()).getCoord(0)) + shift), (float) (panY + m1 * size * points2.get(l.getStartIndex()).getCoord(1)), (float) ((panX + m2 * size * points2.get(l.getEndIndex()).getCoord(0)) + shift), (float) (panY + m2 * size * points2.get(l.getEndIndex()).getCoord(1)), linePaint2);
	//	}

	private void drawBackground(Canvas c)
	{
		c.drawColor(Color.BLACK);
	}

	private void drawPoint(Canvas c, Point p)
	{
		double m = Math.pow(DEPTH_3D, p.getCoord(2)) * Math.pow(DEPTH_4D, p.getCoord(3) - 1);
		//		pointPaint.setStrokeWidth((float) ((Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, p.getCoord(3)) * p.getCoord(2))) * 10));
		//		c.drawPoint((float) (pan + m * size * p.getCoord(0)), (float) (pan + m * size * p.getCoord(1)), pointPaint);
		c.drawCircle((float) (panX + m * size * p.getCoord(0)), (float) (panY + m * size * p.getCoord(1)), (float) ((Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, p.getCoord(3) - 1) * p.getCoord(2))) * 5), pointPaint);
	}

	//	private void drawPoint2(Canvas c, Point p)
	//	{
	//		double m = Math.pow(DEPTH_3D, p.getCoord(2)) * Math.pow(DEPTH_4D, p.getCoord(3) - 1);
	//		//		pointPaint2.setStrokeWidth((float) ((Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, p.getCoord(3)) * p.getCoord(2))) * 10));
	//		//		c.drawPoint((float) ((pan + m * size * p.getCoord(0)) + shift), (float) (pan + m * size * p.getCoord(1)), pointPaint2);
	//		c.drawCircle((float) ((panX + m * size * p.getCoord(0)) + shift), (float) (panY + m * size * p.getCoord(1)), (float) ((Math.pow(DEPTH_3D, Math.pow(DEPTH_4D, p.getCoord(3) - 1) * p.getCoord(2))) * 5), pointPaint2);
	//	}

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

	private void rotate2(int[] axes, double degrees)
	{
		double sin_t = Math.sin(Math.toRadians(degrees));
		double cos_t = Math.cos(Math.toRadians(degrees));
		for (int n = 0; n < points2.size(); n++)
		{
			Point point = points2.get(n);
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

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() != MotionEvent.ACTION_MOVE)
		{
			down = 0;
		}
		currentX = event.getX();
		currentY = event.getY();
		return true;
	}

	public interface DrawPoint2
	{
		void doo(Canvas c, Point p);
	}

	public interface DrawLine2
	{
		void doo(Canvas c, Line l);
	}
}
