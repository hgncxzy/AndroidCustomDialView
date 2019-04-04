package com.example.xzy.androiddialview.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.xzy.androiddialview.R;

/**
 * 温度自定义 view
 * @author xzy
 */
public class TemperatureView extends View {

    //白色圆弧画笔
    private Paint whiteArcPaint;

    //温度范围圆弧画笔
    private Paint temperatureArcPaint;

    //温度文本数据画笔
    private Paint temperatureTextDataPaint;

    //天气图标画笔
    private Paint weatherIconPaint;

    //圆弧矩形范围
    private RectF oval;

    //当前数据
    private float currentData;

    //圆弧经过的角度范围
    private float sweepAngle = 180;

    private String temperature = "0.0℃";

    public TemperatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        whiteArcPaint = new Paint();
        whiteArcPaint.setAntiAlias(true);
        whiteArcPaint.setColor(Color.parseColor("#F5F5F5"));
        whiteArcPaint.setStyle(Paint.Style.STROKE);

        temperatureArcPaint = new Paint();
        temperatureArcPaint.setAntiAlias(true);
        temperatureArcPaint.setColor(Color.parseColor("#3a84f4"));
        temperatureArcPaint.setStyle(Paint.Style.STROKE);

        temperatureTextDataPaint = new Paint();
        temperatureTextDataPaint.setColor(Color.parseColor("#2DDA95"));
        temperatureTextDataPaint.setTextSize(45);
        temperatureTextDataPaint.setFakeBoldText(true);

        weatherIconPaint = new Paint();
        weatherIconPaint.setColor(Color.parseColor("#999999"));
        weatherIconPaint.setTextSize(26);

        currentData = 0;

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
        whiteArcPaint.setStrokeWidth(width * (float) 0.1);
        temperatureArcPaint.setStrokeWidth(width * (float) 0.1);
        oval = new RectF(width * (float) 0.2, width * (float) 0.2, width * (float) 0.8, width * (float) 0.8);
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteArc(canvas);
        drawTemperatureArc(canvas);
        drawTemperatureTextData(canvas);
        drawWeatherIcon(canvas);
    }

    private void drawWhiteArc(Canvas canvas) {
        canvas.save();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{60, 5}, 0);
        whiteArcPaint.setPathEffect(dashPathEffect);
        canvas.drawArc(oval, 180, sweepAngle, false, whiteArcPaint);
    }

    private void drawTemperatureArc(Canvas canvas) {
        canvas.save();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{60, 5}, 0);
        temperatureArcPaint.setPathEffect(dashPathEffect);
        int[] colors = {Color.parseColor("#2DDA95"), Color.parseColor("#FFD800"), Color.parseColor("#FF6500")};
        temperatureArcPaint.setShader(new SweepGradient(getWidth() / 2, getHeight() / 2, colors, new float[]{0.6f, 0.8f, 1.0f}));
        canvas.drawArc(oval, 180, sweepAngle * currentData / 180, false, temperatureArcPaint);
    }

    private void drawTemperatureTextData(Canvas canvas) {
        canvas.save();
        Rect rect = new Rect();

        String data = temperature;
        temperatureTextDataPaint.getTextBounds(data, 0, data.length(), rect);
        canvas.drawText(data, getWidth() / 2 - rect.width() / 2, (int) (getHeight() * (float) 0.45), temperatureTextDataPaint);
        canvas.save();
    }

    private void drawWeatherIcon(Canvas canvas) {
        canvas.save();
        Rect startScaleText = new Rect();

        String data1 = "Low";
        weatherIconPaint.getTextBounds(data1, 0, data1.length(), startScaleText);
        int height1 = (int) (getWidth() * 0.3 * Math.cos(Math.PI / 6) / 2 + getWidth() * 0.6 * 0.5 + getWidth() * 0.10);
        int width1 = (int) ((getWidth() * 0.2 + getWidth() * 0.3 * 0.25) - startScaleText.width() - getWidth() * 0.05);
//        canvas.drawText(data1, width1, height1, scaleDataPaint);

        Bitmap weatherIcon1 = BitmapFactory.decodeResource(getResources(), R.mipmap.weather_icon_1);
        canvas.drawBitmap(weatherIcon1, width1, height1, whiteArcPaint);
        weatherIcon1.recycle();
        canvas.save();

        Rect endScaleText = new Rect();
        String data2 = "High";
        weatherIconPaint.getTextBounds(data2, 0, data2.length(), endScaleText);
        int width2 = (int) (getWidth() * 0.8 - getWidth() * 0.3 * 0.25 + getWidth() * 0.035);
        int height2 = (int) (getWidth() * 0.3 * Math.cos(Math.PI / 6) / 2 + getWidth() * 0.6 * 0.5 + getWidth() * 0.10);
//        canvas.drawText(data2, width2, height1, scaleDataPaint);
        canvas.save();

        Bitmap weatherIcon2 = BitmapFactory.decodeResource(getResources(), R.mipmap.weather_icon_2);
        canvas.drawBitmap(weatherIcon2, width2, height2, whiteArcPaint);
        weatherIcon2.recycle();
        canvas.save();
    }

    public void setPercentData(final float data, TimeInterpolator interpolator) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentData, data);
        valueAnimator.setDuration((long) (Math.abs(currentData - data) * 10));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator1) {
                float value = (float) valueAnimator1.getAnimatedValue();
                currentData = (float) (Math.round(value * 10)) / 10;
                temperature = String.valueOf(data / 4.5f) + "℃";
                TemperatureView.this.invalidate();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }
}
