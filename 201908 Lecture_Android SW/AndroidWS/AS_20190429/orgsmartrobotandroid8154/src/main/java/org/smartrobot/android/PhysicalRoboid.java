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

import java.util.HashMap;

import org.json.JSONArray;
import org.roboid.robot.Device;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
abstract class PhysicalRoboid extends AbstractRoboid
{
	private final HashMap<String, PeripheralRoboid> mPeripherals = new HashMap<String, PeripheralRoboid>();

	PhysicalRoboid(int size, int tag)
	{
		super(size, tag);
	}
	
	void addPeripheral(PeripheralRoboid peripheral)
	{
		if(peripheral == null) return;
		addRoboid(peripheral);
		synchronized(mPeripherals)
		{
			mPeripherals.put(peripheral.mPeripheralId + "." + peripheral.mProductId, peripheral);
		}
	}
	
	PeripheralRoboid findPeripheral(int peripheralId, int productId)
	{
		synchronized(mPeripherals)
		{
			return mPeripherals.get(peripheralId + "." + productId);
		}
	}

	@Override
	protected Device findDevice(int uid)
	{
		Device device = super.findDevice(uid);
		if(device == null && (uid & 0x80000000) != 0)
		{
			PeripheralRoboid peripheral = PeripheralFactory.create((uid & 0x3ff00000) >> 20, (uid & 0x000ff000) >> 12);
			if(peripheral == null) return null;
			addPeripheral(peripheral);
			return peripheral.findDeviceById(uid);
		}
		return device;
	}
	
	void encodeXml(StringBuilder sb, long timestamp)
	{
		synchronized(mPeripherals)
		{
			for(PeripheralRoboid peripheral : mPeripherals.values())
				peripheral.encodeXml(sb, timestamp);
		}
	}
	
	void encodeJson(StringBuilder sb, long timestamp)
	{
		synchronized(mPeripherals)
		{
			for(PeripheralRoboid peripheral : mPeripherals.values())
				peripheral.encodeJson(sb, timestamp);
		}
	}
	
	abstract boolean decodeJson(JSONArray jsonArray);
}