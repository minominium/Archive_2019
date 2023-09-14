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

import kr.robomation.physical.Alpha;

import org.json.JSONArray;
import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class AlphaRoboid extends PhysicalRoboid
{
	private int mReadSensoryFlag;
	private int mReadMotoringFlag;
	private int mWriteMotoringFlag;
	private int mXmlReadSensoryFlag;
	private int mXmlReadMotoringFlag;
	private int mJsonReadSensoryFlag;
	private int mJsonReadMotoringFlag;
	private final byte[] mMotoringSimulacrum = new byte[985];
	private final int[][] mReadBuffer = new int[19][];
	private final int[][] mWriteBuffer = new int[9][];
	private boolean mZeroSpeaker;
	
	static
	{
		System.loadLibrary("alpha");
	}
	
	private native int n1(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10, int[] a11);
	private native int n2(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10);
	private native void n3(byte[] a1, int a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10, int[] a11);

	AlphaRoboid()
	{
		super(19, 0x00100000);
		
		int[][] readBuffer = mReadBuffer;
		int[][] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, EFFECTOR, Alpha.EFFECTOR_SPEAKER, "Speaker", DataType.INTEGER, 480, 0);
		readBuffer[0] = (int[])getReadData(device);
		writeBuffer[0] = (int[])getWriteData(device);
		
		device = addDevice(1, EFFECTOR, Alpha.EFFECTOR_VOLUME, "Volume", DataType.INTEGER, 1, 100);
		readBuffer[1] = (int[])getReadData(device);
		writeBuffer[1] = (int[])getWriteData(device);
		
		device = addDevice(2, EFFECTOR, Alpha.EFFECTOR_LIP, "Lip", DataType.INTEGER, 1, 0);
		readBuffer[2] = (int[])getReadData(device);
		writeBuffer[2] = (int[])getWriteData(device);
		
		device = addDevice(3, EFFECTOR, Alpha.EFFECTOR_LEFT_WHEEL, "LeftWheel", DataType.INTEGER, 1, 0);
		readBuffer[3] = (int[])getReadData(device);
		writeBuffer[3] = (int[])getWriteData(device);
		
		device = addDevice(4, EFFECTOR, Alpha.EFFECTOR_RIGHT_WHEEL, "RightWheel", DataType.INTEGER, 1, 0);
		readBuffer[4] = (int[])getReadData(device);
		writeBuffer[4] = (int[])getWriteData(device);
		
		device = addDevice(5, EFFECTOR, Alpha.EFFECTOR_LEFT_EYE, "LeftEye", DataType.INTEGER, 3, 0);
		readBuffer[5] = (int[])getReadData(device);
		writeBuffer[5] = (int[])getWriteData(device);

		device = addDevice(6, EFFECTOR, Alpha.EFFECTOR_RIGHT_EYE, "RightEye", DataType.INTEGER, 3, 0);
		readBuffer[6] = (int[])getReadData(device);
		writeBuffer[6] = (int[])getWriteData(device);

		device = addDevice(7, EFFECTOR, Alpha.EFFECTOR_BUZZER, "Buzzer", DataType.INTEGER, 1, 0);
		readBuffer[7] = (int[])getReadData(device);
		writeBuffer[7] = (int[])getWriteData(device);

		device = addDevice(8, COMMAND, Alpha.COMMAND_PAD_SIZE, "PadSize", DataType.INTEGER, 2, 0);
		readBuffer[8] = (int[])getReadData(device);
		writeBuffer[8] = (int[])getWriteData(device);

		device = addDevice(9, SENSOR, Alpha.SENSOR_LEFT_PROXIMITY, "LeftProximity", DataType.INTEGER, 4, 0);
		readBuffer[9] = (int[])getReadData(device);
		
		device = addDevice(10, SENSOR, Alpha.SENSOR_RIGHT_PROXIMITY, "RightProximity", DataType.INTEGER, 4, 0);
		readBuffer[10] = (int[])getReadData(device);
		
		device = addDevice(11, SENSOR, Alpha.SENSOR_ACCELERATION, "Acceleration", DataType.INTEGER, 3, 0);
		readBuffer[11] = (int[])getReadData(device);
		
		device = addDevice(12, SENSOR, Alpha.SENSOR_POSITION, "Position", DataType.INTEGER, 2, -1);
		readBuffer[12] = (int[])getReadData(device);
		
		device = addDevice(13, SENSOR, Alpha.SENSOR_ORIENTATION, "Orientation", DataType.INTEGER, 1, -200);
		readBuffer[13] = (int[])getReadData(device);
		
		device = addDevice(14, SENSOR, Alpha.SENSOR_LIGHT, "Light", DataType.INTEGER, 1, 0);
		readBuffer[14] = (int[])getReadData(device);
		
		device = addDevice(15, SENSOR, Alpha.SENSOR_TEMPERATURE, "Temperature", DataType.INTEGER, 1, 0);
		readBuffer[15] = (int[])getReadData(device);
		
		device = addDevice(16, SENSOR, Alpha.SENSOR_BATTERY, "Battery", DataType.INTEGER, 1, 0);
		readBuffer[16] = (int[])getReadData(device);
		
		device = addDevice(17, EVENT, Alpha.EVENT_FRONT_OID, "FrontOID", DataType.INTEGER, 1, -1);
		readBuffer[17] = (int[])getReadData(device);
		
		device = addDevice(18, EVENT, Alpha.EVENT_BACK_OID, "BackOID", DataType.INTEGER, 1, -1);
		readBuffer[18] = (int[])getReadData(device);
	}
	
	@Override
	public String getId()
	{
		return Alpha.ID;
	}
	
	@Override
	public String getName()
	{
		return "Alpha";
	}
	
	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case Alpha.EFFECTOR_SPEAKER:
			mZeroSpeaker = false;
			mWriteMotoringFlag |= 0x02;
			break;
		case Alpha.COMMAND_PAD_SIZE:
			mWriteMotoringFlag |= 0x01;
			break;
		}
	}
	
	@Override
	boolean decodeSensorySimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 33) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			mReadSensoryFlag = n1(simulacrum, buffer[9], buffer[10], buffer[11], buffer[12], buffer[13], buffer[14], buffer[15], buffer[16], buffer[17], buffer[18]);
			mXmlReadSensoryFlag = mReadSensoryFlag;
			mJsonReadSensoryFlag = mReadSensoryFlag;
		}
		for(int i = 9; i < 17; ++i)
			fire(i);
		if((mReadSensoryFlag & 0x02) != 0)
			fire(17);
		if((mReadSensoryFlag & 0x01) != 0)
			fire(18);
		return true;
	}
	
	@Override
	void notifySensoryDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		for(int i = 9; i < 17; ++i)
			listener.onDeviceDataChanged(devices[i], buffer[i], timestamp);
		if((mReadSensoryFlag & 0x02) != 0)
			listener.onDeviceDataChanged(devices[17], buffer[17], timestamp);
		if((mReadSensoryFlag & 0x01) != 0)
			listener.onDeviceDataChanged(devices[18], buffer[18], timestamp);
	}
	
	@Override
	boolean decodeMotoringSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 17) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			mReadMotoringFlag = n2(simulacrum, buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], buffer[7], buffer[8]);
			mXmlReadMotoringFlag = mReadMotoringFlag;
			mJsonReadMotoringFlag = mReadMotoringFlag;
		}
		for(int i = 0; i < 8; ++i)
			fire(i);
		if((mReadMotoringFlag & 0x01) != 0)
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
		if((mReadMotoringFlag & 0x01) != 0)
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
		sb.append(Alpha.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0xbfbfc000;
		synchronized(mReadLock)
		{
			if((mXmlReadMotoringFlag & 0x02) != 0)
				deviceMap |= 0x40000000;
			if((mXmlReadMotoringFlag & 0x01) != 0)
				deviceMap |= 0x00400000;
			if((mXmlReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x00002000;
			if((mXmlReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x00001000;
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
				sb.append("<padSizeWidth>");
				values = buffer[8];
				sb.append(values[0]);
				sb.append("</padSizeWidth><padSizeHeight>");
				sb.append(values[1]);
				sb.append("</padSizeHeight>");
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
			sb.append("</rightProximity4><accelerationX>");
			sb.append(buffer[11][0]);
			sb.append("</accelerationX><accelerationY>");
			sb.append(buffer[11][1]);
			sb.append("</accelerationY><accelerationZ>");
			sb.append(buffer[11][2]);
			sb.append("</accelerationZ><positionX>");
			sb.append(buffer[12][0]);
			sb.append("</positionX><positionY>");
			sb.append(buffer[12][1]);
			sb.append("</positionY><orientation>");
			sb.append(buffer[13][0]);
			sb.append("</orientation><light>");
			sb.append(buffer[14][0]);
			sb.append("</light><temperature>");
			sb.append(buffer[15][0]);
			sb.append("</temperature><battery>");
			sb.append(buffer[16][0]);
			sb.append("</battery>");
			if((deviceMap & 0x00002000) != 0)
			{
				sb.append("<frontOID>");
				sb.append(buffer[17][0]);
				sb.append("</frontOID>");
			}
			if((deviceMap & 0x00001000) != 0)
			{
				sb.append("<backOID>");
				sb.append(buffer[18][0]);
				sb.append("</backOID>");
			}
			mXmlReadSensoryFlag = 0;
			mXmlReadMotoringFlag = 0;
		}
		sb.append("</robot>");
		super.encodeXml(sb, timestamp);
	}
	
	@Override
	void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append("[0,'");
		sb.append(Alpha.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		int deviceMap = 0xbfbfc000;
		synchronized(mReadLock)
		{
			if((mJsonReadMotoringFlag & 0x02) != 0)
				deviceMap |= 0x40000000;
			if((mJsonReadMotoringFlag & 0x01) != 0)
				deviceMap |= 0x00400000;
			if((mJsonReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x00002000;
			if((mJsonReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x00001000;
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
				values = buffer[8];
				sb.append(values[0]);
				sb.append(",");
				sb.append(values[1]);
				sb.append("]");
			}
			for(int i = 9; i < 17; ++i)
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
			if((deviceMap & 0x00002000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[17][0]);
				sb.append("]");
			}
			if((deviceMap & 0x00001000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[18][0]);
				sb.append("]");
			}
			mJsonReadSensoryFlag = 0;
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
			if(!Alpha.ID.equals(jsonArray.getString(1))) return false;
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
					mWriteMotoringFlag |= 0x02;
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
					values = buffer[8];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					fire(8);
					mWriteMotoringFlag |= 0x01;
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}