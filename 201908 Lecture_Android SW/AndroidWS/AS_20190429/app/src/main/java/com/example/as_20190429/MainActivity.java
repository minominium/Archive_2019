package com.example.as_20190429;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;
import org.smartrobot.android.RobotActivity;

import kr.robomation.physical.Albert;

public class MainActivity extends RobotActivity
{
    private Device leftWheelDevice;
    private Device rightWheelDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onInitialized(Robot robot)
    {
        leftWheelDevice = robot.findDeviceById(Albert.EFFECTOR_LEFT_WHEEL);
        rightWheelDevice = robot.findDeviceById(Albert.EFFECTOR_RIGHT_WHEEL);
    }

    @Override
    public void onExecute()
    {
        leftWheelDevice.write(20);
        rightWheelDevice.write(20);
    }
}
