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
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.example.xzy.androiddialview.R;

/**
 * 湿度自定义 view
 *
 * @author xzy
 */
public class HumidityView extends View {

    //白色圆弧画笔
    private Paint whiteArcPaint;

    //湿度圆弧比例画笔
    private Paint humidityPercentArcPaint;

    //湿度百分比画笔
    private Paint humidityPercentPaint;

    //圆弧矩形范围
    private RectF oval;

    //当前数据
    private float currentData;

    //圆弧经过的角度范围
    private float sweepAngle = 285;


    public HumidityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        whiteArcPaint = new Paint();
        whiteArcPaint.setAntiAlias(true);
        whiteArcPaint.setColor(Color.parseColor("#F5F5F5"));
        whiteArcPaint.setStyle(Paint.Style.STROKE);

        humidityPercentArcPaint = new Paint();
        humidityPercentArcPaint.setAntiAlias(true);
        humidityPercentArcPaint.setColor(Color.parseColor("#3a84f4"));
        humidityPercentArcPaint.setStyle(Paint.Style.STROKE);

        humidityPercentPaint = new Paint();
        humidityPercentPaint.setColor(Color.parseColor("#2DDA95"));
        humidityPercentPaint.setTextSize(45);
        humidityPercentPaint.setFakeBoldText(true);
        humidityPercentPaint.setStyle(Paint.Style.FILL);

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
        humidityPercentArcPaint.setStrokeWidth(width * (float) 0.1);
        oval = new RectF(width * (float) 0.2, width * (float) 0.2, width * (float) 0.8, width * (float) 0.8);
        setMeasuredDimension(width, width);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteArc(canvas);
        drawHumidityPercentArc(canvas);
        drawArrow(canvas);
        drawHumidityPercentData(canvas);
    }

    private void drawWhiteArc(Canvas canvas) {
        canvas.save();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{60, 5}, 0);
        whiteArcPaint.setPathEffect(dashPathEffect);
        canvas.drawArc(oval, 130, sweepAngle, false, whiteArcPaint);
    }


    private void drawHumidityPercentArc(Canvas canvas) {
        if(currentData == 0){
            return;
        }
        canvas.save();
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{60, 5}, 0);
        humidityPercentArcPaint.setPathEffect(dashPathEffect);
        int[] colors = {Color.parseColor("#FF6500"), Color.parseColor("#FFD800"), Color.parseColor("#2DDA95")};
        humidityPercentArcPaint.setShader(new SweepGradient(360, 600, colors, new float[]{0.3f, 0.6f, 1.0f}));
        canvas.drawArc(oval, 130, sweepAngle * currentData / 360, false, humidityPercentArcPaint);
    }


    private void drawArrow(Canvas canvas) {
        canvas.save();
        Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.water_icon);
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        // int newWidth = (int) (getWidth() * 0.2);
        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, null, true);
        canvas.drawBitmap(newBitmap, getWidth() / 2 - newBitmap.getWidth() / 2, getHeight() / 2 - newBitmap.getHeight() / 2 + 200, whiteArcPaint);
        oldBitmap.recycle();
        newBitmap.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void drawHumidityPercentData(Canvas canvas) {
        canvas.save();
        Rect rect = new Rect();
        DecimalFormat decimalFormat = new DecimalFormat("0");
        String data = decimalFormat.format(100 * currentData / 360) + "%";
        humidityPercentPaint.getTextBounds(data, 0, data.length(), rect);
        canvas.drawText(data, getWidth() / 2 - rect.width() / 2, (int) (getHeight() * (float) 0.5), humidityPercentPaint);
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
                HumidityView.this.invalidate();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }
}
