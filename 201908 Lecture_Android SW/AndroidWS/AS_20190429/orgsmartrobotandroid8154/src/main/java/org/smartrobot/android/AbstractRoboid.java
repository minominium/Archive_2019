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

import org.roboid.robot.Device.DeviceDataChangedListener;
import org.roboid.robot.impl.RoboidImpl;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
abstract class AbstractRoboid extends RoboidImpl
{
	AbstractRoboid(int size)
	{
		super(size);
	}
	
	AbstractRoboid(int size, int tag)
	{
		super(size, tag);
	}
	
	void handleSensorySimulacrum(byte[] simulacrum, long timestamp)
	{
		if(decodeSensorySimulacrum(simulacrum))
		{
			synchronized(mListeners)
			{
				for(DeviceDataChangedListener listener : mListeners)
					notifySensoryDataChanged(listener, timestamp);
			}
		}
	}
	
	void handleMotoringSimulacrum(byte[] simulacrum, long timestamp)
	{
		if(decodeMotoringSimulacrum(simulacrum))
		{
			synchronized(mListeners)
			{
				for(DeviceDataChangedListener listener : mListeners)
					notifyMotoringDataChanged(listener, timestamp);
			}
		}
	}
	
	abstract boolean decodeSensorySimulacrum(byte[] simulacrum);
	abstract void notifySensoryDataChanged(DeviceDataChangedListener listener, long timestamp);
	
	boolean decodeMotoringSimulacrum(byte[] simulacrum)
	{
		return false;
	}
	
	void notifyMotoringDataChanged(DeviceDataChangedListener listener, long timestamp)
	{
	}
	
	byte[] encodeMotoringSimulacrum()
	{
		return null;
	}
}