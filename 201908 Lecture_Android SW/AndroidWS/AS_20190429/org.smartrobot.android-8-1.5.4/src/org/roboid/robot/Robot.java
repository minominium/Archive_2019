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

import org.roboid.robot.Device.DeviceDataChangedListener;

public interface Robot extends NamedElement
{
	int STATE_CONNECTING = 1;
	int STATE_CONNECTED = 2;
	int STATE_CONNECTION_LOST = 3;
	int STATE_DISCONNECTED = 4;
	
	String getId();
	String getComponentId();
	Device findDeviceById(int deviceId);
	Device findDeviceById(int productId, int deviceId);
	void addDeviceDataChangedListener(DeviceDataChangedListener listener);
	void removeDeviceDataChangedListener(DeviceDataChangedListener listener);
	void clearDeviceDataChangedListener();
}
