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
abstract class IntDeviceImpl extends DeviceImpl
{
	private final Object mReadLock;
	private int[] mReadData;
	
	IntDeviceImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object readLock, Object fireLock)
	{
		super(parent, uid, name, dataType, dataSize, initialValue, fireLock);
		
		mReadLock = readLock;
		if(dataSize < 0) return;
		
		mReadData = new int[dataSize];
		fill(mReadData, initialValue, 0, dataSize);
	}
	
	void fill(int[] data, Object value, int start, int end)
	{
		if(value instanceof Integer)
		{
			int v = (Integer)value;
			for(int i = start; i < end; ++i)
				data[i] = v;
		}
		else if(value instanceof Float)
		{
			int v = (int)(float)(Float)value;
			for(int i = start; i < end; ++i)
				data[i] = v;
		}
		else if(value instanceof int[])
		{
			int[] v = (int[])value;
			int len = Math.min(end, v.length);
			if(start >= len)
			{
				for(int i = start; i < end; ++i)
					data[i] = 0;
			}
			else
			{
				System.arraycopy(v, start, data, start, len - start);
				for(int i = len; i < end; ++i)
					data[i] = 0;
			}
		}
		else if(value instanceof float[])
		{
			float[] v = (float[])value;
			int len = Math.min(end, v.length);
			if(start >= len)
			{
				for(int i = start; i < end; ++i)
					data[i] = 0;
			}
			else
			{
				for(int i = start; i < len; ++i)
					data[i] = (int)v[i];
				for(int i = len; i < end; ++i)
					data[i] = 0;
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
		if(data instanceof int[])
			return put((int[])data);
		else if(data instanceof float[])
			return putFloat((float[])data);
		else if(data instanceof Integer)
			return put((Integer)data);
		else if(data instanceof Float)
			return putFloat((Float)data);
		return false;
	}
	
	@Override
	public int read()
	{
		synchronized(mReadLock)
		{
			if(mReadData == null || mReadData.length <= 0) return 0;
			return mReadData[0];
		}
	}

	@Override
	public int read(int index)
	{
		if(index < 0) return 0;
		synchronized(mReadLock)
		{
			if(mReadData == null || index >= mReadData.length) return 0;
			return mReadData[index];
		}
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
			len = Math.min(mReadData.length, datalen);
			System.arraycopy(mReadData, 0, data, 0, len);
		}
		for(int i = len; i < datalen; ++i)
			data[i] = 0;
		return len;
	}

	@Override
	public float readFloat()
	{
		return read();
	}

	@Override
	public float readFloat(int index)
	{
		return read(index);
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
			int[] readData = mReadData;
			len = Math.min(readData.length, datalen);
			for(int i = 0; i < len; ++i)
				data[i] = readData[i];
		}
		for(int i = len; i < datalen; ++i)
			data[i] = 0.0f;
		return len;
	}
	
	@Override
	boolean put(int data)
	{
		synchronized(mReadLock)
		{
			if(mReadData == null || mReadData.length <= 0)
			{
				if(mDataSize < 0)
					mReadData = new int[1];
				else
					return false;
			}
			mReadData[0] = data;
			fire();
		}
		return true;
	}
	
	@Override
	boolean put(int index, int data)
	{
		if(index < 0) return false;
		synchronized(mReadLock)
		{
			if(mReadData == null)
			{
				if(mDataSize < 0)
				{
					mReadData = new int[index + 1];
					fill(mReadData, mInitialValue, 0, index);
				}
				else
					return false;
			}
			else if(index >= mReadData.length)
			{
				if(mDataSize < 0)
				{
					int[] newData = new int[index + 1];
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
					mReadData = new int[datalen];
			}
			int[] readData = mReadData;
			if(readData == null) return false;
			int thislen = readData.length;
			if(thislen <= 0) return false;
			int len = Math.min(thislen, datalen);
			System.arraycopy(data, 0, readData, 0, len);
			for(int i = len; i < thislen; ++i)
				readData[i] = 0;
			fire();
		}
		return true;
	}
	
	@Override
	boolean putFloat(float data)
	{
		return put((int)data);
	}

	@Override
	boolean putFloat(int index, float data)
	{
		return put(index, (int)data);
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
					mReadData = new int[datalen];
			}
			int[] readData = mReadData;
			if(readData == null) return false;
			int thislen = readData.length;
			if(thislen <= 0) return false;
			int len = Math.min(thislen, datalen);
			for(int i = 0; i < len; ++i)
				readData[i] = (int)data[i];
			for(int i = len; i < thislen; ++i)
				readData[i] = 0;
			fire();
		}
		return true;
	}
}