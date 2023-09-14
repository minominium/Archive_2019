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

import org.roboid.robot.Device;
import org.roboid.robot.Roboid;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
abstract class DeviceImpl extends NamedElementImpl implements Device
{
	private final Roboid mParent;
	private final int mUid;
	private final int mDataType;
	final int mDataSize;
	final Object mInitialValue;
	private boolean mEvent;
	private boolean mFired;
	private final Object mFireLock;

	DeviceImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object fireLock)
	{
		super(name);
		
		mParent = parent;
		mUid = uid;
		mDataType = dataType;
		mDataSize = dataSize;
		mInitialValue = initialValue;
		mFireLock = fireLock;
	}

	int getUid()
	{
		return mUid;
	}
	
	@Override
	public int getId()
	{
		return mUid & 0xfff00fff;
	}
	
	@Override
	public int getProductId()
	{
		return (mUid & 0x000ff000) >> 12;
	}
	
	@Override
	public int getDataType()
	{
		return mDataType;
	}
	
	@Override
	public int getDataSize()
	{
		return mDataSize;
	}

	Object getWriteData()
	{
		return null;
	}

	abstract Object getReadData();
	abstract boolean putReadData(Object data);
	
	@Override
	public boolean e()
	{
		synchronized(mFireLock)
		{
			return mEvent;
		}
	}

	@Override
	public int read()
	{
		return 0;
	}

	@Override
	public int read(int index)
	{
		return 0;
	}

	@Override
	public int read(int[] data)
	{
		return 0;
	}

	@Override
	public float readFloat()
	{
		return 0.0f;
	}

	@Override
	public float readFloat(int index)
	{
		return 0.0f;
	}

	@Override
	public int readFloat(float[] data)
	{
		return 0;
	}
	
	@Override
	public String readString()
	{
		return "";
	}

	@Override
	public String readString(int index)
	{
		return "";
	}

	@Override
	public int readString(String[] data)
	{
		return 0;
	}
	
	@Override
	public boolean write(int data)
	{
		return false;
	}

	@Override
	public boolean write(int index, int data)
	{
		return false;
	}

	@Override
	public int write(int[] data)
	{
		return 0;
	}
	
	@Override
	public boolean writeFloat(float data)
	{
		return false;
	}

	@Override
	public boolean writeFloat(int index, float data)
	{
		return false;
	}

	@Override
	public int writeFloat(float[] data)
	{
		return 0;
	}

	@Override
	public boolean writeString(String data)
	{
		return false;
	}

	@Override
	public boolean writeString(int index, String data)
	{
		return false;
	}

	@Override
	public int writeString(String[] data)
	{
		return 0;
	}
	
	boolean put(int data)
	{
		return false;
	}
	
	boolean put(int index, int data)
	{
		return false;
	}
	
	boolean put(int[] data)
	{
		return false;
	}
	
	boolean putFloat(float data)
	{
		return false;
	}
	
	boolean putFloat(int index, float data)
	{
		return false;
	}
	
	boolean putFloat(float[] data)
	{
		return false;
	}
	
	boolean putString(String data)
	{
		return false;
	}
	
	boolean putString(int index, String data)
	{
		return false;
	}
	
	boolean putString(String[] data)
	{
		return false;
	}
	
	void fire()
	{
		synchronized(mFireLock)
		{
			mFired = true;
		}
	}
	
	void notifyMotoringDeviceWritten()
	{
		if(mParent != null)
			((RoboidImpl)mParent).onMotoringDeviceWritten(this);
	}
	
	void updateDeviceState()
	{
		synchronized(mFireLock)
		{
			mEvent = mFired;
			mFired = false;
		}
	}
}