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

import kr.robomation.physical.Albert;

import org.json.JSONArray;
import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class AlbertRoboid extends PhysicalRoboid
{
	private int mReadSensoryFlag;
	private int mReadMotoringFlag;
	private int mWriteMotoringFlag;
	private int mXmlReadSensoryFlag;
	private int mXmlReadMotoringFlag;
	private int mJsonReadSensoryFlag;
	private int mJsonReadMotoringFlag;
	private final byte[] mMotoringSimulacrum = new byte[990];
	private final int[][] mReadBuffer = new int[23][];
	private final int[][] mWriteBuffer = new int[12][];
	private boolean mZeroSpeaker;
	
	static
	{
		System.loadLibrary("albert");
	}
	
	private native int n1(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10, int[] a11);
	private native int n2(byte[] a1, int[] a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10, int[] a11, int[] a12);
	private native void n3(byte[] a1, int a2, int[] a3, int[] a4, int[] a5, int[] a6, int[] a7, int[] a8, int[] a9, int[] a10, int[] a11, int[] a12, int[] a13);

	AlbertRoboid()
	{
		super(23, 0x00200000);
		
		int[][] readBuffer = mReadBuffer;
		int[][] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, EFFECTOR, Albert.EFFECTOR_SPEAKER, "Speaker", DataType.INTEGER, 480, 0);
		readBuffer[0] = (int[])getReadData(device);
		writeBuffer[0] = (int[])getWriteData(device);
		
		device = addDevice(1, EFFECTOR, Albert.EFFECTOR_VOLUME, "Volume", DataType.INTEGER, 1, 100);
		readBuffer[1] = (int[])getReadData(device);
		writeBuffer[1] = (int[])getWriteData(device);
		
		device = addDevice(2, EFFECTOR, Albert.EFFECTOR_LIP, "Lip", DataType.INTEGER, 1, 0);
		readBuffer[2] = (int[])getReadData(device);
		writeBuffer[2] = (int[])getWriteData(device);
		
		device = addDevice(3, EFFECTOR, Albert.EFFECTOR_LEFT_WHEEL, "LeftWheel", DataType.INTEGER, 1, 0);
		readBuffer[3] = (int[])getReadData(device);
		writeBuffer[3] = (int[])getWriteData(device);
		
		device = addDevice(4, EFFECTOR, Albert.EFFECTOR_RIGHT_WHEEL, "RightWheel", DataType.INTEGER, 1, 0);
		readBuffer[4] = (int[])getReadData(device);
		writeBuffer[4] = (int[])getWriteData(device);
		
		device = addDevice(5, EFFECTOR, Albert.EFFECTOR_LEFT_EYE, "LeftEye", DataType.INTEGER, 3, 0);
		readBuffer[5] = (int[])getReadData(device);
		writeBuffer[5] = (int[])getWriteData(device);

		device = addDevice(6, EFFECTOR, Albert.EFFECTOR_RIGHT_EYE, "RightEye", DataType.INTEGER, 3, 0);
		readBuffer[6] = (int[])getReadData(device);
		writeBuffer[6] = (int[])getWriteData(device);
		
		device = addDevice(7, EFFECTOR, Albert.EFFECTOR_BODY_LED, "BodyLED", DataType.INTEGER, 1, 0);
		readBuffer[7] = (int[])getReadData(device);
		writeBuffer[7] = (int[])getWriteData(device);

		device = addDevice(8, EFFECTOR, Albert.EFFECTOR_BUZZER, "Buzzer", DataType.INTEGER, 1, 0);
		readBuffer[8] = (int[])getReadData(device);
		writeBuffer[8] = (int[])getWriteData(device);

		device = addDevice(9, COMMAND, Albert.COMMAND_FRONT_LED, "FrontLED", DataType.INTEGER, 1, 0);
		readBuffer[9] = (int[])getReadData(device);
		writeBuffer[9] = (int[])getWriteData(device);
		
		device = addDevice(10, COMMAND, Albert.COMMAND_PAD_SIZE, "PadSize", DataType.INTEGER, 2, 0);
		readBuffer[10] = (int[])getReadData(device);
		writeBuffer[10] = (int[])getWriteData(device);

		device = addDevice(11, SENSOR, Albert.SENSOR_LEFT_PROXIMITY, "LeftProximity", DataType.INTEGER, 4, 0);
		readBuffer[11] = (int[])getReadData(device);
		
		device = addDevice(12, SENSOR, Albert.SENSOR_RIGHT_PROXIMITY, "RightProximity", DataType.INTEGER, 4, 0);
		readBuffer[12] = (int[])getReadData(device);
		
		device = addDevice(13, SENSOR, Albert.SENSOR_ACCELERATION, "Acceleration", DataType.INTEGER, 3, 0);
		readBuffer[13] = (int[])getReadData(device);
		
		device = addDevice(14, SENSOR, Albert.SENSOR_POSITION, "Position", DataType.INTEGER, 2, -1);
		readBuffer[14] = (int[])getReadData(device);
		
		device = addDevice(15, SENSOR, Albert.SENSOR_ORIENTATION, "Orientation", DataType.INTEGER, 1, -200);
		readBuffer[15] = (int[])getReadData(device);
		
		device = addDevice(16, SENSOR, Albert.SENSOR_LIGHT, "Light", DataType.INTEGER, 1, 0);
		readBuffer[16] = (int[])getReadData(device);
		
		device = addDevice(17, SENSOR, Albert.SENSOR_TEMPERATURE, "Temperature", DataType.INTEGER, 1, 0);
		readBuffer[17] = (int[])getReadData(device);
		
		device = addDevice(18, SENSOR, Albert.SENSOR_BATTERY, "Battery", DataType.INTEGER, 1, 0);
		readBuffer[18] = (int[])getReadData(device);
		
		device = addDevice(19, EVENT, Albert.EVENT_FRONT_OID, "FrontOID", DataType.INTEGER, 1, -1);
		readBuffer[19] = (int[])getReadData(device);
		
		device = addDevice(20, EVENT, Albert.EVENT_BACK_OID, "BackOID", DataType.INTEGER, 1, -1);
		readBuffer[20] = (int[])getReadData(device);
		
		device = addDevice(21, COMMAND, Albert.COMMAND_NOTE, "Note", DataType.INTEGER, 1, 0);
		readBuffer[21] = (int[])getReadData(device);
		writeBuffer[11] = (int[])getWriteData(device);
		
		device = addDevice(22, SENSOR, Albert.SENSOR_SIGNAL_STRENGTH, "SignalStrength", DataType.INTEGER, 1, 0);
		readBuffer[22] = (int[])getReadData(device);
	}
	
	@Override
	public String getId()
	{
		return Albert.ID;
	}
	
	@Override
	public String getName()
	{
		return "Albert";
	}
	
	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case Albert.EFFECTOR_SPEAKER:
			mZeroSpeaker = false;
			mWriteMotoringFlag |= 0x04;
			break;
		case Albert.COMMAND_FRONT_LED:
			mWriteMotoringFlag |= 0x02;
			break;
		case Albert.COMMAND_PAD_SIZE:
			mWriteMotoringFlag |= 0x01;
			break;
		case Albert.COMMAND_NOTE:
			mWriteMotoringFlag |= 0x08;
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
			mReadSensoryFlag = n1(simulacrum, buffer[11], buffer[12], buffer[13], buffer[14], buffer[15], buffer[16], buffer[17], buffer[18], buffer[19], buffer[20]);
			mXmlReadSensoryFlag = mReadSensoryFlag;
			mJsonReadSensoryFlag = mReadSensoryFlag;
		}
		for(int i = 11; i < 19; ++i)
			fire(i);
		if((mReadSensoryFlag & 0x02) != 0)
			fire(19);
		if((mReadSensoryFlag & 0x01) != 0)
			fire(20);
		
		if(simulacrum.length > 41)
		{
			mReadBuffer[22][0] = simulacrum[41];
			fire(22);
		}
		return true;
	}
	
	@Override
	void notifySensoryDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		for(int i = 11; i < 19; ++i)
			listener.onDeviceDataChanged(devices[i], buffer[i], timestamp);
		if((mReadSensoryFlag & 0x02) != 0)
			listener.onDeviceDataChanged(devices[19], buffer[19], timestamp);
		if((mReadSensoryFlag & 0x01) != 0)
			listener.onDeviceDataChanged(devices[20], buffer[20], timestamp);
		listener.onDeviceDataChanged(devices[22], buffer[22], timestamp);
	}
	
	@Override
	boolean decodeMotoringSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 19) return false;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			mReadMotoringFlag = n2(simulacrum, buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], buffer[7], buffer[8], buffer[9], buffer[10]);
			mXmlReadMotoringFlag = mReadMotoringFlag;
			mJsonReadMotoringFlag = mReadMotoringFlag;
		}
		for(int i = 0; i < 9; ++i)
			fire(i);
		if((mReadMotoringFlag & 0x02) != 0)
			fire(9);
		if((mReadMotoringFlag & 0x01) != 0)
			fire(10);
		
		if(simulacrum.length > 989 && (simulacrum[2] & 0x01) != 0)
		{
			mReadBuffer[21][0] = simulacrum[989] & 0xff;
			mReadMotoringFlag |= 0x08;
			mXmlReadMotoringFlag = mReadMotoringFlag;
			mJsonReadMotoringFlag = mReadMotoringFlag;
			fire(21);
		}
		return true;
	}
	
	@Override
	void notifyMotoringDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		for(int i = 0; i < 9; ++i)
			listener.onDeviceDataChanged(devices[i], buffer[i], timestamp);
		if((mReadMotoringFlag & 0x02) != 0)
			listener.onDeviceDataChanged(devices[9], buffer[9], timestamp);
		if((mReadMotoringFlag & 0x01) != 0)
			listener.onDeviceDataChanged(devices[10], buffer[10], timestamp);
		if((mReadMotoringFlag & 0x08) != 0)
			listener.onDeviceDataChanged(devices[21], buffer[21], timestamp);
	}
	
	@Override
	byte[] encodeMotoringSimulacrum()
	{
		byte[] simulacrum = mMotoringSimulacrum;
		synchronized(mWriteLock)
		{
			int[][] buffer = mWriteBuffer;
			n3(simulacrum, mWriteMotoringFlag, buffer[0], buffer[1], buffer[2], buffer[3], buffer[4], buffer[5], buffer[6], buffer[7], buffer[8], buffer[9], buffer[10]);
			
			if((mWriteMotoringFlag & 0x08) != 0)
			{
				simulacrum[2] |= 0x01;
				simulacrum[989] = (byte)(buffer[11][0] & 0xff);
			}
			mWriteMotoringFlag = 0;
		}
		return simulacrum;
	}
	
	@Override
	void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<robot><id>");
		sb.append(Albert.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0xbfcff000;
		synchronized(mReadLock)
		{
			if((mXmlReadMotoringFlag & 0x04) != 0)
				deviceMap |= 0x40000000;
			if((mXmlReadMotoringFlag & 0x02) != 0)
				deviceMap |= 0x00200000;
			if((mXmlReadMotoringFlag & 0x01) != 0)
				deviceMap |= 0x00100000;
			if((mXmlReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x00000800;
			if((mXmlReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x00000400;
			
			if((mXmlReadMotoringFlag & 0x08) != 0)
				deviceMap |= 0x00000100;
			
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
			sb.append("</rightEyeBlue><bodyLED>");
			sb.append(buffer[7][0]);
			sb.append("</bodyLED><buzzer>");
			sb.append(buffer[8][0]);
			sb.append("</buzzer>");
			if((deviceMap & 0x00200000) != 0)
			{
				sb.append("<frontLED>");
				sb.append(buffer[9][0]);
				sb.append("</frontLED>");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				sb.append("<padSizeWidth>");
				values = buffer[10];
				sb.append(values[0]);
				sb.append("</padSizeWidth><padSizeHeight>");
				sb.append(values[1]);
				sb.append("</padSizeHeight>");
			}
			sb.append("<leftProximity1>");
			sb.append(buffer[11][0]);
			sb.append("</leftProximity1><leftProximity2>");
			sb.append(buffer[11][1]);
			sb.append("</leftProximity2><leftProximity3>");
			sb.append(buffer[11][2]);
			sb.append("</leftProximity3><leftProximity4>");
			sb.append(buffer[11][3]);
			sb.append("</leftProximity4><rightProximity1>");
			sb.append(buffer[12][0]);
			sb.append("</rightProximity1><rightProximity2>");
			sb.append(buffer[12][1]);
			sb.append("</rightProximity2><rightProximity3>");
			sb.append(buffer[12][2]);
			sb.append("</rightProximity3><rightProximity4>");
			sb.append(buffer[12][3]);
			sb.append("</rightProximity4><accelerationX>");
			sb.append(buffer[13][0]);
			sb.append("</accelerationX><accelerationY>");
			sb.append(buffer[13][1]);
			sb.append("</accelerationY><accelerationZ>");
			sb.append(buffer[13][2]);
			sb.append("</accelerationZ><positionX>");
			sb.append(buffer[14][0]);
			sb.append("</positionX><positionY>");
			sb.append(buffer[14][1]);
			sb.append("</positionY><orientation>");
			sb.append(buffer[15][0]);
			sb.append("</orientation><light>");
			sb.append(buffer[16][0]);
			sb.append("</light><temperature>");
			sb.append(buffer[17][0]);
			sb.append("</temperature><battery>");
			sb.append(buffer[18][0]);
			sb.append("</battery>");
			if((deviceMap & 0x00000800) != 0)
			{
				sb.append("<frontOID>");
				sb.append(buffer[19][0]);
				sb.append("</frontOID>");
			}
			if((deviceMap & 0x00000400) != 0)
			{
				sb.append("<backOID>");
				sb.append(buffer[20][0]);
				sb.append("</backOID>");
			}
			
			if((deviceMap & 0x00000100) != 0)
			{
				sb.append("<note>");
				sb.append(buffer[21][0]);
				sb.append("</note>");
			}
			sb.append("<signalStrength>");
			sb.append(buffer[22][0]);
			sb.append("</signalStrength>");
			
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
		sb.append(Albert.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		int deviceMap = 0xbfcff000;
		synchronized(mReadLock)
		{
			if((mJsonReadMotoringFlag & 0x04) != 0)
				deviceMap |= 0x40000000;
			if((mJsonReadMotoringFlag & 0x02) != 0)
				deviceMap |= 0x00200000;
			if((mJsonReadMotoringFlag & 0x01) != 0)
				deviceMap |= 0x00100000;
			if((mJsonReadSensoryFlag & 0x02) != 0)
				deviceMap |= 0x00000800;
			if((mJsonReadSensoryFlag & 0x01) != 0)
				deviceMap |= 0x00000400;
			
			if((mJsonReadMotoringFlag & 0x08) != 0)
				deviceMap |= 0x00000100;
			
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
			for(int i = 1; i < 9; ++i)
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
			if((deviceMap & 0x00200000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[9][0]);
				sb.append("]");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				sb.append(",[");
				values = buffer[10];
				sb.append(values[0]);
				sb.append(",");
				sb.append(values[1]);
				sb.append("]");
			}
			for(int i = 11; i < 19; ++i)
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
			if((deviceMap & 0x00000800) != 0)
			{
				sb.append(",[");
				sb.append(buffer[19][0]);
				sb.append("]");
			}
			if((deviceMap & 0x00000400) != 0)
			{
				sb.append(",[");
				sb.append(buffer[20][0]);
				sb.append("]");
			}
			
			if((deviceMap & 0x00000100) != 0)
			{
				sb.append(",[");
				sb.append(buffer[21][0]);
				sb.append("]");
			}
			sb.append(",[");
			sb.append(buffer[22][0]);
			sb.append("]");
			
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
			if(!Albert.ID.equals(jsonArray.getString(1))) return false;
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
				for(int i = 1; i < 9; ++i)
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[i];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					fire(i);
				}
				if((deviceMap & 0x00200000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[9];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					fire(9);
					mWriteMotoringFlag |= 0x02;
				}
				if((deviceMap & 0x00100000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[10];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					fire(10);
					mWriteMotoringFlag |= 0x01;
				}
				
				if((deviceMap & 0x00000100) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[21];
					len = values.length;
					for(j = 0; j < len; ++j)
						values[j] = ja.getInt(j);
					fire(21);
					mWriteMotoringFlag |= 0x08;
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}