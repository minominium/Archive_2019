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
final class WalkieTalkieAction extends AbstractAction
{
	private final byte[] mSimulacrum = new byte[3];
	private final int[] mReadBuffer;
	private final int[] mWriteBuffer;
	
	WalkieTalkieAction()
	{
		super(1, 0x40300000);
		
		Device device = addDevice(0, EFFECTOR, Action.WalkieTalkie.EFFECTOR_SENSITIVITY, "Sensitivity", DataType.INTEGER, 1, 20);
		mReadBuffer = (int[])getReadData(device);
		mWriteBuffer = (int[])getWriteData(device);
	}

	@Override
	public String getId()
	{
		return Action.WalkieTalkie.ID;
	}
	
	@Override
	public String getName()
	{
		return "WalkieTalkie";
	}
	
	@Override
	byte[] encodeSimulacrum()
	{
		byte[] simulacrum = mSimulacrum;
		simulacrum[0] = (byte)1;
		simulacrum[1] = (byte)0xc0;
		synchronized(mWriteLock)
		{
			DataUtil.writeUnsignedByteArray(simulacrum, 2, mWriteBuffer);
		}
		return simulacrum;
	}

	@Override
	boolean decodeSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 3) return false;
		if((simulacrum[1] & 0x80) == 0) return false;
		
		synchronized(mReadLock)
		{
			DataUtil.readUnsignedByteArray(simulacrum, 2, mReadBuffer);
		}
		fire(0);
		return true;
	}

	@Override
	void notifyDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		listener.onDeviceDataChanged(mDevices[0], mReadBuffer, timestamp);
	}

	@Override
	public void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<action><id>");
		sb.append(Action.WalkieTalkie.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		sb.append(0x01c00000);
		sb.append("</dmp>");
		synchronized(mReadLock)
		{
			sb.append("<sensitivity>");
			sb.append(mReadBuffer[0]);
			sb.append("</sensitivity>");
		}
		sb.append("</action>");
	}

	@Override
	public void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[2,'");
		sb.append(Action.WalkieTalkie.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		sb.append(0x01c00000);
		synchronized(mReadLock)
		{
			sb.append(",[");
			sb.append(mReadBuffer[0]);
			sb.append("]");
		}
		sb.append("]");
	}

	@Override
	public boolean decodeJson(JSONArray jsonArray)
	{
		try
		{
			synchronized(mWriteLock)
			{
				mWriteBuffer[0] = jsonArray.getInt(1);
				fire(0);
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}