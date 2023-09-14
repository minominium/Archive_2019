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
abstract class StringDeviceImpl extends DeviceImpl
{
	private final Object mReadLock;
	private String[] mReadData;
	
	StringDeviceImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object readLock, Object fireLock)
	{
		super(parent, uid, name, dataType, dataSize, initialValue, fireLock);
		
		mReadLock = readLock;
		if(dataSize < 0) return;
		
		mReadData = new String[dataSize];
		fill(mReadData, initialValue, 0, dataSize);
	}
	
	void fill(String[] data, Object value, int start, int end)
	{
		if(value instanceof String)
		{
			String v = (String)value;
			for(int i = start; i < end; ++i)
				data[i] = v;
		}
		else if(value instanceof String[])
		{
			String[] v = (String[])value;
			int len = Math.min(end, v.length);
			if(start >= len)
			{
				for(int i = start; i < end; ++i)
					data[i] = "";
			}
			else
			{
				for(int i = start; i < len; ++i)
					data[i] = v[i];
				for(int i = len; i < end; ++i)
					data[i] = "";
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
		if(data instanceof String[])
			return putString((String[])data);
		else if(data instanceof String)
			return putString((String)data);
		return false;
	}
	
	@Override
	public String readString()
	{
		synchronized(mReadLock)
		{
			if(mReadData == null || mReadData.length <= 0) return "";
			return mReadData[0];
		}
	}

	@Override
	public String readString(int index)
	{
		if(index < 0) return "";
		synchronized(mReadLock)
		{
			if(mReadData == null || index >= mReadData.length) return "";
			return mReadData[index];
		}
	}

	@Override
	public int readString(String[] data)
	{
		if(data == null) return 0;
		int datalen = data.length;
		if(datalen <= 0) return 0;
		
		int len = 0;
		synchronized(mReadLock)
		{
			if(mReadData == null) return 0;
			String[] readData = mReadData;
			len = Math.min(readData.length, datalen);
			for(int i = 0; i < len; ++i)
				data[i] = readData[i];
		}
		for(int i = len; i < datalen; ++i)
			data[i] = "";
		return len;
	}
	
	@Override
	boolean putString(String data)
	{
		synchronized(mReadLock)
		{
			if(mReadData == null || mReadData.length <= 0)
			{
				if(mDataSize < 0)
					mReadData = new String[1];
				else
					return false;
			}
			mReadData[0] = data;
			fire();
		}
		return true;
	}

	@Override
	boolean putString(int index, String data)
	{
		if(index < 0) return false;
		synchronized(mReadLock)
		{
			if(mReadData == null)
			{
				if(mDataSize < 0)
				{
					mReadData = new String[index + 1];
					fill(mReadData, mInitialValue, 0, index);
				}
				else
					return false;
			}
			else if(index >= mReadData.length)
			{
				if(mDataSize < 0)
				{
					String[] newData = new String[index + 1];
					String[] readData = mReadData;
					int len = readData.length;
					for(int i = 0; i < len; ++i)
						newData[i] = readData[i];
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
	boolean putString(String[] data)
	{
		if(data == null) return false;
		int datalen = data.length;
		if(datalen <= 0) return false;
		
		synchronized(mReadLock)
		{
			if(mDataSize < 0)
			{
				if(mReadData == null || mReadData.length != datalen)
					mReadData = new String[datalen];
			}
			String[] readData = mReadData;
			if(readData == null) return false;
			int thislen = readData.length;
			if(thislen <= 0) return false;
			int len = Math.min(thislen, datalen);
			for(int i = 0; i < len; ++i)
				readData[i] = data[i];
			for(int i = len; i < thislen; ++i)
				readData[i] = "";
			fire();
		}
		return true;
	}
}