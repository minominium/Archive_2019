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

package org.smartrobot.android.action;

import org.json.JSONArray;
import org.roboid.robot.DataType;
import org.roboid.robot.Device;
import org.roboid.robot.Device.DeviceDataChangedListener;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class LocalizationAction extends AbstractAction
{
	private int mReadFlag;
	private int mWriteFlag;
	private int mXmlReadFlag;
	private int mJsonReadFlag;
	private final byte[] mSimulacrum = new byte[14];
	private final int[][] mReadBuffer = new int[3][];
	private final int[][] mWriteBuffer = new int[2][];
	
	LocalizationAction()
	{
		super(3, 0x40700000);
		
		int[][] readBuffer = mReadBuffer;
		int[][] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, COMMAND, Action.Localization.COMMAND_PAD_SIZE, "PadSize", DataType.INTEGER, 2, 0);
		readBuffer[0] = (int[])getReadData(device);
		writeBuffer[0] = (int[])getWriteData(device);
		
		device = addDevice(1, COMMAND, Action.Localization.COMMAND_OID, "OID", DataType.INTEGER, 1, -1);
		readBuffer[1] = (int[])getReadData(device);
		writeBuffer[1] = (int[])getWriteData(device);
		
		device = addDevice(2, SENSOR, Action.Localization.SENSOR_POSITION, "Position", DataType.INTEGER, 2, -1);
		readBuffer[2] = (int[])getReadData(device);
	}

	@Override
	public String getId()
	{
		return Action.Localization.ID;
	}
	
	@Override
	public String getName()
	{
		return "Localization";
	}

	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case Action.Localization.COMMAND_PAD_SIZE:
			mWriteFlag |= 0x40000000;
			break;
		case Action.Localization.COMMAND_OID:
			mWriteFlag |= 0x20000000;
			break;
		}
	}
	
	@Override
	byte[] encodeSimulacrum()
	{
		byte[] simulacrum = mSimulacrum;
		synchronized(mWriteLock)
		{
			int[][] buffer = mWriteBuffer;
			int flag = mWriteFlag;
			
			int index = 2;
			simulacrum[0] = (byte)1;
			simulacrum[1] = (byte)0x00;
			if((flag & 0x40000000) != 0)
			{
				simulacrum[1] |= 0xc0;
				index = DataUtil.writeIntArray(simulacrum, index, buffer[0]);
			}
			if((flag & 0x20000000) != 0)
			{
				simulacrum[1] |= 0xa0;
				DataUtil.writeIntArray(simulacrum, index, buffer[1]);
			}
			mWriteFlag = 0;
		}
		return simulacrum;
	}

	@Override
	boolean decodeSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 10) return false;
		if((simulacrum[1] & 0x80) == 0) return false;
		
		int flag = 0;
		int index = 2;
		synchronized(mReadLock)
		{
			int[][] buffer = mReadBuffer;
			if((simulacrum[1] & 0x40) != 0)
			{
				index = DataUtil.readIntArray(simulacrum, index, buffer[0]);
				flag |= 0x40000000;
			}
			if((simulacrum[1] & 0x20) != 0)
			{
				index = DataUtil.readIntArray(simulacrum, index, buffer[1]);
				flag |= 0x20000000;
			}
			DataUtil.readIntArray(simulacrum, index, buffer[2]);
			mReadFlag = flag;
			mXmlReadFlag = flag;
			mJsonReadFlag = flag;
		}
		if((flag & 0x40000000) != 0)
			fire(0);
		if((flag & 0x20000000) != 0)
			fire(1);
		fire(2);
		return true;
	}

	@Override
	void notifyDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		int flag = mReadFlag;
		if((flag & 0x40000000) != 0)
			listener.onDeviceDataChanged(devices[0], buffer[0], timestamp);
		if((flag & 0x20000000) != 0)
			listener.onDeviceDataChanged(devices[1], buffer[1], timestamp);
		listener.onDeviceDataChanged(devices[2], buffer[2], timestamp);
	}

	@Override
	public void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<action><id>");
		sb.append(Action.Localization.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0x01900000;
		synchronized(mReadLock)
		{
			int flag = mXmlReadFlag;
			if((flag & 0x40000000) != 0)
				deviceMap |= 0x00400000;
			if((flag & 0x20000000) != 0)
				deviceMap |= 0x00200000;
			sb.append(deviceMap);
			sb.append("</dmp>");
			
			int[][] buffer = mReadBuffer;
			int[] values;
			if((deviceMap & 0x00400000) != 0)
			{
				sb.append("<padSize>");
				values = buffer[0];
				sb.append(values[0]);
				sb.append("</padSize><padSize>");
				sb.append(values[1]);
				sb.append("</padSize>");
			}
			if((deviceMap & 0x00200000) != 0)
			{
				sb.append("<oid>");
				sb.append(buffer[1][0]);
				sb.append("</oid>");
			}
			sb.append("<position>");
			values = buffer[2];
			sb.append(values[0]);
			sb.append("</position><position>");
			sb.append(values[1]);
			sb.append("</position>");
			mXmlReadFlag = 0;
		}
		sb.append("</action>");
	}

	@Override
	public void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[2,'");
		sb.append(Action.Localization.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		int deviceMap = 0x01900000;
		synchronized(mReadLock)
		{
			int flag = mJsonReadFlag;
			if((flag & 0x40000000) != 0)
				deviceMap |= 0x00400000;
			if((flag & 0x20000000) != 0)
				deviceMap |= 0x00200000;
			sb.append(deviceMap);
			
			int[][] buffer = mReadBuffer;
			int[] values;
			if((deviceMap & 0x00400000) != 0)
			{
				sb.append(",[");
				values = buffer[0];
				sb.append(values[0]);
				sb.append(",");
				sb.append(values[1]);
				sb.append("]");
			}
			if((deviceMap & 0x00200000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[1][0]);
				sb.append("]");
			}
			sb.append(",[");
			values = buffer[2];
			sb.append(values[0]);
			sb.append(",");
			sb.append(values[1]);
			sb.append("]");
			mJsonReadFlag = 0;
		}
		sb.append("]");
	}

	@Override
	public boolean decodeJson(JSONArray jsonArray)
	{
		try
		{
			int deviceMap = jsonArray.getInt(2);
			if((deviceMap & 0x00800000) == 0) return false;
			
			int[] values;
			JSONArray ja;
			int index = 3;
			synchronized(mWriteLock)
			{
				int[][] buffer = mWriteBuffer;
				if((deviceMap & 0x00400000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					values = buffer[0];
					values[0] = ja.getInt(0);
					values[1] = ja.getInt(1);
					fire(0);
					mWriteFlag |= 0x40000000;
				}
				if((deviceMap & 0x00200000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					buffer[1][0] = ja.getInt(0);
					fire(1);
					mWriteFlag |= 0x20000000;
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}