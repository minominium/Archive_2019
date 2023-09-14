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

/**
 * <p>Defines constants describing the model id of an Albert robot and the device id of each device composing the robot.
 * <p>To read or write data of the device, we can get the reference of {@link org.roboid.robot.Device Device} object using {@link org.roboid.robot.Robot#findDeviceById(int) findDeviceById(int deviceId)} method of {@link org.roboid.robot.Robot Robot} class
 * and then read or write data of the device using <i>read</i> or <i>write</i> methods.
 * We can also obtain data of the device through {@link org.roboid.robot.Device.DeviceDataChangedListener#onDeviceDataChanged(org.roboid.robot.Device, Object, long) onDeviceDataChanged} callback method.
 * </p>
 * <pre>
 * void someMethod(Robot robot)
 {
     Device deviceLip = robot.findDeviceById(Albert.EFFECTOR_LIP); // obtains the "lip" device.
     int lip = deviceLip.read(); // reads data.

     Device deviceLeftProximity = robot.findDeviceById(Albert.SENSOR_LEFT_PROXIMITY); // obtains the "left proximity" device.
     int[] leftProximity = new int[4];
     deviceLeftProximity.read(leftProximity); // reads data.

     Device devicePosition = robot.findDeviceById(Albert.SENSOR_POSITION); // obtains the "position" device.
     int positionX = devicePosition.read(0); // reads the x-coordinate value
     int positionY = devicePosition.read(1); // reads the y-coordinate value

     Device deviceFrontOID = robot.findDeviceById(Albert.EVENT_FRONT_OID); // obtains the "front oid" device.
     if(deviceFrontOID.e()) // checks if data of the front OID sensor has been updated.
     {
         int frontOID = deviceFrontOID.read(); // reads data.
     }

     Device deviceLeftWheel = robot.findDeviceById(Albert.EFFECTOR_LEFT_WHEEL); // obtains the "left wheel" device.
     deviceLeftWheel.write(25); // writes data.

     Device deviceLeftEye = robot.findDeviceById(Albert.EFFECTOR_LEFT_EYE); // obtains the "left eye" device.
     int[] leftEye = new int[] { 255, 0, 0 };
     deviceLeftEye.write(leftEye); // writes data.

     Device deviceFrontLED = robot.findDeviceById(Albert.COMMAND_FRONT_LED); // obtains the "front led" device.
     deviceFrontLED.write(1); // writes data.

     Device deviceBodyLED = robot.findDeviceById(Albert.EFFECTOR_BODY_LED); // obtains the "body led" device.
     deviceBodyLED.write(50); // writes data.

     Device devicePadSize = robot.findDeviceById(Albert.COMMAND_PAD_SIZE); // obtains the "pad size" device.
     devicePadSize.write(0, 108); // writes the width value of a pad.
     devicePadSize.write(1, 76); // writes the height value of a pad.
 }

 public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int leftWheel, padWidth, padHeight, frontOID;
     int[] leftProximity = new int[4];
     switch(device.getId())
     {
     case Albert.EFFECTOR_LEFT_WHEEL: // obtains data of the "left wheel" device.
         leftWheel = ((int[])values)[0];
         break;
     case Albert.COMMAND_PAD_SIZE: // obtains data of the "pad size" device.
         padWidth = ((int[])values)[0];
         padHeight = ((int[])values)[1];
         break;
     case Albert.SENSOR_LEFT_PROXIMITY: // obtains data of the "left proximity" device.
         System.arraycopy((int[])values, 0, leftProximity, 0, 4);
         break;
     case Albert.EVENT_FRONT_OID: // obtains data of the "front oid" device.
         frontOID = ((int[])values)[0];
         break;
     }
 }</pre>
 *
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Robot Robot
 * @see org.roboid.robot.Device Device
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 */
public final class Albert
{
	/**
	 * <p>A constant string describing the model id of an Albert robot.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: "kr.robomation.physical.albert"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.physical.albert";
	
	/**
	 * <p>A constant describing the device id of a "speaker" device.
	 * <p>Data of the device shows the sound output (PCM) to the speaker of a robot.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200000
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 480
	 *             <li>Value: -32768 to 32767
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_SPEAKER = 0x00200000;
	/**
	 * <p>A constant describing the device id of a "volume" device.
	 * <p>Data of the device shows the sound volume of a robot in %.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200001
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 300 [%]
	 *             <li>Initial Value: 100
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_VOLUME = 0x00200001;
	/**
	 * <p>A constant describing the device id of a "lip" device.
	 * <p>Data of the device shows the size of a lip in %.
	 * The "lip" device is a virtual device which does not exist in hardware.
	 * Data of this device can be used to display graphic on a screen or make LED blink to show similar effect to moving lips.
	 * Data can be obtained when a motion clip file is played.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200002
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 100 [%]
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LIP = 0x00200002;
	/**
	 * <p>A constant describing the device id of a "left wheel" device.
	 * <p>Data of the device shows the speed of a left wheel in %.
	 * Positive value shows forward direction and negative value shows backward direction.
	 * The greater the absolute value is, the faster the speed is.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200003
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -100 to 100 [%]
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LEFT_WHEEL = 0x00200003;
	/**
	 * <p>A constant describing the device id of a "right wheel" device.
	 * <p>Data of the device shows the speed of a right wheel in %.
	 * Positive value shows forward direction and negative value shows backward direction.
	 * The greater the absolute value is, the faster the speed is.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200004
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -100 to 100 [%]
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_RIGHT_WHEEL = 0x00200004;
	/**
	 * <p>A constant describing the device id of a "left eye" device.
	 * <p>Data of the device shows the LED color of a left eye.
	 * The first value of the integer array is the R (Red) value of RGB, the second value G (Green) and the third value B (Blue).
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200005
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 3
	 *             <li>Value: 0 to 255
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_LEFT_EYE = 0x00200005;
	/**
	 * <p>A constant describing the device id of a "right eye" device.
	 * <p>Data of the device shows the LED color of a right eye.
	 * The first value of the integer array is the R (Red) value of RGB, the second value G (Green) and the third value B (Blue).
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200006
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 3
	 *             <li>Value: 0 to 255
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_RIGHT_EYE = 0x00200006;
	/**
	 * <p>A constant describing the device id of a "body led" device.
	 * <p>Data of the device shows the brightness of a body LED in %.
	 * The greater the data value is, the brighter the LED is.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200013
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 100 [%]
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_BODY_LED = 0x00200013;
	/**
	 * <p>A constant describing the device id of a "buzzer" device.
	 * <p>Data of the device shows the sound pitch of a buzzer in Hz.
	 * Write 0 to the device to turn off buzzer sound.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200007
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 2500 [Hz] (0: off)
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EFFECTOR_BUZZER = 0x00200007;
	/**
	 * <p>A constant describing the device id of a "front led" device.
	 * <p>Data of the device shows the status of a front LED.
	 * Write 1 to the device to turn the LED on, or 0 to turn the LED off.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200014
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 or 1 (0: LED off, 1: LED on)
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int COMMAND_FRONT_LED = 0x00200014;
	/**
	 * <p>A constant describing the device id of a "pad size" device.
	 * <p>Data of the device shows the size of a navigation pad.
	 * The first value of the integer array is the width and the second value is the height of a pad.
	 * The multiplication (area) of the width and height cannot exceed 40000.
	 * In other words, if the width is 1, the height can have the value 1 to 40000, and the width is 200, the height can have the value 1 to 200.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200008
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 2
	 *             <li>Value: 0 to 40000 (0: invalid value)
	 *             <li>Initial Value: 0 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int COMMAND_PAD_SIZE = 0x00200008;
	/**
	 * <p>A constant describing the device id of a "left proximity" device.
	 * <p>Data of the device shows the value of a left proximity sensor.
	 * As data is measured with 10ms interval by a proximity sensor and is delivered to an application in every 40ms,
	 * four sensor values are accumulated and delivered together.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200009
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 4
	 *             <li>Value: 0 to 255
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_LEFT_PROXIMITY = 0x00200009;
	/**
	 * <p>A constant describing the device id of a "right proximity" device.
	 * <p>Data of the device shows the value of a right proximity sensor.
	 * As data is measured with 10ms interval by a proximity sensor and is delivered to an application in every 40ms,
	 * four sensor values are accumulated and delivered together.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x0020000a
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 4
	 *             <li>Value: 0 to 255
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_RIGHT_PROXIMITY = 0x0020000a;
	/**
	 * <p>A constant describing the device id of an "acceleration" device.
	 * <p>Data of the device shows the 3-axis acceleration values of a robot.
	 * The first value of the integer array is the acceleration value of x-axis, the second value that of y-axis, and the third value that of z-axis.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x0020000b
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 3
	 *             <li>Value: -8192 to 8191
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_ACCELERATION = 0x0020000b;
	/**
	 * <p>A constant describing the device id of a "position" device.
	 * <p>Data of the device shows the position of a robot on a navigation pad.
	 * The first value of the integer array is the x-coordinate value and the second value is the the y-coordinate value of the position.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x0020000c
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 2
	 *             <li>Value: -1 to 39999 (-1: invalid value)
	 *             <li>Initial Value: -1 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_POSITION = 0x0020000c;
	/**
	 * <p>A constant describing the device id of an "orientation" device.
	 * <p>Data of the device shows the orientation of a robot on a navigation pad.
	 * The direction of x-axis of the navigation pad is 0 degrees.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x0020000d
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -179 to 180[<sup>o</sup>] or -200[<sup>o</sup>] (-200: invalid value)
	 *             <li>Initial Value: -200 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_ORIENTATION = 0x0020000d;
	/**
	 * <p>A constant describing the device id of a "light" device.
	 * <p>Data of the device shows the brightness of the surroundings of a robot.
	 * The brighter the surroundings are, the greater the data value is.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x0020000e
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 65535
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_LIGHT = 0x0020000e;
	/**
	 * <p>A constant describing the device id of a "temperature" device.
	 * <p>Data of the device shows the temperature in a robot.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x0020000f
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -40 to 88 [<sup>o</sup>C]
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_TEMPERATURE = 0x0020000f;
	/**
	 * <p>A constant describing the device id of a "battery" device.
	 * <p>Data of the device shows the battery remains of a robot in %.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200010
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 100 [%]
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_BATTERY = 0x00200010;
	/**
	 * <p>A constant describing the device id of an "front oid" device.
	 * <p>Data of the device shows the value of a front OID sensor at the bottom of a robot.
	 * It shows -1 when the sensor cannot read OID data.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200011
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -1 to 65535 (-1: invalid value)
	 *             <li>Initial Value: -1 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_FRONT_OID = 0x00200011;
	/**
	 * <p>A constant describing the device id of an "back oid" device.
	 * <p>Data of the device shows the value of a back OID sensor at the bottom of a robot.
	 * It shows -1 when the sensor cannot read OID data.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x00200012
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -1 to 65535 (-1: invalid value)
	 *             <li>Initial Value: -1 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_BACK_OID = 0x00200012;
	
	public static final int COMMAND_NOTE = 0x00200015;
	public static final int SENSOR_SIGNAL_STRENGTH = 0x00200016;
}