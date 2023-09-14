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
final class TileAction extends AbstractAction
{
	private int mReadFlag;
	private int mWriteFlag;
	private int mXmlReadFlag;
	private int mJsonReadFlag;
	private byte[] mSimulacrum;
	private final int[][] mReadBuffer = new int[5][];
	private final int[][] mWriteBuffer = new int[5][];
	
	TileAction()
	{
		super(5, 0x40800000);
		
		int[][] readBuffer = mReadBuffer;
		int[][] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, COMMAND, Action.Tile.COMMAND_PAD_SIZE, "PadSize", DataType.INTEGER, 2, 0);
		readBuffer[0] = (int[])getReadData(device);
		writeBuffer[0] = (int[])getWriteData(device);
		
		device = addDevice(1, COMMAND, Action.Tile.COMMAND_TILE_SIZE, "TileSize", DataType.INTEGER, 2, 0);
		readBuffer[1] = (int[])getReadData(device);
		writeBuffer[1] = (int[])getWriteData(device);
		
		device = addDevice(2, COMMAND, Action.Tile.COMMAND_INITIAL_DIRECTION, "InitialDirection", DataType.INTEGER, 1, 0);
		readBuffer[2] = (int[])getReadData(device);
		writeBuffer[2] = (int[])getWriteData(device);
		
		device = addDevice(3, COMMAND, Action.Tile.COMMAND_MOTION, "Motion", DataType.INTEGER, -1, 0);
		readBuffer[3] = (int[])getReadData(device);
		writeBuffer[3] = (int[])getWriteData(device);
		
		device = addDevice(4, EVENT, Action.Tile.EVENT_STATE, "State", DataType.INTEGER, 3, 0);
		readBuffer[4] = (int[])getReadData(device);
		writeBuffer[4] = (int[])getWriteData(device);
	}

	@Override
	public String getId()
	{
		return Action.Tile.ID;
	}

	@Override
	public String getName()
	{
		return "Tile";
	}

	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case Action.Tile.COMMAND_PAD_SIZE:
			mWriteFlag |= 0x40000000;
			break;
		case Action.Tile.COMMAND_TILE_SIZE:
			mWriteFlag |= 0x20000000;
			break;
		case Action.Tile.COMMAND_INITIAL_DIRECTION:
			mWriteFlag |= 0x10000000;
			break;
		case Action.Tile.COMMAND_MOTION:
			mWriteBuffer[3] = (int[])getWriteData(mDevices[3]);
			mWriteFlag |= 0x08000000;
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
			
			int len = 20;
			if((flag & 0x08000000) != 0 && buffer[3] != null)
				len += buffer[3].length + 4;
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
			if((flag & 0x10000000) != 0)
			{
				simulacrum[1] |= 0x90;
				index = DataUtil.writeUnsignedByteArray(simulacrum, index, buffer[2]);
			}
			if((flag & 0x08000000) != 0 && buffer[3] != null)
			{
				simulacrum[1] |= 0x88;
				index = DataUtil.writeInt(simulacrum, index, buffer[3].length);
				index = DataUtil.writeUnsignedByteArray(simulacrum, index, buffer[3]);
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
				index = DataUtil.readUnsignedByteArray(simulacrum, index, buffer[2]);
				flag |= 0x10000000;
			}
			if((simulacrum[1] & 0x08) != 0)
			{
				int len = DataUtil.readInt(simulacrum, index);
				index += 4;
				int[] data = new int[len];
				index = DataUtil.readUnsignedByteArray(simulacrum, index, data);
				if(putReadData(devices[3], data))
				{
					buffer[3] = (int[])getReadData(devices[3]);
					flag |= 0x08000000;
				}
			}
			if((simulacrum[1] & 0x04) != 0)
			{
				index = DataUtil.readIntArray(simulacrum, index, buffer[4]);
				flag |= 0x04000000;
			}
			mReadFlag = flag;
			mXmlReadFlag = flag;
			mJsonReadFlag = flag;
		}
		if((flag & 0x40000000) != 0)
			fire(0);
		if((flag & 0x20000000) != 0)
			fire(1);
		if((flag & 0x10000000) != 0)
			fire(2);
		if((flag & 0x04000000) != 0)
			fire(4);
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
	}

	@Override
	public void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<action><id>");
		sb.append(Action.Tile.ID);
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
				sb.append("<tileSize>");
				values = buffer[1];
				sb.append(values[0]);
				sb.append("</tileSize><tileSize>");
				sb.append(values[1]);
				sb.append("</tileSize>");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				sb.append("<initialDirection>");
				sb.append(buffer[2][0]);
				sb.append("</initialDirection>");
			}
			if((deviceMap & 0x00080000) != 0)
			{
				values = buffer[3];
				int len = values.length;
				for(int i = 0; i < len; ++i)
				{
					sb.append("<motion>");
					sb.append(values[i]);
					sb.append("</motion>");
				}
			}
			if((deviceMap & 0x00040000) != 0)
			{
				values = buffer[4];
				int len = values.length;
				for(int i = 0; i < len; ++i)
				{
					sb.append("<state>");
					sb.append(values[i]);
					sb.append("</state>");
				}
			}
			mXmlReadFlag = 0;
		}
		sb.append("</action>");
	}

	@Override
	public void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[2,'");
		sb.append(Action.Tile.ID);
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
				sb.append(buffer[2][0]);
				sb.append("]");
			}
			if((deviceMap & 0x00080000) != 0)
			{
				sb.append(",[");
				values = buffer[3];
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
			if((deviceMap & 0x00040000) != 0)
			{
				sb.append(",[");
				values = buffer[4];
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
					buffer[2][0] = ja.getInt(0);
					fire(2);
					mWriteFlag |= 0x10000000;
				}
				if((deviceMap & 0x00080000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					int len = ja.length();
					if(len > 0)
					{
						values = new int[len];
						for(int i = 0; i < len; ++i)
							values[i] = ja.getInt(i);
						mDevices[3].write(values);
						mWriteBuffer[3] = (int[])getWriteData(mDevices[3]);
						mWriteFlag |= 0x08000000;
					}
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}