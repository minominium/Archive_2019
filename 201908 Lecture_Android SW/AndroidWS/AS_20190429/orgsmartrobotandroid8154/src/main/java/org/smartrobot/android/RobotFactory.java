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

import kr.robomation.physical.Albert;
import kr.robomation.physical.AlbertPop;
import kr.robomation.physical.Alpha;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class RobotFactory
{
	static AbstractRobot create(String modelId, String componentId)
	{
		if(Alpha.ID.equals(modelId))
			return new AlphaRobot(componentId);
		else if(Albert.ID.equals(modelId))
			return new AlbertRobot(componentId);
		else if(AlbertPop.ID.equals(modelId))
			return new AlbertPopRobot(componentId);
		return null;
	}
}