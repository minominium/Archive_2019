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

import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;
import org.roboid.robot.Roboid;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public abstract class RoboidImpl extends NamedElementImpl implements Roboid
{
	protected static final int SENSOR = 0;
	protected static final int EFFECTOR = 1;
	protected static final int COMMAND = 2;
	protected static final int EVENT = 3;
	
	private int mTag;
	private final ArrayList<Roboid> mRoboids = new ArrayList<Roboid>();
	protected final ArrayList<DeviceDataChangedListener> mListeners = new ArrayList<DeviceDataChangedListener>();
	protected final Device[] mDevices;
	protected final Object mReadLock = new Object();
	protected final Object mWriteLock = new Object();
	private final Object mFireLock = new Object();

	protected RoboidImpl(int size)
	{
		mDevices = new Device[size];
	}
	
	protected RoboidImpl(int size, int tag)
	{
		mDevices = new Device[size];
		mTag = tag;
	}
	
	protected void setTag(int tag)
	{
		mTag = tag;
	}
	
	protected Device addDevice(int index, int deviceType, int uid, String name, int dataType, int dataSize, Object initialValue)
	{
		if(index < 0 || index >= mDevices.length) return null;
		
		Device device = null;
		switch(deviceType)
		{
		case SENSOR:
			switch(dataType)
			{
			case DataType.INTEGER:
				device = new IntSensorImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mFireLock);
				break;
			case DataType.FLOAT:
				device = new FloatSensorImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mFireLock);
				break;
			case DataType.STRING:
				device = new StringSensorImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mFireLock);
				break;
			}
			break;
		case EFFECTOR:
			switch(dataType)
			{
			case DataType.INTEGER:
				device = new IntEffectorImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mWriteLock, mFireLock);
				break;
			case DataType.FLOAT:
				device = new FloatEffectorImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mWriteLock, mFireLock);
				break;
			case DataType.STRING:
				device = new StringEffectorImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mWriteLock, mFireLock);
				break;
			}
			break;
		case COMMAND:
			switch(dataType)
			{
			case DataType.INTEGER:
				device = new IntCommandImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mWriteLock, mFireLock);
				break;
			case DataType.FLOAT:
				device = new FloatCommandImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mWriteLock, mFireLock);
				break;
			case DataType.STRING:
				device = new StringCommandImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mWriteLock, mFireLock);
				break;
			}
			break;
		case EVENT:
			switch(dataType)
			{
			case DataType.INTEGER:
				device = new IntEventImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mFireLock);
				break;
			case DataType.FLOAT:
				device = new FloatEventImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mFireLock);
				break;
			case DataType.STRING:
				device = new StringEventImpl(this, uid, name, dataType, dataSize, initialValue, mReadLock, mFireLock);
				break;
			}
			break;
		}
		if(device != null)
			mDevices[index] = device;
		return device;
	}
	
	protected void addRoboid(Roboid roboid)
	{
		if(roboid == null) return;
		synchronized(mRoboids)
		{
			if(mRoboids.contains(roboid)) return;
			mRoboids.add(roboid);
		}
		synchronized(mListeners)
		{
			for(DeviceDataChangedListener listener : mListeners)
				roboid.addDeviceDataChangedListener(listener);
		}
	}
	
	protected Object getReadData(Device device)
	{
		if(device == null) return null;
		return ((DeviceImpl)device).getReadData();
	}
	
	protected Object getWriteData(Device device)
	{
		if(device == null) return null;
		return ((DeviceImpl)device).getWriteData();
	}
	
	protected boolean putReadData(Device device, Object data)
	{
		if(device == null) return false;
		return ((DeviceImpl)device).putReadData(data);
	}
	
	@Override
	public Device findDeviceByName(String name)
	{
		if(name == null || name.length() <= 0) return null;
		
		int dot = name.indexOf(".");
		if(dot < 0)
		{
			for(Device device : mDevices)
			{
				if(name.equalsIgnoreCase(device.getName()))
					return device;
			}
		}
		else
		{
			String roboidName = name.substring(0, dot);
			String subName = name.substring(dot + 1);
			synchronized(mRoboids)
			{
				for(Roboid roboid : mRoboids)
				{
					if(roboidName.equalsIgnoreCase(roboid.getName()))
						return roboid.findDeviceByName(subName);
				}
			}
		}
		return null;
	}
	
	@Override
	public Device findDeviceById(int deviceId)
	{
		return findDevice(deviceId);
	}
	
	protected Device findDevice(int uid)
	{
		int tag = uid & 0xfffff000;
		if(tag == mTag)
		{
			for(Device device : mDevices)
			{
				if(((DeviceImpl)device).getUid() == uid)
					return device;
			}
			return null;
		}
		synchronized(mRoboids)
		{
			Device device = null;
			for(Roboid roboid : mRoboids)
			{
				device = ((RoboidImpl)roboid).findDevice(uid);
				if(device != null)
					return device;
			}
		}
		return null;
	}
	
	@Override
	public void addDeviceDataChangedListener(DeviceDataChangedListener listener)
	{
		if(listener == null) return;
		synchronized(mListeners)
		{
			if(!mListeners.contains(listener))
				mListeners.add(listener);
		}
		synchronized(mRoboids)
		{
			for(Roboid roboid : mRoboids)
				roboid.addDeviceDataChangedListener(listener);
		}
	}
	
	@Override
	public void removeDeviceDataChangedListener(DeviceDataChangedListener listener)
	{
		if(listener == null) return;
		synchronized(mListeners)
		{
			mListeners.remove(listener);
		}
		synchronized(mRoboids)
		{
			for(Roboid roboid : mRoboids)
				roboid.removeDeviceDataChangedListener(listener);
		}
	}
	
	@Override
	public void clearDeviceDataChangedListener()
	{
		synchronized(mListeners)
		{
			mListeners.clear();
		}
		synchronized(mRoboids)
		{
			for(Roboid roboid : mRoboids)
				roboid.clearDeviceDataChangedListener();
		}
	}
	
	protected void fire(int index)
	{
		if(index < 0 || index >= mDevices.length) return;
		((DeviceImpl)mDevices[index]).fire();
	}
	
	protected void onMotoringDeviceWritten(Device device)
	{
	}
	
	protected void updateDeviceState()
	{
		for(Device device : mDevices)
			((DeviceImpl)device).updateDeviceState();
		synchronized(mRoboids)
		{
			for(Roboid roboid : mRoboids)
				((RoboidImpl)roboid).updateDeviceState();
		}
	}
}