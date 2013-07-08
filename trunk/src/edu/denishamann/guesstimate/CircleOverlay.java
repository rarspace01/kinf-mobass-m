package edu.denishamann.guesstimate;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class CircleOverlay extends Overlay {
	private GeoPoint geoPosition = new GeoPoint(0, 0);

	private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint innerPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

	private int     radius;
	private boolean tappable;

	public CircleOverlay(Context ctx, MapView mv, GeoPoint pos, int radius, int alpha, boolean tappable) {
		super(ctx);

		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
		borderPaint.setARGB(50, 0, 0, alpha);

		innerPaint.setAntiAlias(true);
		innerPaint.setARGB(50, 0, 0, alpha);

		geoPosition = pos;

		Log.d("GM", "Displaydebug: Density: " + ctx.getResources().getDisplayMetrics().density + " X/Y: " + ctx.getResources().getDisplayMetrics().xdpi + "/" + ctx.getResources().getDisplayMetrics().ydpi);

		this.radius = radius;
		this.tappable = tappable;
	}

	@Override
	public void draw(Canvas c, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		Point p = new Point();
		projection.toMapPixels(geoPosition, p);
		float actualRadius = projection.metersToEquatorPixels(radius) * (1 / FloatMath.cos((float) Math.toRadians(geoPosition.getLatitudeE6() / 1e6)));

		c.drawCircle(p.x, p.y, actualRadius, borderPaint);

		if (tappable) {
			c.drawCircle(p.x, p.y, actualRadius, innerPaint);
			c.drawCircle(p.x, p.y, actualRadius * 2 / 3, borderPaint);
			c.drawCircle(p.x, p.y, actualRadius / 3, borderPaint);
			c.drawLine(p.x - actualRadius, p.y, p.x + actualRadius, p.y, borderPaint);
			c.drawLine(p.x, p.y - actualRadius, p.x, p.y + actualRadius, borderPaint);
		}
	}

	public void setRadius(int r) {
		radius = r;
	}

	@Override
	public boolean onSingleTapUp(final MotionEvent event, final MapView mapView) {
		if (tappable) {
			if (tapIsInCircle(event, mapView)) {
				Toast.makeText(mapView.getContext(), "Go Here!", Toast.LENGTH_LONG).show();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onLongPress(final MotionEvent event, final MapView mapView) {
		if (tapIsInCircle(event, mapView)) {
			Toast.makeText(mapView.getContext(), "This point was calculated based on your guesses.", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private boolean tapIsInCircle(final MotionEvent event, final MapView mapView) {
		Projection projection = mapView.getProjection();

		Point geoPoint = new Point();
		projection.toMapPixels(geoPosition, geoPoint);

		Point tapPoint = new Point();
		projection.fromMapPixels((int) event.getX(), (int) event.getY(), tapPoint);

		float actualRadius = projection.metersToEquatorPixels(radius) * scale;

		if ((tapPoint.x < geoPoint.x + actualRadius && tapPoint.x > geoPoint.x - actualRadius)
				&& (tapPoint.y < geoPoint.y + actualRadius && tapPoint.y > geoPoint.y - actualRadius)) {
			return true;
		}

		return false;
	}
}
