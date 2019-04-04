package com.example.xzy.androiddialview.view;

import static java.math.RoundingMode.HALF_UP;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.xzy.androiddialview.R;
import com.example.xzy.androiddialview.util.CalcUtils;

/**
 * 电压自定义 view
 *
 * @author xzy
 */
public class VoltageView extends View {

    //白色圆弧画笔
    private Paint whiteArcPaint;

    //进度圆弧画笔
    private Paint progressArcPaint;

    // 文本画笔
    private Paint textPaint;
    // 文本背景画笔
    private Paint textBgPaint;

    //刻度数据画笔
    private Paint scaleDataPaint;

    //圆弧矩形范围
    private RectF oval;

    //当前数据
    private float currentData;

    //圆弧经过的角度范围
    private float sweepAngle = 180;

    private String percent = "0/0";
    private String percentText = "****";
    private float actualVoltage = 0;


    public VoltageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        whiteArcPaint = new Paint();
        whiteArcPaint.setAntiAlias(true);
        whiteArcPaint.setColor(Color.parseColor("#F5F5F5"));
        whiteArcPaint.setStyle(Paint.Style.STROKE);

        progressArcPaint = new Paint();
        progressArcPaint.setAntiAlias(true);
        progressArcPaint.setColor(Color.parseColor("#3a84f4"));
        progressArcPaint.setStyle(Paint.Style.STROKE);
        // 设置阴影效果
        // progressArcPaint.setShadowLayer((float) 10, (float) 10, (float) 10, Color.parseColor("#99000000"));

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);

        textBgPaint = new Paint();
        textBgPaint.setColor(Color.parseColor("#2DDA95"));
        textBgPaint.setAntiAlias(true);
        textBgPaint.setStyle(Paint.Style.FILL);

        scaleDataPaint = new Paint();
        scaleDataPaint.setColor(Color.parseColor("#999999"));
        scaleDataPaint.setTextSize(26);
        scaleDataPaint.setFakeBoldText(true);

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteArc(canvas);
        drawProgressArc(canvas);
        drawArrow(canvas);
        drawScaleData(canvas, percent);
    }

    private void drawWhiteArc(Canvas canvas) {
        canvas.save();
        canvas.drawArc(oval, 180, sweepAngle, false, whiteArcPaint);
    }


    private void drawProgressArc(Canvas canvas) {
        canvas.save();
        // 设置渐变色效果
        int[] colors = {Color.parseColor("#FF6500"), Color.parseColor("#FFD800"), Color.parseColor("#2DDA95")};
        progressArcPaint.setShader(new SweepGradient(getWidth() / 2, getHeight() / 2, colors, new float[]{0.5f, 0.7f, 0.8f}));
        canvas.drawArc(oval, 180, sweepAngle * currentData / 180, false, progressArcPaint);
    }


    private void drawArrow(Canvas canvas) {
        canvas.save();
        Bitmap oldBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow);
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
//        int newWidth = (int) (getWidth() * 0.26);
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newWidth) / height;
        Matrix matrix = new Matrix();
        matrix.postRotate(-159 + (sweepAngle * currentData / 180), 880, 880);
        matrix.postScale(0.6f, 0.6f, 0, 0);
        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
        canvas.drawBitmap(newBitmap, getWidth() / 2 - newBitmap.getWidth() / 2, getHeight() / 2 - newBitmap.getHeight() / 2,
                whiteArcPaint);
        oldBitmap.recycle();
        newBitmap.recycle();
    }

    private void drawScaleData(Canvas canvas, String data) {
        canvas.save();
        Rect startScaleText = new Rect();
        String data1 = "Low";
        scaleDataPaint.getTextBounds(data1, 0, data1.length(), startScaleText);
        int height1 = (int) (getWidth() * 0.3 * Math.cos(Math.PI / 6) / 2 + getWidth() * 0.6 * 0.5 + getWidth() * 0.12);
        int width1 = (int) ((getWidth() * 0.2 + getWidth() * 0.3 * 0.25) - startScaleText.width() - getWidth() * 0.05);
        canvas.drawText(data1, width1, height1, scaleDataPaint);
        canvas.save();

        Rect endScaleText = new Rect();
        String data2 = "High";
        scaleDataPaint.getTextBounds(data2, 0, data2.length(), endScaleText);
        int width2 = (int) (getWidth() * 0.8 - getWidth() * 0.3 * 0.25 + getWidth() * 0.035);
        canvas.drawText(data2, width2, height1, scaleDataPaint);
        canvas.save();

        // 绘制百分比
        Rect middleScaleText = new Rect();
        scaleDataPaint.getTextBounds(data, 0, data.length(), middleScaleText);
        int width3 = getWidth() / 2 - middleScaleText.width() / 2;
        int height3 = (int) (getWidth() * 0.12);
        canvas.drawText(data, width3, height3, scaleDataPaint);
        canvas.save();

        // 绘制文本背景
        RectF rectF1 = new RectF();
        rectF1.left = 500;
        rectF1.right = 400;
        rectF1.top = 55;
        rectF1.bottom = 105;
        canvas.drawRoundRect(rectF1, getWidth() / 2 - rectF1.width() / 2 + 20, (int) (getHeight() * (float) 0.5), textBgPaint);
        canvas.save();

        Rect rect4 = new Rect();
        String data4 = percentText;
        int width;
        if("0%".equals(percent)){
            width = width3 +90;
        }else{
            width = width3 + 107;
        }
        textPaint.getTextBounds(data4, 0, data4.length(), rect4);
        canvas.drawText(data4, width, height3 + 6, textPaint);
        canvas.save();

        Rect rect5 = new Rect();
        // 通过百分比反推出电压数值 10V ~ 12.8V
        textPaint.getTextBounds(data, 0, data.length(), rect5);
        scaleDataPaint.setTextSize(30);
        scaleDataPaint.setFakeBoldText(true);
        canvas.drawText(actualVoltage+"V", getWidth() / 2 - rect5.width() / 2, (int) (getHeight() * (float) 0.4), scaleDataPaint);
        canvas.save();
    }

    public void setPercentData(float data, TimeInterpolator interpolator) {

        // 实际电压
        actualVoltage = data;

        // 电池的标准电压是 12.8V ,最低电压 是 10V .大于 12.8V  进度显示为 100%。
        // 换算成圆弧数据
        final float finalData;
        if(data >= 12.8){
            finalData = 180.0f;
        }else if(data == 0){
            finalData = 0;
        }
        else{
            finalData = 90 *(data - 10.0f);
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentData, finalData);
        valueAnimator.setDuration((long) (Math.abs(currentData - finalData) * 10));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator1) {
                float value = (float) valueAnimator1.getAnimatedValue();
                Double d1 = CalcUtils.divide(Double.parseDouble(String.valueOf(finalData)), Double.parseDouble(String.valueOf(180)), 2, HALF_UP);


                // 当前绘制的角度
                currentData = (float) (Math.round(value * 10)) / 10;
                // 进度显示
                Double dd = d1 * 100;
                percent = dd.intValue() + "%";

                // 电压高低判断
                if (d1 * 100 >= 50) {
                    percentText = "High";
                } else {
                    percentText = "Low ";
                }
                // 重绘
                VoltageView.this.invalidate();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }
}
