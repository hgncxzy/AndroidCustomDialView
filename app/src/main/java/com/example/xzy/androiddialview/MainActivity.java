package com.example.xzy.androiddialview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import com.example.xzy.androiddialview.view.ElectricQuantityView;
import com.example.xzy.androiddialview.view.HumidityView;
import com.example.xzy.androiddialview.view.TemperatureView;
import com.example.xzy.androiddialview.view.VoltageView;

/**
 * Created by xzy
 */
public class MainActivity extends AppCompatActivity {

    private HumidityView mHumidityView;
    private ElectricQuantityView mElectricQuantityView;
    private TemperatureView mTemperatureView;
    private VoltageView mVoltageView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHumidityView = findViewById(R.id.humidity);
        mElectricQuantityView = findViewById(R.id.electricQuantity);
        mTemperatureView = findViewById(R.id.temperature);
        mVoltageView = findViewById(R.id.voltageView);

        mHandler = new Handler();

        // 初始化 HumidityView
        mHumidityView.setPercentData(0,new DecelerateInterpolator());
        // 初始化 TemperatureView
        mTemperatureView.setPercentData(0,new DecelerateInterpolator());
        // 初始化 VoltageView
        mVoltageView.setPercentData(0,new DecelerateInterpolator());
        // 初始化 ElectricQuantity
        mElectricQuantityView.setPercentData(0,new DecelerateInterpolator());
        // 2000 毫秒后更新 UI
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHumidityView.setPercentData(300,new DecelerateInterpolator());
                mTemperatureView.setPercentData(28*4.5f,new DecelerateInterpolator());

                mVoltageView.setPercentData(11.3f,new DecelerateInterpolator());
                mElectricQuantityView.setPercentData(320,new DecelerateInterpolator());
            }
        },2000);
    }
}
