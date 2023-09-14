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
final class MicrophoneAction extends AbstractAction
{
	private final float[] mReadBuffer;
	
	MicrophoneAction()
	{
		super(1, 0x40200000);
		
		Device device = addDevice(0, SENSOR, Action.Microphone.SENSOR_LEVEL, "Level", DataType.FLOAT, 1, -100);
		mReadBuffer = (float[])getReadData(device);
	}

	@Override
	public String getId()
	{
		return Action.Microphone.ID;
	}

	@Override
	public String getName()
	{
		return "Microphone";
	}

	@Override
	boolean decodeSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 6) return false;
		if((simulacrum[1] & 0x80) == 0) return false;
		
		synchronized(mReadLock)
		{
			DataUtil.readFloatArray(simulacrum, 2, mReadBuffer);
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
		sb.append(Action.Microphone.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		sb.append(0x01c00000);
		sb.append("</dmp>");
		synchronized(mReadLock)
		{
			sb.append("<level>");
			sb.append(mReadBuffer[0]);
			sb.append("</level>");
		}
		sb.append("</action>");
	}

	@Override
	public void encodeJson(StringBuilder sb, long timestamp)
	{
		sb.append(",[2,'");
		sb.append(Action.Microphone.ID);
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
		return false;
	}
}