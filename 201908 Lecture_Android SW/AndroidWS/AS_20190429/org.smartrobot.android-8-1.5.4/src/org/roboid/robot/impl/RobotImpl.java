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

package org.roboid.robot.impl;

import java.util.ArrayList;

import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;
import org.roboid.robot.Roboid;
import org.roboid.robot.Robot;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public abstract class RobotImpl extends NamedElementImpl implements Robot
{
	private final ArrayList<Roboid> mRoboids = new ArrayList<Roboid>();
	
	@Override
	protected void setName(String name)
	{
		super.setName(name);
	}

	protected void addRoboid(Roboid roboid)
	{
		if(roboid == null) return;
		if(mRoboids.contains(roboid)) return;
		mRoboids.add(roboid);
	}
	
	protected void updateDeviceState()
	{
		for(Roboid roboid : mRoboids)
			((RoboidImpl)roboid).updateDeviceState();
	}
	
	@Override
	public Device findDeviceById(int deviceId)
	{
		Device device = null;
		for(Roboid roboid : mRoboids)
		{
			device = ((RoboidImpl)roboid).findDevice(deviceId);
			if(device != null)
				return device;
		}
		return null;
	}

	@Override
	public Device findDeviceById(int productId, int deviceId)
	{
		if(productId <= 0 || productId > 255) return null;
		
		Device device = null;
		int uid = (deviceId & 0xfff00fff) | (productId << 12);
		for(Roboid roboid : mRoboids)
		{
			device = ((RoboidImpl)roboid).findDevice(uid);
			if(device != null)
				return device;
		}
		return null;
	}

	@Override
	public void addDeviceDataChangedListener(DeviceDataChangedListener listener)
	{
		if(listener == null) return;
		for(Roboid roboid : mRoboids)
			roboid.addDeviceDataChangedListener(listener);
	}

	@Override
	public void removeDeviceDataChangedListener(DeviceDataChangedListener listener)
	{
		if(listener == null) return;
		for(Roboid roboid : mRoboids)
			roboid.removeDeviceDataChangedListener(listener);
	}
	
	@Override
	public void clearDeviceDataChangedListener()
	{
		for(Roboid roboid : mRoboids)
			roboid.clearDeviceDataChangedListener();
	}
}