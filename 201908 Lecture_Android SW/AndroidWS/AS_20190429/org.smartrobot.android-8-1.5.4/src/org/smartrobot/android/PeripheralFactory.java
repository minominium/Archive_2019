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

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class PeripheralFactory
{
	static PeripheralRoboid create(int peripheralId, int productId)
	{
		switch(peripheralId)
		{
		case 1:
			return new PenRoboid(productId);
		case 2:
			return new DiceRoboid(productId);
		}
		return null;
	}
}