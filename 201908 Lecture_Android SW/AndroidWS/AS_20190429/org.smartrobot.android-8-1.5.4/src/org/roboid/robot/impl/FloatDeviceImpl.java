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

import org.roboid.robot.Roboid;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
abstract class FloatDeviceImpl extends DeviceImpl
{
	private final Object mReadLock;
	private float[] mReadData;
	
	FloatDeviceImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object readLock, Object fireLock)
	{
		super(parent, uid, name, dataType, dataSize, initialValue, fireLock);
		
		mReadLock = readLock;
		if(dataSize < 0) return;
		
		mReadData = new float[dataSize];
		fill(mReadData, initialValue, 0, dataSize);
	}
	
	void fill(float[] data, Object value, int start, int end)
	{
		if(value instanceof Float)
		{
			float v = (Float)value;
			for(int i = start; i < end; ++i)
				data[i] = v;
		}
		else if(value instanceof Integer)
		{
			float v = (Integer)value;
			for(int i = start; i < end; ++i)
				data[i] = v;
		}
		else if(value instanceof float[])
		{
			float[] v = (float[])value;
			int len = Math.min(end, v.length);
			if(start >= len)
			{
				for(int i = start; i < end; ++i)
					data[i] = 0.0f;
			}
			else
			{
				System.arraycopy(v, start, data, start, len - start);
				for(int i = len; i < end; ++i)
					data[i] = 0.0f;
			}
		}
		else if(value instanceof int[])
		{
			int[] v = (int[])value;
			int len = Math.min(end, v.length);
			if(start >= len)
			{
				for(int i = start; i < end; ++i)
					data[i] = 0.0f;
			}
			else
			{
				for(int i = start; i < len; ++i)
					data[i] = v[i];
				for(int i = len; i < end; ++i)
					data[i] = 0.0f;
			}
		}
	}
	
	@Override
	Object getReadData()
	{
		return mReadData;
	}
	
	@Override
	boolean putReadData(Object data)
	{
		if(data instanceof float[])
			return putFloat((float[])data);
		else if(data instanceof int[])
			return put((int[])data);
		else if(data instanceof Float)
			return putFloat((Float)data);
		else if(data instanceof Integer)
			return put((Integer)data);
		return false;
	}
	
	@Override
	public int read()
	{
		return (int)readFloat();
	}

	@Override
	public int read(int index)
	{
		return (int)readFloat(index);
	}

	@Override
	public int read(int[] data)
	{
		if(data == null) return 0;
		int datalen = data.length;
		if(datalen <= 0) return 0;
		
		int len = 0;
		synchronized(mReadLock)
		{
			if(mReadData == null) return 0;
			float[] readData = mReadData;
			len = Math.min(readData.length, datalen);
			for(int i = 0; i < len; ++i)
				data[i] = (int)readData[i];
		}
		for(int i = len; i < datalen; ++i)
			data[i] = 0;
		return len;
	}

	@Override
	public float readFloat()
	{
		synchronized(mReadLock)
		{
			if(mReadData == null || mReadData.length <= 0) return 0.0f;
			return mReadData[0];
		}
	}

	@Override
	public float readFloat(int index)
	{
		if(index < 0) return 0.0f;
		synchronized(mReadLock)
		{
			if(mReadData == null || index >= mReadData.length) return 0.0f;
			return mReadData[index];
		}
	}

	@Override
	public int readFloat(float[] data)
	{
		if(data == null) return 0;
		int datalen = data.length;
		if(datalen <= 0) return 0;
		
		int len = 0;
		synchronized(mReadLock)
		{
			if(mReadData == null) return 0;
			len = Math.min(mReadData.length, datalen);
			System.arraycopy(mReadData, 0, data, 0, len);
		}
		for(int i = len; i < datalen; ++i)
			data[i] = 0.0f;
		return len;
	}
	
	@Override
	boolean put(int data)
	{
		return putFloat((float)data);
	}
	
	@Override
	boolean put(int index, int data)
	{
		return putFloat(index, (float)data);
	}
	
	@Override
	boolean put(int[] data)
	{
		if(data == null) return false;
		int datalen = data.length;
		if(datalen <= 0) return false;
		
		synchronized(mReadLock)
		{
			if(mDataSize < 0)
			{
				if(mReadData == null || mReadData.length != datalen)
					mReadData = new float[datalen];
			}
			float[] readData = mReadData;
			if(readData == null) return false;
			int thislen = readData.length;
			if(thislen <= 0) return false;
			int len = Math.min(thislen, datalen);
			for(int i = 0; i < len; ++i)
				readData[i] = data[i];
			for(int i = len; i < thislen; ++i)
				readData[i] = 0.0f;
			fire();
		}
		return true;
	}
	
	@Override
	boolean putFloat(float data)
	{
		synchronized(mReadLock)
		{
			if(mReadData == null || mReadData.length <= 0)
			{
				if(mDataSize < 0)
					mReadData = new float[1];
				else
					return false;
			}
			mReadData[0] = data;
			fire();
		}
		return true;
	}

	@Override
	boolean putFloat(int index, float data)
	{
		if(index < 0) return false;
		synchronized(mReadLock)
		{
			if(mReadData == null)
			{
				if(mDataSize < 0)
				{
					mReadData = new float[index + 1];
					fill(mReadData, mInitialValue, 0, index);
				}
				else
					return false;
			}
			else if(index >= mReadData.length)
			{
				if(mDataSize < 0)
				{
					float[] newData = new float[index + 1];
					int len = mReadData.length;
					System.arraycopy(mReadData, 0, newData, 0, len);
					fill(newData, mInitialValue, len, index);
					mReadData = newData;
				}
				else
					return false;
			}
			mReadData[index] = data;
			fire();
		}
		return true;
	}

	@Override
	boolean putFloat(float[] data)
	{
		if(data == null) return false;
		int datalen = data.length;
		if(datalen <= 0) return false;

		synchronized(mReadLock)
		{
			if(mDataSize < 0)
			{
				if(mReadData == null || mReadData.length != datalen)
					mReadData = new float[datalen];
			}
			float[] readData = mReadData;
			if(readData == null) return false;
			int thislen = readData.length;
			if(thislen <= 0) return false;
			int len = Math.min(thislen, datalen);
			System.arraycopy(data, 0, readData, 0, len);
			for(int i = len; i < thislen; ++i)
				readData[i] = 0.0f;
			fire();
		}
		return true;
	}
}