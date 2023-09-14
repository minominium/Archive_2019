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

package org.smartrobot.android.action;

import org.json.JSONArray;

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
public abstract class AbstractAction extends Action
{
	AbstractAction(int size, int tag)
	{
		super(size, tag);
	}
	
	public abstract void encodeXml(StringBuilder sb, long timestamp);
	public abstract void encodeJson(StringBuilder sb, long timestamp);
	public abstract boolean decodeJson(JSONArray jsonArray);
}