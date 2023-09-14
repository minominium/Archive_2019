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

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class DataUtil
{
	static int readInt(byte[] simulacrum, int index)
	{
		int val = simulacrum[index++] << 24;
		val |= (simulacrum[index++] & 0xff) << 16;
		val |= (simulacrum[index++] & 0xff) << 8;
		val |= (simulacrum[index++] & 0xff);
		return val;
	}
	
	static int writeInt(byte[] simulacrum, int index, int data)
	{
		simulacrum[index++] = (byte)((data >> 24) & 0xff);
		simulacrum[index++] = (byte)((data >> 16) & 0xff);
		simulacrum[index++] = (byte)((data >> 8) & 0xff);
		simulacrum[index++] = (byte)(data & 0xff);
		return index;
	}
	
	static int readByteArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		for(int i = 0; i < sz; ++i)
			data[i] = simulacrum[index++];
		return index;
	}
	
	static int writeByteArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		for(int i = 0; i < sz; ++i)
			simulacrum[index++] = (byte)(data[i] & 0xff);
		return index;
	}
	
	static int readUnsignedByteArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		for(int i = 0; i < sz; ++i)
			data[i] = simulacrum[index ++] & 0xff;
		return index;
	}
	
	static int writeUnsignedByteArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		for(int i = 0; i < sz; ++i)
			simulacrum[index++] = (byte)(data[i] & 0xff);
		return index;
	}
	
	static int readShortArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = simulacrum[index++] << 8;
			val |= (simulacrum[index++] & 0xff);
			data[i] = val;
		}
		return index;
	}
	
	static int writeShortArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = data[i];
			simulacrum[index++] = (byte)((val >> 8) & 0xff);
			simulacrum[index++] = (byte)(val & 0xff);
		}
		return index;
	}
	
	static int readUnsignedShortArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = (simulacrum[index++] & 0xff) << 8;
			val |= (simulacrum[index++] & 0xff);
			data[i] = val;
		}
		return index;
	}
	
	static int writeUnsignedShortArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = data[i];
			simulacrum[index++] = (byte)((val >> 8) & 0xff);
			simulacrum[index++] = (byte)(val & 0xff);
		}
		return index;
	}
	
	static int readIntArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = simulacrum[index++] << 24;
			val |= (simulacrum[index++] & 0xff) << 16;
			val |= (simulacrum[index++] & 0xff) << 8;
			val |= (simulacrum[index++] & 0xff);
			data[i] = val;
		}
		return index;
	}
	
	static int writeIntArray(byte[] simulacrum, int index, int[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = data[i];
			simulacrum[index++] = (byte)((val >> 24) & 0xff);
			simulacrum[index++] = (byte)((val >> 16) & 0xff);
			simulacrum[index++] = (byte)((val >> 8) & 0xff);
			simulacrum[index++] = (byte)(val & 0xff);
		}
		return index;
	}
	
	static int readFloatArray(byte[] simulacrum, int index, float[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = (simulacrum[index++] & 0xff) << 24;
			val |= (simulacrum[index++] & 0xff) << 16;
			val |= (simulacrum[index++] & 0xff) << 8;
			val |= (simulacrum[index++] & 0xff);
			data[i] = Float.intBitsToFloat(val);
		}
		return index;
	}
	
	static int writeFloatArray(byte[] simulacrum, int index, float[] data)
	{
		int sz = data.length;
		int val = 0;
		for(int i = 0; i < sz; ++i)
		{
			val = Float.floatToIntBits(data[i]);
			simulacrum[index++] = (byte)((val >> 24) & 0xff);
			simulacrum[index++] = (byte)((val >> 16) & 0xff);
			simulacrum[index++] = (byte)((val >> 8) & 0xff);
			simulacrum[index++] = (byte)(val & 0xff);
		}
		return index;
	}
	
	static int readStringArray(byte[] simulacrum, int index, String[] data)
	{
		int sz = data.length;
		int len = 0;
		for(int i = 0; i < sz; ++i)
		{
			len = readInt(simulacrum, index);
			index += 4;
			data[i] = new String(simulacrum, index, len);
			index += len;
		}
		return index;
	}
	
	static int writeStringArray(byte[] simulacrum, int index, String[] data)
	{
		int sz = data.length;
		int len = 0;
		byte[] tmp;
		for(int i = 0; i < sz; ++i)
		{
			tmp = data[i].getBytes();
			len = tmp.length;
			index = writeInt(simulacrum, index, len);
			System.arraycopy(tmp, 0, simulacrum, index, len);
			index += len;
		}
		return index;
	}
	
	static int length(String[] data)
	{
		int sz = data.length;
		int len = 0;
		for(int i = 0; i < sz; ++i)
		{
			len += 4;
			len += data[i].getBytes().length;
		}
		return len;
	}
}