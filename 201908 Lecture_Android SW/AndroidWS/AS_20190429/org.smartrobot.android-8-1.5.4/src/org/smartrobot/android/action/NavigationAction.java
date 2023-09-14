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
final class NavigationAction extends AbstractAction
{
	private int mReadFlag;
	private int mWriteFlag;
	private int mXmlReadFlag;
	private int mJsonReadFlag;
	private byte[] mSimulacrum;
	private final int[][] mReadBuffer = new int[6][];
	private final int[][] mWriteBuffer = new int[6][];
	
	NavigationAction()
	{
		super(6, 0x40600000);
		
		int[][] readBuffer = mReadBuffer;
		int[][] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, COMMAND, Action.Navigation.COMMAND_PAD_SIZE, "PadSize", DataType.INTEGER, 2, 0);
		readBuffer[0] = (int[])getReadData(device);
		writeBuffer[0] = (int[])getWriteData(device);
		
		device = addDevice(1, COMMAND, Action.Navigation.COMMAND_INITIAL_POSITION, "InitialPosition", DataType.INTEGER, 2, 0);
		readBuffer[1] = (int[])getReadData(device);
		writeBuffer[1] = (int[])getWriteData(device);
		
		device = addDevice(2, COMMAND, Action.Navigation.COMMAND_WAYPOINTS, "Waypoints", DataType.INTEGER, -1, 0);
		readBuffer[2] = (int[])getReadData(device);
		writeBuffer[2] = (int[])getWriteData(device);
		
		device = addDevice(3, COMMAND, Action.Navigation.COMMAND_FINAL_ORIENTATION, "FinalOrientation", DataType.INTEGER, 1, 0);
		readBuffer[3] = (int[])getReadData(device);
		writeBuffer[3] = (int[])getWriteData(device);
		
		device = addDevice(4, COMMAND, Action.Navigation.COMMAND_MAX_SPEED, "MaxSpeed", DataType.INTEGER, 1, 50);
		readBuffer[4] = (int[])getReadData(device);
		writeBuffer[4] = (int[])getWriteData(device);
		
		device = addDevice(5, COMMAND, Action.Navigation.COMMAND_CURVATURE, "Curvature", DataType.INTEGER, 1, 50);
		readBuffer[5] = (int[])getReadData(device);
		writeBuffer[5] = (int[])getWriteData(device);
	}

	@Override
	public String getId()
	{
		return Action.Navigation.ID;
	}

	@Override
	public String getName()
	{
		return "Navigation";
	}

	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case Action.Navigation.COMMAND_PAD_SIZE:
			mWriteFlag |= 0x40000000;
			break;
		case Action.Navigation.COMMAND_INITIAL_POSITION:
			mWriteFlag |= 0x20000000;
			break;
		case Action.Navigation.COMMAND_WAYPOINTS:
			mWriteBuffer[2] = (int[])getWriteData(mDevices[2]);
			mWriteFlag |= 0x10000000;
			break;
		case Action.Navigation.COMMAND_FINAL_ORIENTATION:
			mWriteFlag |= 0x08000000;
			break;
		case Action.Navigation.COMMAND_MAX_SPEED:
			mWriteFlag |= 0x04000000;
			break;
		case Action.Navigation.COMMAND_CURVATURE:
			mWriteFlag |= 0x02000000;
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
			
			int len = 23;
			if((flag & 0x10000000) != 0 && buffer[2] != null)
				len += buffer[2].length * 4 + 4;
			if(simulacrum == null || len > simulacrum.length)
			{
				mSimulacrum = new byte[len];
				simulacrum = mSimulacrum;
			}
			
			int index = 3;
			simulacrum[0] = (byte)1;
			simulacrum[1] = (byte)0x00;
			simulacrum[2] = (byte)0x00;
			if((flag & 0x40000000) != 0)
			{
				simulacrum[1] |= 0xc0;
				index = DataUtil.writeIntArray(simulacrum, index, buffer[0]);
			}
			if((flag & 0x20000000) != 0)
			{
				simulacrum[1] |= 0xa0;
				index = DataUtil.writeIntArray(simulacrum, index, buffer[1]);
			}
			if((flag & 0x10000000) != 0 && buffer[2] != null)
			{
				simulacrum[1] |= 0x90;
				index = DataUtil.writeInt(simulacrum, index, buffer[2].length);
				index = DataUtil.writeIntArray(simulacrum, index, buffer[2]);
			}
			if((flag & 0x08000000) != 0)
			{
				simulacrum[1] |= 0x88;
				index = DataUtil.writeShortArray(simulacrum, index, buffer[3]);
			}
			if((flag & 0x04000000) != 0)
			{
				simulacrum[1] |= 0x84;
				index = DataUtil.writeUnsignedByteArray(simulacrum, index, buffer[4]);
			}
			if((flag & 0x02000000) != 0)
			{
				simulacrum[1] |= 0x82;
				DataUtil.writeUnsignedByteArray(simulacrum, index, buffer[5]);
			}
			mWriteFlag = 0;
		}
		return simulacrum;
	}

	@Override
	boolean decodeSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 3) return false;
		if((simulacrum[1] & 0x80) == 0) return false;
		
		Device[] devices = mDevices;
		int[][] buffer = mReadBuffer;
		
		int flag = 0;
		int index = 3;
		synchronized(mReadLock)
		{
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
			if((simulacrum[1] & 0x10) != 0)
			{
				int len = DataUtil.readInt(simulacrum, index);
				index += 4;
				int[] data = new int[len];
				index = DataUtil.readIntArray(simulacrum, index, data);
				if(putReadData(devices[2], data))
				{
					buffer[2] = (int[])getReadData(devices[2]);
					flag |= 0x10000000;
				}
			}
			if((simulacrum[1] & 0x08) != 0)
			{
				index = DataUtil.readShortArray(simulacrum, index, buffer[3]);
				flag |= 0x08000000;
			}
			if((simulacrum[1] & 0x04) != 0)
			{
				index = DataUtil.readUnsignedByteArray(simulacrum, index, buffer[4]);
				flag |= 0x04000000;
			}
			if((simulacrum[1] & 0x02) != 0)
			{
				DataUtil.readUnsignedByteArray(simulacrum, index, buffer[5]);
				flag |= 0x02000000;
			}
			mReadFlag = flag;
			mXmlReadFlag = flag;
			mJsonReadFlag = flag;
		}
		if((flag & 0x40000000) != 0)
			fire(0);
		if((flag & 0x20000000) != 0)
			fire(1);
		if((flag & 0x08000000) != 0)
			fire(3);
		if((flag & 0x04000000) != 0)
			fire(4);
		if((flag & 0x02000000) != 0)
			fire(5);
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
		if((flag & 0x10000000) != 0)
			listener.onDeviceDataChanged(devices[2], buffer[2], timestamp);
		if((flag & 0x08000000) != 0)
			listener.onDeviceDataChanged(devices[3], buffer[3], timestamp);
		if((flag & 0x04000000) != 0)
			listener.onDeviceDataChanged(devices[4], buffer[4], timestamp);
		if((flag & 0x02000000) != 0)
			listener.onDeviceDataChanged(devices[5], buffer[5], timestamp);
	}

	@Override
	public void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<action><id>");
		sb.append(Action.Navigation.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0x01000000;
		synchronized(mReadLock)
		{
			int flag = mXmlReadFlag;
			if((flag & 0x40000000) != 0)
				deviceMap |= 0x00c00000;
			if((flag & 0x20000000) != 0)
				deviceMap |= 0x00a00000;
			if((flag & 0x10000000) != 0)
				deviceMap |= 0x00900000;
			if((flag & 0x08000000) != 0)
				deviceMap |= 0x00880000;
			if((flag & 0x04000000) != 0)
				deviceMap |= 0x00840000;
			if((flag & 0x02000000) != 0)
				deviceMap |= 0x00820000;
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
				sb.append("<initialPosition>");
				values = buffer[1];
				sb.append(values[0]);
				sb.append("</initialPosition><initialPosition>");
				sb.append(values[1]);
				sb.append("</initialPosition>");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				values = buffer[2];
				int len = values.length;
				for(int i = 0; i < len; ++i)
				{
					sb.append("<waypoints>");
					sb.append(values[i]);
					sb.append("</waypoints>");
				}
			}
			if((deviceMap & 0x00080000) != 0)
			{
				sb.append("<finalOrientation>");
				sb.append(buffer[3][0]);
				sb.append("</finalOrientation>");
			}
			if((deviceMap & 0x00040000) != 0)
			{
				sb.append("<maxSpeed>");
				sb.append(buffer[4][0]);
				sb.append("</maxSpeed>");
			}
			if((deviceMap & 0x00020000) != 0)
			{
				sb.append("<curvature>");
				sb.append(buffer[5][0]);
				sb.append("</curvature>");
			}
			mXmlReadFlag = 0;
		}
		sb.append("</action>");
	}

	@Override
	public void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[2,'");
		sb.append(Action.Navigation.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		int deviceMap = 0x01000000;
		synchronized(mReadLock)
		{
			int flag = mJsonReadFlag;
			if((flag & 0x40000000) != 0)
				deviceMap |= 0x00c00000;
			if((flag & 0x20000000) != 0)
				deviceMap |= 0x00a00000;
			if((flag & 0x10000000) != 0)
				deviceMap |= 0x00900000;
			if((flag & 0x08000000) != 0)
				deviceMap |= 0x00880000;
			if((flag & 0x04000000) != 0)
				deviceMap |= 0x00840000;
			if((flag & 0x02000000) != 0)
				deviceMap |= 0x00820000;
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
				values = buffer[1];
				sb.append(values[0]);
				sb.append(",");
				sb.append(values[1]);
				sb.append("]");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				sb.append(",[");
				values = buffer[2];
				int len = values.length;
				if(len > 0)
					sb.append(values[0]);
				for(int i = 1; i < len; ++i)
				{
					sb.append(",");
					sb.append(values[i]);
				}
				sb.append("]");
			}
			if((deviceMap & 0x00080000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[3][0]);
				sb.append("]");
			}
			if((deviceMap & 0x00040000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[4][0]);
				sb.append("]");
			}
			if((deviceMap & 0x00020000) != 0)
			{
				sb.append(",[");
				sb.append(buffer[5][0]);
				sb.append("]");
			}
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
					values = buffer[1];
					values[0] = ja.getInt(0);
					values[1] = ja.getInt(1);
					fire(1);
					mWriteFlag |= 0x20000000;
				}
				if((deviceMap & 0x00100000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					int len = ja.length();
					if(len > 0)
					{
						values = new int[len];
						for(int i = 0; i < len; ++i)
							values[i] = ja.getInt(i);
						mDevices[2].write(values);
						mWriteBuffer[2] = (int[])getWriteData(mDevices[2]);
						mWriteFlag |= 0x10000000;
					}
				}
				if((deviceMap & 0x00080000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					buffer[3][0] = ja.getInt(0);
					fire(3);
					mWriteFlag |= 0x08000000;
				}
				if((deviceMap & 0x00040000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					buffer[4][0] = ja.getInt(0);
					fire(4);
					mWriteFlag |= 0x04000000;
				}
				if((deviceMap & 0x00020000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					buffer[5][0] = ja.getInt(0);
					fire(5);
					mWriteFlag |= 0x02000000;
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}