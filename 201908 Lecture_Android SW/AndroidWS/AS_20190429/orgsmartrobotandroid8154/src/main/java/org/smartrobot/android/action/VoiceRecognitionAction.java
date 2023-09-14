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
final class VoiceRecognitionAction extends AbstractAction
{
	private int mReadFlag;
	private int mWriteFlag;
	private int mXmlReadFlag;
	private int mJsonReadFlag;
	private byte[] mSimulacrum;
	private final Object[] mReadBuffer = new Object[9];
	private final Object[] mWriteBuffer = new Object[7];
	
	VoiceRecognitionAction()
	{
		super(9, 0x40100000);
		
		Object[] readBuffer = mReadBuffer;
		Object[] writeBuffer = mWriteBuffer;
		
		Device device = addDevice(0, COMMAND, Action.VoiceRecognition.COMMAND_LANGUAGE_MODEL, "LanguageModel", DataType.STRING, 1, "free_form");
		readBuffer[0] = getReadData(device);
		writeBuffer[0] = getWriteData(device);
		
		device = addDevice(1, COMMAND, Action.VoiceRecognition.COMMAND_LANGUAGE, "Language", DataType.STRING, 1, "");
		readBuffer[1] = getReadData(device);
		writeBuffer[1] = getWriteData(device);
		
		device = addDevice(2, COMMAND, Action.VoiceRecognition.COMMAND_COUNTRY, "Country", DataType.STRING, 1, "");
		readBuffer[2] = getReadData(device);
		writeBuffer[2] = getWriteData(device);
		
		device = addDevice(3, COMMAND, Action.VoiceRecognition.COMMAND_VARIANT, "Variant", DataType.STRING, 1, "");
		readBuffer[3] = getReadData(device);
		writeBuffer[3] = getWriteData(device);
		
		device = addDevice(4, COMMAND, Action.VoiceRecognition.COMMAND_VISIBILITY, "Visibility", DataType.INTEGER, 1, 1);
		readBuffer[4] = getReadData(device);
		writeBuffer[4] = getWriteData(device);
		
		device = addDevice(5, COMMAND, Action.VoiceRecognition.COMMAND_PROMPT, "Prompt", DataType.STRING, 1, "");
		readBuffer[5] = getReadData(device);
		writeBuffer[5] = getWriteData(device);
		
		device = addDevice(6, COMMAND, Action.VoiceRecognition.COMMAND_MAX_RESULTS, "MaxResults", DataType.INTEGER, 1, 5);
		readBuffer[6] = getReadData(device);
		writeBuffer[6] = getWriteData(device);
		
		device = addDevice(7, SENSOR, Action.VoiceRecognition.SENSOR_MIC_LEVEL, "MicLevel", DataType.FLOAT, 1, -100);
		readBuffer[7] = getReadData(device);
		
		device = addDevice(8, EVENT, Action.VoiceRecognition.EVENT_TEXT, "Text", DataType.STRING, -1, "");
		readBuffer[8] = getReadData(device);
	}

	@Override
	public String getId()
	{
		return Action.VoiceRecognition.ID;
	}

	@Override
	public String getName()
	{
		return "VoiceRecognition";
	}

	@Override
	protected void onMotoringDeviceWritten(Device device)
	{
		switch(device.getId())
		{
		case Action.VoiceRecognition.COMMAND_LANGUAGE_MODEL:
			mWriteFlag |= 0x40000000;
			break;
		case Action.VoiceRecognition.COMMAND_LANGUAGE:
			mWriteFlag |= 0x20000000;
			break;
		case Action.VoiceRecognition.COMMAND_COUNTRY:
			mWriteFlag |= 0x10000000;
			break;
		case Action.VoiceRecognition.COMMAND_VARIANT:
			mWriteFlag |= 0x08000000;
			break;
		case Action.VoiceRecognition.COMMAND_VISIBILITY:
			mWriteFlag |= 0x04000000;
			break;
		case Action.VoiceRecognition.COMMAND_PROMPT:
			mWriteFlag |= 0x02000000;
			break;
		case Action.VoiceRecognition.COMMAND_MAX_RESULTS:
			mWriteFlag |= 0x01000000;
			break;
		}
	}
	
	@Override
	byte[] encodeSimulacrum()
	{
		byte[] simulacrum = mSimulacrum;
		synchronized(mWriteLock)
		{
			Object[] buffer = mWriteBuffer;
			int flag = mWriteFlag;
			
			int len = 5;
			if((flag & 0x40000000) != 0)
				len += DataUtil.length((String[])buffer[0]);
			if((flag & 0x20000000) != 0)
				len += DataUtil.length((String[])buffer[1]);
			if((flag & 0x10000000) != 0)
				len += DataUtil.length((String[])buffer[2]);
			if((flag & 0x08000000) != 0)
				len += DataUtil.length((String[])buffer[3]);
			if((flag & 0x02000000) != 0)
				len += DataUtil.length((String[])buffer[5]);
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
				index = DataUtil.writeStringArray(simulacrum, index, (String[])buffer[0]);
			}
			if((flag & 0x20000000) != 0)
			{
				simulacrum[1] |= 0xa0;
				index = DataUtil.writeStringArray(simulacrum, index, (String[])buffer[1]);
			}
			if((flag & 0x10000000) != 0)
			{
				simulacrum[1] |= 0x90;
				index = DataUtil.writeStringArray(simulacrum, index, (String[])buffer[2]);
			}
			if((flag & 0x08000000) != 0)
			{
				simulacrum[1] |= 0x88;
				index = DataUtil.writeStringArray(simulacrum, index, (String[])buffer[3]);
			}
			if((flag & 0x04000000) != 0)
			{
				simulacrum[1] |= 0x84;
				index = DataUtil.writeUnsignedByteArray(simulacrum, index, (int[])buffer[4]);
			}
			if((flag & 0x02000000) != 0)
			{
				simulacrum[1] |= 0x82;
				index = DataUtil.writeStringArray(simulacrum, index, (String[])buffer[5]);
			}
			if((flag & 0x01000000) != 0)
			{
				simulacrum[1] |= 0x81;
				index = DataUtil.writeUnsignedByteArray(simulacrum, index, (int[])buffer[6]);
			}
			mWriteFlag = 0;
		}
		return simulacrum;
	}

	@Override
	boolean decodeSimulacrum(byte[] simulacrum)
	{
		if(simulacrum == null || simulacrum.length < 9) return false;
		if((simulacrum[1] & 0x80) == 0) return false;
		
		Device[] devices = mDevices;
		Object[] buffer = mReadBuffer;
		
		int flag = 0;
		int index = 3;
		synchronized(mReadLock)
		{
			if((simulacrum[1] & 0x40) != 0)
			{
				index = DataUtil.readStringArray(simulacrum, index, (String[])buffer[0]);
				flag |= 0x40000000;
			}
			if((simulacrum[1] & 0x20) != 0)
			{
				index = DataUtil.readStringArray(simulacrum, index, (String[])buffer[1]);
				flag |= 0x20000000;
			}
			if((simulacrum[1] & 0x10) != 0)
			{
				index = DataUtil.readStringArray(simulacrum, index, (String[])buffer[2]);
				flag |= 0x10000000;
			}
			if((simulacrum[1] & 0x08) != 0)
			{
				index = DataUtil.readStringArray(simulacrum, index, (String[])buffer[3]);
				flag |= 0x08000000;
			}
			if((simulacrum[1] & 0x04) != 0)
			{
				index = DataUtil.readUnsignedByteArray(simulacrum, index, (int[])buffer[4]);
				flag |= 0x04000000;
			}
			if((simulacrum[1] & 0x02) != 0)
			{
				index = DataUtil.readStringArray(simulacrum, index, (String[])buffer[5]);
				flag |= 0x02000000;
			}
			if((simulacrum[1] & 0x01) != 0)
			{
				index = DataUtil.readUnsignedByteArray(simulacrum, index, (int[])buffer[6]);
				flag |= 0x01000000;
			}
			index = DataUtil.readFloatArray(simulacrum, index, (float[])buffer[7]);
			if((simulacrum[2] & 0x40) != 0)
			{
				int len = DataUtil.readInt(simulacrum, index);
				index += 4;
				String[] data = new String[len];
				DataUtil.readStringArray(simulacrum, index, data);
				if(putReadData(devices[8], data))
				{
					buffer[8] = getReadData(devices[8]);
					flag |= 0x00400000;
				}
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
		if((flag & 0x08000000) != 0)
			fire(3);
		if((flag & 0x04000000) != 0)
			fire(4);
		if((flag & 0x02000000) != 0)
			fire(5);
		if((flag & 0x01000000) != 0)
			fire(6);
		fire(7);
		return true;
	}

	@Override
	void notifyDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
		Device[] devices = mDevices;
		Object[] buffer = mReadBuffer;
		
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
		if((flag & 0x01000000) != 0)
			listener.onDeviceDataChanged(devices[6], buffer[6], timestamp);
		listener.onDeviceDataChanged(devices[7], buffer[7], timestamp);
		if((flag & 0x00400000) != 0)
			listener.onDeviceDataChanged(devices[8], buffer[8], timestamp);
	}

	@Override
	public void encodeXml(StringBuilder sb, long timestamp)
	{
		sb.append("<action><id>");
		sb.append(Action.VoiceRecognition.ID);
		sb.append("</id><timestamp>");
		sb.append(timestamp);
		sb.append("</timestamp><dmp>");
		int deviceMap = 0x01808000;
		synchronized(mReadLock)
		{
			if((mXmlReadFlag & 0x40000000) != 0)
				deviceMap |= 0x00400000;
			if((mXmlReadFlag & 0x20000000) != 0)
				deviceMap |= 0x00200000;
			if((mXmlReadFlag & 0x10000000) != 0)
				deviceMap |= 0x00100000;
			if((mXmlReadFlag & 0x08000000) != 0)
				deviceMap |= 0x00080000;
			if((mXmlReadFlag & 0x04000000) != 0)
				deviceMap |= 0x00040000;
			if((mXmlReadFlag & 0x02000000) != 0)
				deviceMap |= 0x00020000;
			if((mXmlReadFlag & 0x01000000) != 0)
				deviceMap |= 0x00010000;
			if((mXmlReadFlag & 0x00400000) != 0)
				deviceMap |= 0x00004000;
			sb.append(deviceMap);
			sb.append("</dmp>");
			
			Object[] buffer = mReadBuffer;
			if((deviceMap & 0x00400000) != 0)
			{
				sb.append("<languageModel>");
				sb.append(((String[])buffer[0])[0]);
				sb.append("</languageModel>");
			}
			if((deviceMap & 0x00200000) != 0)
			{
				sb.append("<language>");
				sb.append(((String[])buffer[1])[0]);
				sb.append("</language>");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				sb.append("<country>");
				sb.append(((String[])buffer[2])[0]);
				sb.append("</country>");
			}
			if((deviceMap & 0x00080000) != 0)
			{
				sb.append("<variant>");
				sb.append(((String[])buffer[3])[0]);
				sb.append("</variant>");
			}
			if((deviceMap & 0x00040000) != 0)
			{
				sb.append("<visibility>");
				sb.append(((int[])buffer[4])[0]);
				sb.append("</visibility>");
			}
			if((deviceMap & 0x00020000) != 0)
			{
				sb.append("<prompt>");
				sb.append(((String[])buffer[5])[0]);
				sb.append("</prompt>");
			}
			if((deviceMap & 0x00010000) != 0)
			{
				sb.append("<maxResults>");
				sb.append(((int[])buffer[6])[0]);
				sb.append("</maxResults>");
			}
			sb.append("<micLevel>");
			sb.append(((float[])buffer[7])[0]);
			sb.append("</micLevel>");
			if((deviceMap & 0x00004000) != 0)
			{
				String[] values = (String[])buffer[8];
				int len = values.length;
				for(int i = 0; i < len; ++i)
				{
					sb.append("<text>");
					sb.append(values[i]);
					sb.append("</text>");
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
		sb.append(Action.VoiceRecognition.ID);
		sb.append("',");
		sb.append(timestamp);
		sb.append(",");
		int deviceMap = 0x01808000;
		synchronized(mReadLock)
		{
			if((mJsonReadFlag & 0x40000000) != 0)
				deviceMap |= 0x00400000;
			if((mJsonReadFlag & 0x20000000) != 0)
				deviceMap |= 0x00200000;
			if((mJsonReadFlag & 0x10000000) != 0)
				deviceMap |= 0x00100000;
			if((mJsonReadFlag & 0x08000000) != 0)
				deviceMap |= 0x00080000;
			if((mJsonReadFlag & 0x04000000) != 0)
				deviceMap |= 0x00040000;
			if((mJsonReadFlag & 0x02000000) != 0)
				deviceMap |= 0x00020000;
			if((mJsonReadFlag & 0x01000000) != 0)
				deviceMap |= 0x00010000;
			if((mJsonReadFlag & 0x00400000) != 0)
				deviceMap |= 0x00004000;
			sb.append(deviceMap);
			
			Object[] buffer = mReadBuffer;
			if((deviceMap & 0x00400000) != 0)
			{
				sb.append(",['");
				sb.append(((String[])buffer[0])[0]);
				sb.append("']");
			}
			if((deviceMap & 0x00200000) != 0)
			{
				sb.append(",['");
				sb.append(((String[])buffer[1])[0]);
				sb.append("']");
			}
			if((deviceMap & 0x00100000) != 0)
			{
				sb.append(",['");
				sb.append(((String[])buffer[2])[0]);
				sb.append("']");
			}
			if((deviceMap & 0x00080000) != 0)
			{
				sb.append(",['");
				sb.append(((String[])buffer[3])[0]);
				sb.append("']");
			}
			if((deviceMap & 0x00040000) != 0)
			{
				sb.append(",[");
				sb.append(((int[])buffer[4])[0]);
				sb.append("]");
			}
			if((deviceMap & 0x00020000) != 0)
			{
				sb.append(",['");
				sb.append(((String[])buffer[5])[0]);
				sb.append("']");
			}
			if((deviceMap & 0x00010000) != 0)
			{
				sb.append(",[");
				sb.append(((int[])buffer[6])[0]);
				sb.append("]");
			}
			sb.append(",[");
			sb.append(((float[])buffer[7])[0]);
			sb.append("]");
			if((deviceMap & 0x00004000) != 0)
			{
				sb.append(",['");
				String[] values = (String[])buffer[8];
				int len = values.length;
				if(len > 0)
					sb.append(values[0]);
				for(int i = 1; i < len; ++i)
				{
					sb.append("','");
					sb.append(values[i]);
				}
				sb.append("']");
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
			
			JSONArray ja;
			int index = 3;
			synchronized(mWriteLock)
			{
				Object[] buffer = mWriteBuffer;
				if((deviceMap & 0x00400000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((String[])buffer[0])[0] = ja.getString(0);
					fire(0);
					mWriteFlag |= 0x40000000;
				}
				if((deviceMap & 0x00200000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((String[])buffer[1])[0] = ja.getString(0);
					fire(1);
					mWriteFlag |= 0x20000000;
				}
				if((deviceMap & 0x00100000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((String[])buffer[2])[0] = ja.getString(0);
					fire(2);
					mWriteFlag |= 0x10000000;
				}
				if((deviceMap & 0x00080000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((String[])buffer[3])[0] = ja.getString(0);
					fire(3);
					mWriteFlag |= 0x08000000;
				}
				if((deviceMap & 0x00040000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((int[])buffer[4])[0] = ja.getInt(0);
					fire(4);
					mWriteFlag |= 0x04000000;
				}
				if((deviceMap & 0x00020000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((String[])buffer[5])[0] = ja.getString(0);
					fire(5);
					mWriteFlag |= 0x02000000;
				}
				if((deviceMap & 0x00010000) != 0)
				{
					ja = jsonArray.getJSONArray(index++);
					((int[])buffer[6])[0] = ja.getInt(0);
					fire(6);
					mWriteFlag |= 0x01000000;
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}