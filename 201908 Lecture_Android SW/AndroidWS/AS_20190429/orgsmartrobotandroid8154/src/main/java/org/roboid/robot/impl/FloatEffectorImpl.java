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

package org.roboid.robot.impl;

import org.roboid.robot.Effector;
import org.roboid.robot.Roboid;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class FloatEffectorImpl extends FloatMotoringDeviceImpl implements Effector
{
	FloatEffectorImpl(Roboid parent, int uid, String name, int dataType, int dataSize, Object initialValue, Object readLock, Object writeLock, Object fireLock)
	{
		super(parent, uid, name, dataType, dataSize, initialValue, readLock, writeLock, fireLock);
	}
}