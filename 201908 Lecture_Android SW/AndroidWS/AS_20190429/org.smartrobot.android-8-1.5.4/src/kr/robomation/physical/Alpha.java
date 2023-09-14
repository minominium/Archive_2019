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

package kr.robomation.physical;

public final class Alpha
{
	public static final String ID = "kr.robomation.physical.alpha";
	
	public static final int EFFECTOR_SPEAKER = 0x00100000;
	public static final int EFFECTOR_VOLUME = 0x00100001;
	public static final int EFFECTOR_LIP = 0x00100002;
	public static final int EFFECTOR_LEFT_WHEEL = 0x00100003;
	public static final int EFFECTOR_RIGHT_WHEEL = 0x00100004;
	public static final int EFFECTOR_LEFT_EYE = 0x00100005;
	public static final int EFFECTOR_RIGHT_EYE = 0x00100006;
	public static final int EFFECTOR_BUZZER = 0x00100007;
	public static final int COMMAND_PAD_SIZE = 0x00100008;
	public static final int SENSOR_LEFT_PROXIMITY = 0x00100009;
	public static final int SENSOR_RIGHT_PROXIMITY = 0x0010000a;
	public static final int SENSOR_ACCELERATION = 0x0010000b;
	public static final int SENSOR_POSITION = 0x0010000c;
	public static final int SENSOR_ORIENTATION = 0x0010000d;
	public static final int SENSOR_LIGHT = 0x0010000e;
	public static final int SENSOR_TEMPERATURE = 0x0010000f;
	public static final int SENSOR_BATTERY = 0x00100010;
	public static final int EVENT_FRONT_OID = 0x00100011;
	public static final int EVENT_BACK_OID = 0x00100012;
}