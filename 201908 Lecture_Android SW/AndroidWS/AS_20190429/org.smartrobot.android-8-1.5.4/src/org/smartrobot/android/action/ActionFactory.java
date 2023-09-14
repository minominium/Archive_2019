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

/**
 * @author akaii@kw.ac.kr (Kwang-Hyun Park)
 */
final class ActionFactory
{
	static Action create(String id)
	{
		if(Action.VoiceRecognition.ID.equals(id))
			return new VoiceRecognitionAction();
		else if(Action.Microphone.ID.equals(id))
			return new MicrophoneAction();
		else if(Action.WalkieTalkie.ID.equals(id))
			return new WalkieTalkieAction();
		else if(Action.Vibration.ID.equals(id))
			return new VibrationAction();
		else if(Action.Tts.ID.equals(id))
			return new TtsAction();
		else if(Action.Navigation.ID.equals(id))
			return new NavigationAction();
		else if(Action.Localization.ID.equals(id))
			return new LocalizationAction();
		else if(Action.Tile.ID.equals(id))
			return new TileAction();
		return null;
	}
}