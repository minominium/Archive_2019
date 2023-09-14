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

import kr.robomation.physical.AlbertPop;

import org.json.JSONArray;
import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class AlbertPopRoboid extends PhysicalRoboid
{
	private int mReadMotoringFlag;
	private int mWriteMotoringFlag;
	private int mXmlReadMotoringFlag;
	private int mJsonReadMotoringFlag;
	private final byte[] mMotoringSimulacrum = new byte[977];
	private final int[][] mReadBuffer = new int[13][];
	private final int[][] mWriteBuffer = new int[9][];
	private boolean mZeroSpeaker;
	
	static
	{
		System.loadLibrary("albertpop");
	}
	
	private native int n1(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5);
	private native int n2(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10);
	private native void n3(byte[] a1, int a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10, int[] a11);

	AlbertPopRoboid()
	{
		super(13, 0x00300000);
		
		int[][] readBuffer = mReadBuffer;
		int[][] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, EFFECTOR, AlbertPop.EFFECTOR_SPEAKER, "Speaker", DataType.INTEGER, 480, 0);
		readBuffer[0] = (int[])getReadData(device);
		writeBuffer[0] = (int[])getWriteData(device);
		
		device = addDevice(1, EFFECTOR, AlbertPop.EFFECTOR_VOLUME, "Volume", DataType.INTEGER, 1, 100);
		readBuffer[1] = (int[])getReadData(device);
		writeBuffer[1] = (int[])getWriteData(device);
		
		device = addDevice(2, EFFECTOR, AlbertPop.EFFECTOR_LIP, "Lip", DataType.INTEGER, 1, 0);
		readBuffer[2] = (int[])getReadData(device);
		writeBuffer[2] = (int[])getWriteData(device);
		
		device = addDevice(3, EFFECTOR, AlbertPop.EFFECTOR_LEFT_WHEEL, "LeftWheel", DataType.INTEGER, 1, 0);
		readBuffer[3] = (int[])getReadData(device);
		writeBuffer[3] = (int[])getWriteData(device);
		
		device = addDevice(4, EFFECTOR, AlbertPop.EFFECTOR_RIGHT_WHEEL, "RightWheel", DataType.INTEGER, 1, 0);
		readBuffer[4] = (int[])getReadData(device);
		writeBuffer[4] = (int[])getWriteData(device);
		
		device = addDevice(5, EFFECTOR, AlbertPop.EFFECTOR_LEFT_EYE, "LeftEye", DataType.INTEGER, 3, 0);
		readBuffer[5] = (int[])getReadData(device);
		writeBuffer[5] = (int[])getWriteData(device);

		device = addDevice(6, EFFECTOR, AlbertPop.EFFECTOR_RIGHT_EYE, "RightEye", DataType.INTEGER, 3, 0);
		readBuffer[6] = (int[])getReadData(device);
		writeBuffer[6] = (int[])getWriteData(device);
		
		device = addDevice(7, EFFECTOR, AlbertPop.EFFECTOR_BUZZER, "Buzzer", DataType.INTEGER, 1, 0);
		readBuffer[7] = (int[])getReadData(device);
		writeBuffer[7] = (int[])getWriteData(device);

		device = addDevice(8, COMMAND, AlbertPop.COMMAND_FRONT_LED, "FrontLED", DataType.INTEGER, 1, 0);
		readBuffer[8] = (int[])getReadData(device);
		writeBuffer[8] = (int[])getWriteData(device);
		
		device = addDevice(9, SENSOR, AlbertPop.SENSOR_LEFT_PROXIMITY, "LeftProximity", DataType.INTEGER, 4, 0);
		readBuffer[9] = (int[])getReadData(device);
		
		device = addDevice(10, SENSOR, AlbertPop.SENSOR_RIGHT_PROXIMITY, "RightProximity", DataType.INTEGER, 4, 0);
		readBuffer[10] = (int[])getReadData(device);
		
		device = addDevice(11, SENSOR, AlbertPop.SENSOR_LIGHT, "Light", DataType.INTEGER, 1, 0);
		readBuffer[11] = (int[])getReadData(device);
		
		device = addDevice(12, SENSOR, AlbertPop.SENSOR_BATTERY, "Battery", DataType.INTEGER, 1, 0);
		readBuffer[12] = (int[])getReadData(device);
	}
	
	@Override
	public String getId()
	{
		return AlbertPop.ID;
	}
	
	@Override
	public String getName()
	{
		return "AlbertPop";
	}
	
	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case AlbertPop.EFFECTOR_SPEAKER:
			mZeroSpeaker = false;
			mWriteMotoringFlag |= 0x04;
			break;
		case AlbertPop.COMMAND_FRONT_LED:
			mWriteMotoringFlag |= 0x02;
			break;
		}
	}
	
	@Override
	boolean decodeSensorySimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 13) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			n1(simulacrum, buffer[9], buffer[10], buffer[11], buffer[12]);
		}
		for(int i = 9; i < 13; ++i)
			fire(i);
		return true;
	}
	
	@Override
	void notifySensoryDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		for(int i = 9; i < 13; ++i)
			listener.onDeviceDataChanged(devices[i], buffer[i], timestamp);
	}
	
	@Override
	boolean decodeMotoringSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 16) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			mReadMotoringFlag = n2(simulacrum, buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], buffer[7], buffer[8]);
			mXmlReadMotoringFlag = mReadMotoringFlag;
			mJsonReadMotoringFlag = mReadMotoringFlag;
		}
		for(int i = 0; i < 8; ++i)
			fire(i);
		if((mReadMotoringFlag & 0x02) != 0)
			fire(8);
		return true;
	}
	
	@Override
	void notifyMotoringDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		for(int i = 0; i < 8; ++i)
			listener.onDeviceDataChanged(devices[i], buffer[i], timestamp);
		if((mReadMotoringFlag & 0x02) != 0)
			listener.onDeviceDataChanged(devices[8], buffer[8], timestamp);
	}
	
	@Override
	byte[] encodeMotoringSimulacrum()
	{
		synchronized(mWriteLock)
		{
			int[][] buffer = mWriteBuffer;
			n3(mMotoringSimulacrum, mWriteMotoringFlag, buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], buffer[7], buffer[8]);
			mWriteMotoringFlag = 0;
		}
		return mMotoringSimulacrum;
	}
	
	@Override
	void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<robot><id>");
		sb.append(AlbertPop.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0xbfbc0000;
		synchronized(mReadLock)
		{
			if((mXmlReadMotoringFlag & 0x04) != 0)
				deviceMap |= 0x40000000;
			if((mXmlReadMotoringFlag & 0x02) != 0)
				deviceMap |= 0x00400000;
			sb.append(deviceMap);
			sb.append("</dmp>");
			
			int[][] buffer = mReadBuffer;
			int[] values;
			int i;
			if((deviceMap & 0x40000000) != 0)
			{
				sb.append("<speaker>");
				values = buffer[0];
				sb.append(values[0]);
				for(i = 1; i < 480; ++i)
				{
					sb.append("</speaker><speaker>");
					sb.append(values[i]);
				}
				sb.append("</speaker>");
			}
			sb.append("<volume>");
			sb.append(buffer[1][0]);
			sb.append("</volume><lip>");
			sb.append(buffer[2][0]);
			sb.append("</lip><leftWheel>");
			sb.append(buffer[3][0]);
			sb.append("</leftWheel><rightWheel>");
			sb.append(buffer[4][0]);
			sb.append("</rightWheel><leftEyeRed>");
			sb.append(buffer[5][0]);
			sb.append("</leftEyeRed><leftEyeGreen>");
			sb.append(buffer[5][1]);
			sb.append("</leftEyeGreen><leftEyeBlue>");
			sb.append(buffer[5][2]);
			sb.append("</leftEyeBlue><rightEyeRed>");
			sb.append(buffer[6][0]);
			sb.append("</rightEyeRed><rightEyeGreen>");
			sb.append(buffer[6][1]);
			sb.append("</rightEyeGreen><rightEyeBlue>");
			sb.append(buffer[6][2]);
			sb.append("</rightEyeBlue><buzzer>");
			sb.append(buffer[7][0]);
			sb.append("</buzzer>");
			if((deviceMap & 0x00400000) != 0)
			{
				sb.append("<frontLED>");
				sb.append(buffer[8][0]);
				sb.append("</frontLED>");
			}
			sb.append("<leftProximity1>");
			sb.append(buffer[9][0]);
			sb.append("</leftProximity1><leftProximity2>");
			sb.append(buffer[9][1]);
			sb.append("</leftProximity2><leftProximity3>");
			sb.append(buffer[9][2]);
			sb.append("</leftProximity3><leftProximity4>");
			sb.append(buffer[9][3]);
			sb.append("</leftProximity4><rightProximity1>");
			sb.append(buffer[10][0]);
			sb.append("</rightProximity1><rightProximity2>");
			sb.append(buffer[10][1]);
			sb.append("</rightProximity2><rightProximity3>");
			sb.append(buffer[10][2]);
			sb.append("</rightProximity3><rightProximity4>");
			sb.append(buffer[10][3]);
			sb.append("</rightProximity4><light>");
			sb.append(buffer[11][0]);
			sb.append("</light><battery>");
			sb.append(buffer[12][0]);
			sb.append("</battery>");
			mXmlReadMotoringFlag = 0;
		}
		sb.append("</robot>");
		super.encodeXml(sb, timestamp);
	}
	
	@Override
	void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append("[0,'");
		sb.append(AlbertPop.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		int deviceMap = 0xbfbc0000;
		synchronized(mReadLock)
		{
			if((mJsonReadMotoringFlag & 0x04) != 0)
				deviceMap |= 0x40000000;
			if((mJsonReadMotoringFlag & 0x02) != 0)
				deviceMap |= 0x00400000;
			sb.append(deviceMap);
			
			int[][] buffer = mReadBuffer;
			int[] values;
			int len, j;
			if((deviceMap & 0x40000000) != 0)
			{
				sb.append(",[");
				values = buffer[0];
				sb.append(values[0]);
				for(j = 1; j < 480; ++j)
				{
					sb.append(",");
					sb.append(values[j]);
				}
				sb.append("]");
			}
			for(int i = 1; i < 8; ++i)
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
			if((deviceMap & 0x00400000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[8][0]);
				sb.append("]");
			}
			for(int i = 9; i < 13; ++i)
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
			mJsonReadMotoringFlag = 0;
		}
		sb.append("]");
		super.encodeJson(sb, timestamp);
	}
	
	@Override
	boolean decodeJson(JSONArray jsonArray)
	{
		try
		{
			if(!AlbertPop.ID.equals(jsonArray.getString(1))) return false;
			int deviceMap = jsonArray.getInt(2);
			int[] values;
			int len, j;
			JSONArray ja;
			int index = 3;
			synchronized(mWriteLock)
			{
				int[][] buffer = mWriteBuffer;
				if((deviceMap & 0x40000000) == 0)
				{
					if(mZeroSpeaker == false)
					{
						values = buffer[0];
						len = values.length;
						for(j = 0; j < len; ++j)
							values[j] = 0;
					}
					mZeroSpeaker = true;
					fire(0);
				}
				else
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[0];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					mZeroSpeaker = false;
					fire(0);
					mWriteMotoringFlag |= 0x04;
				}
				for(int i = 1; i < 8; ++i)
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[i];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					fire(i);
				}
				if((deviceMap & 0x00400000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					buffer[8][0] = ja.getInt(0);
					fire(8);
					mWriteMotoringFlag |= 0x02;
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}