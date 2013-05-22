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

public class CircleOverlay extends Overlay {
	private GeoPoint	geoPosition	= new GeoPoint(0, 0);

	private Paint		borderPaint		= new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint		innerPaint		= new Paint(Paint.ANTI_ALIAS_FLAG);

	private int			radius;
	float				scale;

	public CircleOverlay(Context ctx, GeoPoint pos, MapView mv) {
		super(ctx);

		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
		borderPaint.setARGB(50, 0, 0, 155);

		innerPaint.setAntiAlias(true);
		innerPaint.setARGB(50, 0, 0, 155);

		geoPosition = pos;

		scale = ctx.getResources().getDisplayMetrics().density;

		radius = 0;
	}

	@Override
	public void draw(Canvas c, MapView osmv, boolean shadow) {
		Projection projection = osmv.getProjection();
		Point p = new Point();
		projection.toMapPixels(geoPosition, p);
		float actualRadius = projection.metersToEquatorPixels(radius) * scale;

		c.drawCircle(p.x, p.y, actualRadius, borderPaint);
		c.drawCircle(p.x, p.y, actualRadius, innerPaint);
		c.drawCircle(p.x, p.y, actualRadius * 2 / 3, borderPaint);
		c.drawCircle(p.x, p.y, actualRadius / 3, borderPaint);

		c.drawLine(p.x - actualRadius, p.y, p.x + actualRadius, p.y, borderPaint);
		c.drawLine(p.x, p.y - actualRadius, p.x, p.y + actualRadius, borderPaint);
	}

	public void setRadius(int r) {
		radius = r;
	}
}
