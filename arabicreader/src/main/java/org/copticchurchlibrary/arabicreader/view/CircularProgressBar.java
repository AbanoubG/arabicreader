package org.copticchurchlibrary.arabicreader.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CircularProgressBar extends ProgressBar {

	public static final String TAG = CircularProgressBar.class.getSimpleName();

	public CircularProgressBar(Context context) {
		this(context, null);
	}

	public CircularProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, org.copticchurchlibrary.arabicreader.R.attr.cpbStyle);
		
	}

	public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if (isInEditMode()) {
			setIndeterminateDrawable(new CircularProgressDrawable.Builder(context).build());
			return;
		}
		
		Resources res = context.getResources();
		TypedArray a = context.obtainStyledAttributes(attrs, org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar, defStyle, 0);

		final int color = a.getColor(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_color, res.getColor(org.copticchurchlibrary.arabicreader.R.color.cpb_default_color));
		final float strokeWidth = a.getDimension(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_stroke_width, res.getDimension(org.copticchurchlibrary.arabicreader.R.dimen.cpb_default_stroke_width));
		final float sweepSpeed = a.getFloat(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_sweep_speed, Float.parseFloat(res.getString(org.copticchurchlibrary.arabicreader.R.string.cpb_default_sweep_speed)));
		final float rotationSpeed = a.getFloat(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_rotation_speed, Float.parseFloat(res.getString(org.copticchurchlibrary.arabicreader.R.string.cpb_default_rotation_speed)));
		final int colorsId = a.getResourceId(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_colors, 0);
		final int minSweepAngle = a.getInteger(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_min_sweep_angle, res.getInteger(org.copticchurchlibrary.arabicreader.R.integer.cpb_default_min_sweep_angle));
		final int maxSweepAngle = a.getInteger(org.copticchurchlibrary.arabicreader.R.styleable.CircularProgressBar_cpb_max_sweep_angle, res.getInteger(org.copticchurchlibrary.arabicreader.R.integer.cpb_default_max_sweep_angle));
		a.recycle();
		
		int[] colors = null;
		// colors
		if (colorsId != 0) {
			colors = res.getIntArray(colorsId);
		}

		Drawable indeterminateDrawable;
		CircularProgressDrawable.Builder builder = new CircularProgressDrawable.Builder(context).sweepSpeed(sweepSpeed).rotationSpeed(rotationSpeed).strokeWidth(strokeWidth)
				.minSweepAngle(minSweepAngle).maxSweepAngle(maxSweepAngle);

		if (colors != null && colors.length > 0)
			builder.colors(colors);
		else
			builder.color(color);

		indeterminateDrawable = builder.build();
		setIndeterminateDrawable(indeterminateDrawable);
	}

	private CircularProgressDrawable checkIndeterminateDrawable() {
		Drawable ret = getIndeterminateDrawable();
		if (ret == null || !(ret instanceof CircularProgressDrawable))
			throw new RuntimeException("The drawable is not a CircularProgressDrawable");
		return (CircularProgressDrawable) ret;
	}

	public void progressiveStop() {
		checkIndeterminateDrawable().progressiveStop();
	}

	public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
		checkIndeterminateDrawable().progressiveStop(listener);
	}
}
