package com.example.xzy.androiddialview.view;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;


/**
 * 电量自定义 view
 * @author xzy
 */
public class ElectricQuantityView extends View {

    //白色圆弧画笔
    private Paint whiteArcPaint;

    //进度圆弧画笔
    private Paint progressArcPaint;

    //电量百分比画笔
    private Paint powerDataPercentPaint;
    // 状态文本画笔
    private Paint textPaint;
    // 状态文本背景画笔
    private Paint textBgPaint;

    //圆弧矩形范围
    private RectF oval;

    //当前数据
    private float currentData;

    //圆弧经过的角度范围
    private float sweepAngle = 360;
    private String status = "****";


    public ElectricQuantityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        whiteArcPaint = new Paint();
        whiteArcPaint.setAntiAlias(true);
        whiteArcPaint.setColor(Color.parseColor("#F5F5F5"));
        whiteArcPaint.setStyle(Paint.Style.STROKE);

        progressArcPaint = new Paint();
        progressArcPaint.setAntiAlias(true);
        progressArcPaint.setColor(Color.parseColor("#3a84f4"));
        progressArcPaint.setStyle(Paint.Style.STROKE);

        powerDataPercentPaint = new Paint();
        powerDataPercentPaint.setColor(Color.parseColor("#2DDA95"));
        powerDataPercentPaint.setTextSize(37);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);

        textBgPaint = new Paint();
        textBgPaint.setColor(Color.parseColor("#2DDA95"));
        textBgPaint.setAntiAlias(true);
        textBgPaint.setStyle(Paint.Style.FILL);

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
        progressArcPaint.setStrokeWidth(width * (float) 0.1);
        oval = new RectF(width * (float) 0.2, width * (float) 0.2, width * (float) 0.8, width * (float) 0.8);
        setMeasuredDimension(width, width);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteArc(canvas);
        drawProgressArc(canvas);
        drawPowerDataPercent(canvas);
    }

    private void drawWhiteArc(Canvas canvas) {
        canvas.save();
        canvas.drawArc(oval, 180, sweepAngle, false, whiteArcPaint);
    }

    private void drawProgressArc(Canvas canvas) {
        canvas.save();
        int[] colors = {Color.parseColor("#FF6500"), Color.parseColor("#FFD800"), Color.parseColor("#2DDA95"), Color.parseColor("#2DDA95")};
        progressArcPaint.setShader(new SweepGradient(360, 600, colors, new float[]{0.5f, 0.7f, 0.8f,1.0f}));
        canvas.drawArc(oval, 90, sweepAngle * currentData / 360, false, progressArcPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void drawPowerDataPercent(Canvas canvas) {
        canvas.save();
        Rect rect = new Rect();
        DecimalFormat decimalFormat = new DecimalFormat("0");
        String data = decimalFormat.format(100 * currentData / 360) + "%";
        powerDataPercentPaint.getTextBounds(data, 0, data.length(), rect);
        canvas.drawText(data, getWidth() / 2 - rect.width() / 2, (int) (getHeight() * (float) 0.5), powerDataPercentPaint);
        canvas.save();

        RectF rectF1 = new RectF();
        rectF1.left = 420;
        rectF1.right = 300;
        rectF1.top = 430;
        rectF1.bottom = 370;
        canvas.drawRoundRect(rectF1, getWidth() / 2 - rectF1.width() / 2, (int) (getHeight() * (float) 0.5), textBgPaint);
        canvas.save();

        Rect rect2 = new Rect();
        String data2 = status;
        textPaint.getTextBounds(data2, 0, data2.length(), rect2);
        canvas.drawText(data2, getWidth() / 2 - rect2.width() / 2 + 10, (int) (getHeight() * (float) 0.589), textPaint);
        canvas.save();
    }

    public void setPercentData(final float data, TimeInterpolator interpolator) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentData, data);
        valueAnimator.setDuration((long) (Math.abs(currentData - data) * 5));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator1) {
                float value = (float) valueAnimator1.getAnimatedValue();
                currentData = (float) (Math.round(value * 10)) / 10;
                // 边界处理
                if(currentData > 360){
                    currentData = 360;
                }
                if (data > 180f) {
                    status = "Good";
                } else {
                    status = "Low";
                }
                ElectricQuantityView.this.invalidate();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }
}
