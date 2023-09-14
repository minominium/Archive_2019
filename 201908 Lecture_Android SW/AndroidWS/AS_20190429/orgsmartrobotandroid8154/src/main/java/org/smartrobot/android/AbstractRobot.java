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

import org.json.JSONArray;
import org.roboid.robot.impl.RobotImpl;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public abstract class AbstractRobot extends RobotImpl
{
	final PhysicalRoboid mRoboid;
	private final String mComponentId;
	
	AbstractRobot(PhysicalRoboid roboid, String componentId)
	{
		mRoboid = roboid;
		mComponentId = (componentId == null) ? "" : componentId;
		addRoboid(roboid);
	}

	@Override
	public String getComponentId()
	{
		return mComponentId;
	}

	@Override
	protected void setName(String name)
	{
		super.setName(name);
	}

	@Override
	protected void updateDeviceState()
	{
		super.updateDeviceState();
	}
	
	public void encodeXml(StringBuilder sb, long timestamp)
	{
		mRoboid.encodeXml(sb, timestamp);
	}
	
	public void encodeJson(StringBuilder sb, long timestamp)
	{
		mRoboid.encodeJson(sb, timestamp);
	}
	
	public boolean decodeJson(JSONArray jsonArray)
	{
		return mRoboid.decodeJson(jsonArray);
	}
}