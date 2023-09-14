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
abstract class StringMotoringDeviceImpl extends StringDeviceImpl implements MotoringDevice
{
	private final Object mWriteLock;
	private String[] mWriteData;
	
	StringMotoringDeviceImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object readLock, Object writeLock, Object fireLock)
	{
		super(parent, uid, name, dataType, dataSize, initialValue, readLock, fireLock);
		
		mWriteLock = writeLock;
		if(dataSize < 0) return;
		
		mWriteData = new String[dataSize];
		fill(mWriteData, initialValue, 0, dataSize);
	}
	
	@Override
	Object getWriteData()
	{
		return mWriteData;
	}

	@Override
	public boolean writeString(String data)
	{
		synchronized(mWriteLock)
		{
			if(mWriteData == null || mWriteData.length <= 0)
			{
				if(mDataSize < 0)
					mWriteData = new String[1];
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
	public boolean writeString(int index, String data)
	{
		if(index < 0) return false;
		synchronized(mWriteLock)
		{
			if(mWriteData == null)
			{
				if(mDataSize < 0)
				{
					mWriteData = new String[index + 1];
					fill(mWriteData, mInitialValue, 0, index);
				}
				else
					return false;
			}
			else if(index >= mWriteData.length)
			{
				if(mDataSize < 0)
				{
					String[] newData = new String[index + 1];
					String[] writeData = mWriteData;
					int len = writeData.length;
					for(int i = 0; i < len; ++i)
						newData[i] = writeData[i];
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
	public int writeString(String[] data)
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
					mWriteData = new String[datalen];
			}
			String[] writeData = mWriteData;
			if(writeData == null) return 0;
			int thislen = writeData.length;
			if(thislen <= 0) return 0;
			len = Math.min(thislen, datalen);
			for(int i = 0; i < len; ++i)
				writeData[i] = data[i];
			for(int i = len; i < thislen; ++i)
				writeData[i] = "";
			fire();
			notifyMotoringDeviceWritten();
		}
		return len;
	}
}