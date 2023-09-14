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

import kr.robomation.peripheral.Dice;

import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class DiceRoboid extends PeripheralRoboid
{
	private int mReadSensoryFlag;
	private int mXmlReadSensoryFlag;
	private int mJsonReadSensoryFlag;
	private final int[][] mReadBuffer = new int[6][];
	
	static
	{
		System.loadLibrary("dice");
	}
	
	private native int n1(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7);
	
	DiceRoboid(int productId)
	{
		super(6);
		mPeripheralId = 2;
		mProductId = productId;
		
		int productMask = productId << 12;
		setTag(0x80200000 | productMask);
		int[][] buffer = mReadBuffer;
		
		Device device = addDevice(0, SENSOR, Dice.SENSOR_SIGNAL | productMask, "Signal", DataType.INTEGER, 1, 0);
		buffer[0] = (int[])getReadData(device);
		
		device = addDevice(1, SENSOR, Dice.SENSOR_TEMPERATURE | productMask, "Temperature", DataType.INTEGER, 1, 0);
		buffer[1] = (int[])getReadData(device);
		
		device = addDevice(2, SENSOR, Dice.SENSOR_BATTERY | productMask, "Battery", DataType.INTEGER, 1, 0);
		buffer[2] = (int[])getReadData(device);
		
		device = addDevice(3, SENSOR, Dice.SENSOR_ACCELERATION | productMask, "Acceleration", DataType.INTEGER, 3, 0);
		buffer[3] = (int[])getReadData(device);
		
		device = addDevice(4, EVENT, Dice.EVENT_FALL | productMask, "Fall", DataType.INTEGER, 1, -1);
		buffer[4] = (int[])getReadData(device);
		
		device = addDevice(5, EVENT, Dice.EVENT_VALUE | productMask, "Value", DataType.INTEGER, 1, 0);
		buffer[5] = (int[])getReadData(device);
	}

	@Override
	public String getId()
	{
		return Dice.ID;
	}

	@Override
	public String getName()
	{
		return "Dice" + mProductId;
	}

	@Override
	boolean decodeSensorySimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 19) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			mReadSensoryFlag = n1(simulacrum, buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5]);
			mXmlReadSensoryFlag = mReadSensoryFlag;
			mJsonReadSensoryFlag = mReadSensoryFlag;
		}
		for(int i = 0; i < 4; ++i)
			fire(i);
		if((mReadSensoryFlag & 0x02) != 0)
			fire(4);
		if((mReadSensoryFlag & 0x01) != 0)
			fire(5);
		return true;
	}

	@Override
	void notifySensoryDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		for(int i = 0; i < 4; ++i)
			listener.onDeviceDataChanged(devices[i], buffer[i], timestamp);
		if((mReadSensoryFlag & 0x02) != 0)
			listener.onDeviceDataChanged(devices[4], buffer[4], timestamp);
		if((mReadSensoryFlag & 0x01) != 0)
			listener.onDeviceDataChanged(devices[5], buffer[5], timestamp);
	}
	
	@Override
	void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<peripheral><peripheralId>2</peripheralId><productId>");
		sb.append(mProductId);
		sb.append("</productId><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0xf8000000;
		synchronized(mReadLock)
		{
			if((mXmlReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x04000000;
			if((mXmlReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x02000000;
			sb.append(deviceMap);
			sb.append("</dmp><signal>");
			
			int[][] buffer = mReadBuffer;
			sb.append(buffer[0][0]);
			sb.append("</signal><temperature>");
			sb.append(buffer[1][0]);
			sb.append("</temperature><battery>");
			sb.append(buffer[2][0]);
			sb.append("</battery><accelerationX>");
			sb.append(buffer[3][0]);
			sb.append("</accelerationX><accelerationY>");
			sb.append(buffer[3][1]);
			sb.append("</accelerationY><accelerationZ>");
			sb.append(buffer[3][2]);
			sb.append("</accelerationZ>");
			if((deviceMap & 0x04000000) != 0)
			{
				sb.append("<fall>");
				sb.append(buffer[4][0]);
				sb.append("</fall>");
			}
			if((deviceMap & 0x02000000) != 0)
			{
				sb.append("<value>");
				sb.append(buffer[5][0]);
				sb.append("</value>");
			}
			mXmlReadSensoryFlag = 0;
		}
		sb.append("</peripheral>");
	}
	
	@Override
	void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[1,2,");
		sb.append(mProductId);
		sb.append(",");
		sb.append(timestamp);
		sb.append(",");
		
		int deviceMap = 0xf8000000;
		synchronized(mReadLock)
		{
			if((mJsonReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x04000000;
			if((mJsonReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x02000000;
			sb.append(deviceMap);
			
			int[][] buffer = mReadBuffer;
			int[] values;
			int len, j;
			for(int i = 0; i < 4; ++i)
			{
				sb.append(",[");
				values = buffer[i];
				sb.append(values[0]);
				len = values.length;
				for(j = 1; j < len; ++j)
				{
					sb.append(",");
					sb.append(values[j]);
				}
				sb.append("]");
			}
			if((deviceMap & 0x04000000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[4][0]);
				sb.append("]");
			}
			if((deviceMap & 0x02000000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[5][0]);
				sb.append("]");
			}
			mJsonReadSensoryFlag = 0;
		}
		sb.append("]");
	}
}