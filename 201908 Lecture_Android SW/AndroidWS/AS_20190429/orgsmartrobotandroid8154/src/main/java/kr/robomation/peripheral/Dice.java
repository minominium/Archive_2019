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

package kr.robomation.peripheral;

/**
 * <p>Defines constants describing the model id of a dice and the device id of each device composing a dice.
 * <p>In general, we need not distinguish physical dice because most of applications use one dice.
 * In this case, we can get the reference of {@link org.roboid.robot.Device Device} object using {@link org.roboid.robot.Robot#findDeviceById(int) findDeviceById(int deviceId)} method of {@link org.roboid.robot.Robot Robot} class
 * and then read data of the device using <i>read</i> methods.
 * General patterns are:
 * </p>
 * <ul>
 * 	<li>To read data directly from the device
 * <pre>
 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(Dice.SENSOR_SIGNAL); // obtains the "signal" device of any dice.
     int v = device.read(); // reads data.
 }</pre>
 *	<li>Or to obtain data from the callback method of a listener
 * <pre>
 * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int v;
     switch(device.getId())
     {
     case Dice.SENSOR_SIGNAL: // obtains data of the "signal" device for any dice.
         v = ((int[])values)[0];
         break;
     }
 }</pre>
 * </ul>
 * <p>When we want to distinguish physical dice, we have to get the reference of {@link org.roboid.robot.Device Device} object using {@link org.roboid.robot.Robot#findDeviceById(int, int) findDeviceById(int productId, int deviceId)} method of {@link org.roboid.robot.Robot Robot} class
 * and then read data of the device using <i>read</i> methods.
 * The product id is displayed when peripherals such as a pen or dice are registered in Smart Robot Launcher,
 * and we can get the product id using {@link org.roboid.robot.Device#getProductId() getProductId()} method of {@link org.roboid.robot.Device Device} class.
 * General patterns are:
 * </p>
 * <ul>
 * 	<li>To read data directly from the device
 * <pre>
 * void someMethod(Robot robot)
 {
     Device device1 = robot.findDeviceById(1, Dice.SENSOR_SIGNAL); // obtains the "signal" device of dice #1.
     Device device2 = robot.findDeviceById(2, Dice.SENSOR_SIGNAL); // obtains the "signal" device of dice #2.
     int v1 = device1.read(); // reads data for dice #1.
     int v2 = device2.read(); // reads data for dice #2.
 }</pre>
 *	<li>Or to obtain data from the callback method of a listener
 * <pre>
 * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int v1, v2;
     switch(device.getId())
     {
     case Dice.SENSOR_SIGNAL:
         if(device.getProductId() == 1) // obtains data of the "signal" device for dice #1.
             v1 = ((int[])values)[0];
         else if(device.getProductId() == 2) // obtains data of the "signal" device for dice #2.
             v2 = ((int[])values)[0];
         break;
     }
 }</pre>
 * </ul>
 *
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 * <p>
 * @see org.roboid.robot.Robot Robot
 * @see org.roboid.robot.Device Device
 * @see org.roboid.robot.Device.DeviceDataChangedListener Device.DeviceDataChangedListener
 */
public final class Dice
{
	/**
	 * <p>A constant string describing the model id of a dice.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: "kr.robomation.peripheral.dice"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.peripheral.dice";
	
	/**
	 * <p>A constant describing the device id of a "signal" device.
	 * <p>Data of the device shows the signal strength of wireless communication.
	 * The stronger the signal is, the greater the data value is.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80200000
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 to 255
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int SENSOR_SIGNAL = 0x80200000;
	/**
	 * <p>A constant describing the device id of a "temperature" device.
	 * <p>Data of the device shows the temperature in a dice.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80200001
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
	public static final int SENSOR_TEMPERATURE = 0x80200001;
	/**
	 * <p>A constant describing the device id of a "battery" device.
	 * <p>Data of the device shows the battery remains of a dice in %.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80200002
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
	public static final int SENSOR_BATTERY = 0x80200002;
	/**
	 * <p>A constant describing the device id of an "acceleration" device.
	 * <p>Data of the device shows the 3-axis acceleration values of a dice.
	 * The first value of the integer array is the acceleration value of x-axis, the second value that of y-axis, and the third value that of z-axis.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80200003
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
	public static final int SENSOR_ACCELERATION = 0x80200003;
	/**
	 * <p>A constant describing the device id of a "fall" device.
	 * <p>Data of the device shows the id of free fall.
	 * The id increases whenever the dice is thrown to the air.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80200004
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -1 to 255 (-1: invalid value)
	 *             <li>Initial Value: -1 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_FALL = 0x80200004;
	/**
	 * <p>A constant describing the device id of a "value" device.
	 * <p>Data of the device shows the spots of a dice.
	 * When the face of a dice is directed upward vertically, data has the value between 1 to 6, while when the dice is slanted, data has the value between -1 to -6.
	 * In case of negative value (when the dice is slanted) the absolute value represents the spots nearest to the face value.
	 * For example, if the face of the dice shows three spots and it is vertically standing, the value is 3.
	 * If it is slanted, the value is -3.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80200005
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: -6 to 6 (0: invalid value)
	 *             <li>Initial Value: 0 (invalid value)
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_VALUE = 0x80200005;
}