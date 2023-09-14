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
 * <p>Defines constants describing the model id of a pen and the device id of each device composing a pen.
 * <p>In general, we need not distinguish physical pens because most of applications use one pen.
 * In this case, we can get the reference of {@link org.roboid.robot.Device Device} object using {@link org.roboid.robot.Robot#findDeviceById(int) findDeviceById(int deviceId)} method of {@link org.roboid.robot.Robot Robot} class
 * and then read data of the device using <i>read</i> methods.
 * General patterns are:
 * </p>
 * <ul>
 * 	<li>To read data directly from the device
 * <pre>
 * void someMethod(Robot robot)
 {
     Device device = robot.findDeviceById(Pen.SENSOR_SIGNAL); // obtains the "signal" device of any pen.
     int v = device.read(); // reads data.
 }</pre>
 *	<li>Or to obtain data from the callback method of a listener
 * <pre>
 * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int v;
     switch(device.getId())
     {
     case Pen.SENSOR_SIGNAL: // obtains data of the "signal" device for any pen.
         v = ((int[])values)[0];
         break;
     }
 }</pre>
 * </ul>
 * <p>When we want to distinguish physical pens, we have to get the reference of {@link org.roboid.robot.Device Device} object using {@link org.roboid.robot.Robot#findDeviceById(int, int) findDeviceById(int productId, int deviceId)} method of {@link org.roboid.robot.Robot Robot} class
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
     Device device1 = robot.findDeviceById(1, Pen.SENSOR_SIGNAL); // obtains the "signal" device of pen #1.
     Device device2 = robot.findDeviceById(2, Pen.SENSOR_SIGNAL); // obtains the "signal" device of pen #2.
     int v1 = device1.read(); // reads data for pen #1.
     int v2 = device2.read(); // reads data for pen #2.
 }</pre>
 *	<li>Or to obtain data from the callback method of a listener
 * <pre>
 * public void onDeviceDataChanged(Device device, Object values, long timestamp)
 {
     int v1, v2;
     switch(device.getId())
     {
     case Pen.SENSOR_SIGNAL:
         if(device.getProductId() == 1) // obtains data of the "signal" device for pen #1.
             v1 = ((int[])values)[0];
         else if(device.getProductId() == 2) // obtains data of the "signal" device for pen #2.
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
public final class Pen
{
	/**
	 * <p>A constant string describing the model id of a pen.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: "kr.robomation.peripheral.pen"
	 * </ul>
	 */
	public static final String ID = "kr.robomation.peripheral.pen";
	
	/**
	 * <p>A constant describing the device id of a "signal" device.
	 * <p>Data of the device shows the signal strength of wireless communication.
	 * The stronger the signal is, the greater the data value is.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80100000
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
	public static final int SENSOR_SIGNAL = 0x80100000;
	/**
	 * <p>A constant describing the device id of a "battery" device.
	 * <p>Data of the device shows the battery remains of a pen in %.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80100001
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
	public static final int SENSOR_BATTERY = 0x80100001;
	/**
	 * <p>A constant describing the device id of an "oid" device.
	 * <p>Data of the device shows the value of an OID sensor.
	 * It shows -1 when the sensor cannot read OID data.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80100002
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
	public static final int EVENT_OID = 0x80100002;
	/**
	 * <p>A constant describing the device id of a "button" device.
	 * <p>Data of the device shows the status of a button.
	 * The data value is 1 when a button is pressed, and the data value is 0 when it is released.
	 * </p>
	 * <ul>
	 *     <li>Constant Value: 0x80100003
	 *     <li>Data
	 *         <ul>
	 *             <li>Type: int [ ]
	 *             <li>Length: 1
	 *             <li>Value: 0 or 1 (0: released, 1: pressed)
	 *             <li>Initial Value: 0
	 *         </ul>
	 *     </li>
	 * </ul>
	 */
	public static final int EVENT_BUTTON = 0x80100003;
}