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

import org.roboid.robot.MotoringDevice;
import org.roboid.robot.Roboid;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
abstract class FloatMotoringDeviceImpl extends FloatDeviceImpl implements MotoringDevice
{
	private final Object mWriteLock;
	private float[] mWriteData;
	
	FloatMotoringDeviceImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object readLock, Object writeLock, Object fireLock)
	{
		super(parent, uid, name, dataType, dataSize, initialValue, readLock, fireLock);
		
		mWriteLock = writeLock;
		if(dataSize < 0) return;
		
		mWriteData = new float[dataSize];
		fill(mWriteData, initialValue, 0, dataSize);
	}
	
	@Override
	Object getWriteData()
	{
		return mWriteData;
	}

	@Override
	public boolean write(int data)
	{
		return writeFloat((float)data);
	}

	@Override
	public boolean write(int index, int data)
	{
		return writeFloat(index, (float)data);
	}

	@Override
	public int write(int[] data)
	{
		if(data == null) return 0;
		int datalen = data.length;
		if(datalen <= 0) return 0;
		
		int len = 0;
		synchronized(mWriteLock)
		{
			if(mDataSize < 0)
			{
				if(mWriteData == null || mWriteData.length != datalen)
					mWriteData = new float[datalen];
			}
			float[] writeData = mWriteData;
			if(writeData == null) return 0;
			int thislen = writeData.length;
			if(thislen <= 0) return 0;
			len = Math.min(thislen, datalen);
			for(int i = 0; i < len; ++i)
				writeData[i] = data[i];
			for(int i = len; i < thislen; ++i)
				writeData[i] = 0.0f;
			fire();
			notifyMotoringDeviceWritten();
		}
		return len;
	}

	@Override
	public boolean writeFloat(float data)
	{
		synchronized(mWriteLock)
		{
			if(mWriteData == null || mWriteData.length <= 0)
			{
				if(mDataSize < 0)
					mWriteData = new float[1];
				else
					return false;
			}
			mWriteData[0] = data;
			fire();
			notifyMotoringDeviceWritten();
		}
		return true;
	}

	@Override
	public boolean writeFloat(int index, float data)
	{
		if(index < 0) return false;
		synchronized(mWriteLock)
		{
			if(mWriteData == null)
			{
				if(mDataSize < 0)
				{
					mWriteData = new float[index + 1];
					fill(mWriteData, mInitialValue, 0, index);
				}
				else
					return false;
			}
			else if(index >= mWriteData.length)
			{
				if(mDataSize < 0)
				{
					float[] newData = new float[index + 1];
					int len = mWriteData.length;
					System.arraycopy(mWriteData, 0, newData, 0, len);
					fill(newData, mInitialValue, len, index);
					mWriteData = newData;
				}
				else
					return false;
			}
			mWriteData[index] = data;
			fire();
			notifyMotoringDeviceWritten();
		}
		return true;
	}

	@Override
	public int writeFloat(float[] data)
	{
		if(data == null) return 0;
		int datalen = data.length;
		if(datalen <= 0) return 0;
		
		int len = 0;
		synchronized(mWriteLock)
		{
			if(mDataSize < 0)
			{
				if(mWriteData == null || mWriteData.length != datalen)
					mWriteData = new float[datalen];
			}
			float[] writeData = mWriteData;
			if(writeData == null) return 0;
			int thislen = writeData.length;
			if(thislen <= 0) return 0;
			len = Math.min(thislen, datalen);
			System.arraycopy(data, 0, writeData, 0, len);
			for(int i = len; i < thislen; ++i)
				writeData[i] = 0.0f;
			fire();
			notifyMotoringDeviceWritten();
		}
		return len;
	}
}