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

package org.roboid.robot;

public interface Device extends NamedElement
{
	public interface DeviceDataChangedListener
	{
		void onDeviceDataChanged(Device device, Object values, long timestamp);
	}

	int getId();
	int getProductId();
	int getDataType();
	int getDataSize();
	boolean e();
	int read();
	int read(int index);
	int read(int[] data);
	float readFloat();
	float readFloat(int index);
	int readFloat(float[] data);
	String readString();
	String readString(int index);
	int readString(String[] data);
	boolean write(int data);
	boolean write(int index, int data);
	int write(int[] data);
	boolean writeFloat(float data);
	boolean writeFloat(int index, float data);
	int writeFloat(float[] data);
	boolean writeString(String data);
	boolean writeString(int index, String data);
	int writeString(String[] data);
}