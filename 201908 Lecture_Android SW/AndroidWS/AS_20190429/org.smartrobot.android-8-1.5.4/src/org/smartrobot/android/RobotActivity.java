/*
 * Copyright (C) 2011 SmartRobot.ORG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartrobot.android;

import org.roboid.robot.Device;
import org.roboid.robot.Robot;

import android.app.Activity;

public class RobotActivity extends Activity implements SmartRobot.Callback, Device.DeviceDataChangedListener
{
	@Override
	protected void onStart()
	{
		super.onStart();
		SmartRobot.activate(getApplicationContext(), this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		SmartRobot.deactivate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInitialized(Robot robot)
	{
		robot.addDeviceDataChangedListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivated()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeactivated()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisposed()
	{
		finish();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExecute()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStateChanged(int state)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNameChanged(String name)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeviceDataChanged(Device device, Object values, long timestamp)
	{
	}
}