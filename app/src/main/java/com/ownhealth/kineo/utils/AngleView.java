package com.ownhealth.kineo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ownhealth.kineo.R;

/**
 * Created by Agustin Madina on 4/23/2018.
 */
public class AngleView extends View {


    private Paint mSectionPaint;

    private RectF mOval = null;
    private int strokeWidth = 10;
    private float mAngle = 270;


    public AngleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        intialize();
    }

    public AngleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        intialize();

    }

    public AngleView(Context context) {
        super(context);
        intialize();
    }

    public void intialize() {

        mSectionPaint = new Paint();
        mSectionPaint.setStyle(Paint.Style.STROKE);
        mSectionPaint.setColor(getResources().getColor(R.color.colorAccent));
        mSectionPaint.setStrokeWidth((float) 2.1 * getResources().getDisplayMetrics().density);
        mSectionPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        strokeWidth = parentWidth * 8 / 100;
        this.setMeasuredDimension(parentWidth, parentWidth * 5 / 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        int mRadius = 260;

        mOval = new RectF(getWidth()/2 - mRadius, getHeight()/2 - mRadius, getWidth()/2 + mRadius, getHeight()/2 + mRadius);

        canvas.drawArc(mOval, 270F, mAngle, true, mSectionPaint);
    }

    public void setAngle(float angle) {
        mAngle = angle;
        invalidate();
        setVisibility(VISIBLE);
    }
}