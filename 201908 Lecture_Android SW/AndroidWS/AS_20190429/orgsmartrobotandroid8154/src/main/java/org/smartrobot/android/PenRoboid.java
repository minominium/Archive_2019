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

import kr.robomation.peripheral.Pen;

import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class PenRoboid extends PeripheralRoboid
{
	private int mReadSensoryFlag;
	private int mXmlReadSensoryFlag;
	private int mJsonReadSensoryFlag;
	private final int[][] mReadBuffer = new int[4][];
	private final int[] mEvent = new int[] { -2, -2 };
	
	static
	{
		System.loadLibrary("pen");
	}
	
	private native int n1(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6);
	
	PenRoboid(int productId)
	{
		super(4);
		mPeripheralId = 1;
		mProductId = productId;
		
		int productMask = productId << 12;
		setTag(0x80100000 | productMask);
		int[][] buffer = mReadBuffer;
		
		Device device = addDevice(0, SENSOR, Pen.SENSOR_SIGNAL | productMask, "Signal", DataType.INTEGER, 1, 0);
		buffer[0] = (int[])getReadData(device);
		
		device = addDevice(1, SENSOR, Pen.SENSOR_BATTERY | productMask, "Battery", DataType.INTEGER, 1, 0);
		buffer[1] = (int[])getReadData(device);
		
		device = addDevice(2, EVENT, Pen.EVENT_OID | productMask, "OID", DataType.INTEGER, 1, -1);
		buffer[2] = (int[])getReadData(device);
		
		device = addDevice(3, EVENT, Pen.EVENT_BUTTON | productMask, "Button", DataType.INTEGER, 1, 0);
		buffer[3] = (int[])getReadData(device);
	}
	
	@Override
	public String getId()
	{
		return Pen.ID;
	}

	@Override
	public String getName()
	{
		return "Pen" + mProductId;
	}

	@Override
	boolean decodeSensorySimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 15) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			int[] event = mEvent;
			mReadSensoryFlag = n1(simulacrum, buffer[0], buffer[1], buffer[2], buffer[3], event);
			mXmlReadSensoryFlag = mReadSensoryFlag;
			mJsonReadSensoryFlag = mReadSensoryFlag;
		}
		fire(0);
		fire(1);
		if((mReadSensoryFlag & 0x02) != 0)
			fire(2);
		if((mReadSensoryFlag & 0x01) != 0)
			fire(3);
		return true;
	}

	@Override
	void notifySensoryDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		listener.onDeviceDataChanged(devices[0], buffer[0], timestamp);
		listener.onDeviceDataChanged(devices[1], buffer[1], timestamp);
		if((mReadSensoryFlag & 0x02) != 0)
			listener.onDeviceDataChanged(devices[2], buffer[2], timestamp);
		if((mReadSensoryFlag & 0x01) != 0)
			listener.onDeviceDataChanged(devices[3], buffer[3], timestamp);
	}
	
	@Override
	void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<peripheral><peripheralId>1</peripheralId><productId>");
		sb.append(mProductId);
		sb.append("</productId><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0xe0000000;
		synchronized(mReadLock)
		{
			if((mXmlReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x10000000;
			if((mXmlReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x08000000;
			sb.append(deviceMap);
			sb.append("</dmp><signal>");
			
			int[][] buffer = mReadBuffer;
			sb.append(buffer[0][0]);
			sb.append("</signal><battery>");
			sb.append(buffer[1][0]);
			sb.append("</battery>");
			if((deviceMap & 0x10000000) != 0)
			{
				sb.append("<oid>");
				sb.append(buffer[2][0]);
				sb.append("</oid>");
			}
			if((deviceMap & 0x08000000) != 0)
			{
				sb.append("<button>");
				sb.append(buffer[3][0]);
				sb.append("</button>");
			}
			mXmlReadSensoryFlag = 0;
		}
		sb.append("</peripheral>");
	}
	
	@Override
	void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[1,1,");
		sb.append(mProductId);
		sb.append(",");
		sb.append(timestamp);
		sb.append(",");
		
		int deviceMap = 0xe0000000;
		synchronized(mReadLock)
		{
			if((mJsonReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x10000000;
			if((mJsonReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x08000000;
			sb.append(deviceMap);
			
			int[][] buffer = mReadBuffer;
			for(int i = 0; i < 2; ++i)
			{
				sb.append(",[");
				sb.append(buffer[i][0]);
				sb.append("]");
			}
			if((deviceMap & 0x10000000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[2][0]);
				sb.append("]");
			}
			if((deviceMap & 0x08000000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[3][0]);
				sb.append("]");
			}
			mJsonReadSensoryFlag = 0;
		}
		sb.append("]");
	}
}