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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.roboid.robot.Device.DeviceDataChangedListener;
import org.roboid.robot.impl.RoboidImpl;
import org.smartrobot.android.ipc.ac;
import org.smartrobot.android.ipc.dc;
import org.smartrobot.android.ipc.dr;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

public abstract class Action extends RoboidImpl
{
	private static final int STATE_NONE = 0;
	public static final int STATE_PREPARED = 1;
	public static final int STATE_ACTIVATED = 2;
	public static final int STATE_DEACTIVATED = 3;
	public static final int STATE_DISPOSED = 4;
	
	private static final int ERROR_NONE = 0;
	public static final int ERROR_INVALID_ID = -1;
	public static final int ERROR_INVALID_CONTEXT = -2;
	public static final int ERROR_ILLEGAL_STATE = -3;
	public static final int ERROR_NOT_SUPPORTED = -4;
	public static final int ERROR_SECURITY = -5;
	
	public final class VoiceRecognition
	{
		public static final String ID = "org.smartrobot.android.action.voicerecognition";
		public static final int COMMAND_LANGUAGE_MODEL = 0x40100000;
		public static final int COMMAND_LANGUAGE = 0x40100001;
		public static final int COMMAND_COUNTRY = 0x40100002;
		public static final int COMMAND_VARIANT = 0x40100003;
		public static final int COMMAND_VISIBILITY = 0x40100004;
		public static final int COMMAND_PROMPT = 0x40100005;
		public static final int COMMAND_MAX_RESULTS = 0x40100006;
		public static final int SENSOR_MIC_LEVEL = 0x40100007;
		public static final int EVENT_TEXT = 0x40100008;

		public static final int STATE_BEGINNING_OF_SPEECH = 101;
		public static final int STATE_END_OF_SPEECH = 103;
		public static final int STATE_READY_FOR_SPEECH = 105;
		public static final int ERROR_NETWORK_TIMEOUT = -101;
		public static final int ERROR_NETWORK = -102;
		public static final int ERROR_AUDIO = -103;
		public static final int ERROR_SERVER = -104;
		public static final int ERROR_CLIENT = -105;
		public static final int ERROR_SPEECH_TIMEOUT = -106;
		public static final int ERROR_NO_MATCH = -107;
		public static final int ERROR_RECOGNIZER_BUSY = -108;
	}
	
	public final class Microphone
	{
		public static final String ID = "org.smartrobot.android.action.microphone";
		public static final int SENSOR_LEVEL = 0x40200000;
	}
	
	public final class WalkieTalkie
	{
		public static final String ID = "org.smartrobot.android.action.walkietalkie";
		public static final int EFFECTOR_SENSITIVITY = 0x40300000;
	}
	
	public final class Vibration
	{
		public static final String ID = "org.smartrobot.android.action.vibration";
		public static final int COMMAND_TIME = 0x40400000;
		public static final int COMMAND_PATTERN = 0x40400001;
		public static final int COMMAND_REPEAT = 0x40400002;
	}
	
	public final class Tts
	{
		public static final String ID = "org.smartrobot.android.action.tts";
		public static final int COMMAND_ENGINE = 0x40500000;
		public static final int COMMAND_LANGUAGE = 0x40500001;
		public static final int COMMAND_COUNTRY = 0x40500002;
		public static final int COMMAND_VARIANT = 0x40500003;
		public static final int COMMAND_PITCH = 0x40500004;
		public static final int COMMAND_SPEECH_RATE = 0x40500005;
		public static final int COMMAND_TEXT = 0x40500006;
		public static final int ERROR_LANG_NOT_AVAILABLE = -100;
	}

	public final class Navigation
	{
		public static final String ID = "org.smartrobot.android.action.navigation";
		public static final int COMMAND_PAD_SIZE = 0x40600005;
		public static final int COMMAND_INITIAL_POSITION = 0x40600000;
		public static final int COMMAND_WAYPOINTS = 0x40600001;
		public static final int COMMAND_FINAL_ORIENTATION = 0x40600002;
		public static final int COMMAND_MAX_SPEED = 0x40600003;
		public static final int COMMAND_CURVATURE = 0x40600004;
	}
	
	public final class Localization
	{
		public static final String ID = "org.smartrobot.android.action.localization";
		public static final int COMMAND_PAD_SIZE = 0x40700000;
		public static final int COMMAND_OID = 0x40700001;
		public static final int SENSOR_POSITION = 0x40700002;
	}
	
	public final class Tile
	{
		public static final String ID = "org.smartrobot.android.action.tile";
		public static final int COMMAND_PAD_SIZE = 0x40800000;
		public static final int COMMAND_TILE_SIZE = 0x40800001;
		public static final int COMMAND_INITIAL_DIRECTION = 0x40800002;
		public static final int COMMAND_MOTION = 0x40800003;
		public static final int EVENT_STATE = 0x40800004;
		public static final int LEFT = 1;
		public static final int RIGHT = 2;
		public static final int TOP = 3;
		public static final int BOTTOM = 4;
		public static final int STATE_TILE_CHANGED = 1;
		public static final int STATE_WAITING = 2;
		public static final int STATE_OUTSIDE = 3;
		public static final int MOTION_MOVE_FORWARD = 1;
		public static final int MOTION_TURN_LEFT = 2;
		public static final int MOTION_TURN_RIGHT = 3;
		public static final int MOTION_TURN_180 = 4;
		public static final int MOTION_FACE_LEFT = 5;
		public static final int MOTION_FACE_RIGHT = 6;
		public static final int MOTION_FACE_TOP = 7;
		public static final int MOTION_FACE_BOTTOM = 8;
		public static final int MOTION_WAIT = 9;
	}
	
	public interface OnStateChangedListener
	{
		void onStateChanged(Action action, int state);
	}
	
	public interface OnCompletedListener
	{
		void onCompleted(Action action);
	}
	
	public interface OnErrorListener
	{
		void onError(Action action, int errorCode);
	}

	private static final int MSG_ACK = 1;
	private static final int MSG_STATE = 2;
	private static final int MSG_COMPLETION = 3;
	private static final int MSG_ERROR = 4;
	
	private static final HashMap<String, Action> mActions = new HashMap<String, Action>();
	private WeakReference<Context> mContext;
	OnStateChangedListener mOnStateChangedListener;
	OnCompletedListener mOnCompletedListener;
	OnErrorListener mOnErrorListener;
	private boolean mPrepared;
	private boolean mDisposed;
	boolean mActive;
	private final ArrayList<Intent> mIntents = new ArrayList<Intent>();
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	
	private ac mActionBinder;
	private final ServiceConnection mActionConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			mActionBinder = ac.Stub.asInterface(binder);
			try
			{
				mActionBinder.a(mDataChangedCallback);
				mActionBinder.c(mDataRequestCallback);
			} catch (RemoteException e)
			{
			}
			
			mPrepared = true;
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(Action.this, Action.ERROR_INVALID_CONTEXT);
			}
			else
			{
				for(Intent i : mIntents)
					context.sendBroadcast(i);
			}
			mIntents.clear();
			if(mOnStateChangedListener != null)
				mOnStateChangedListener.onStateChanged(Action.this, Action.STATE_PREPARED);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mActionBinder = null;
		}
	};
	private final dc.Stub mDataChangedCallback = new dc.Stub()
	{
		@Override
		public void a(byte[] a1, long a2) throws RemoteException
		{
			if(a1 != null)
				handleSimulacrum(a1, a2);
			updateDeviceState();
		}

		@Override
		public void b(int b1, int b2, byte[] b3, long b4) throws RemoteException
		{
		}
	};
	private final dr.Stub mDataRequestCallback = new dr.Stub()
	{
		@Override
		public byte[] a() throws RemoteException
		{
			if(mActionBinder == null) return null;
			return encodeSimulacrum();
		}

		@Override
		public byte[] b(int b1, int b2) throws RemoteException
		{
			return null;
		}
	};
	
	Action(int size, int tag)
	{
		super(size, tag);
		
		Looper looper;
		if((looper = Looper.myLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else if((looper = Looper.getMainLooper()) != null)
		{
			mEventHandler = new EventHandler(this, looper);
		}
		else
			mEventHandler = new EventHandler(this);
	}
	
	public static Action obtain(Context context, String actionId)
	{
		if(context == null || actionId == null) return null;
		Action action = null;
		synchronized(mActions)
		{
			action = mActions.get(actionId);
			if(action == null)
			{
				action = ActionFactory.create(actionId);
				if(action != null)
				{
					mActions.put(actionId, action);
					action.setContext(context);
					action.registerBroadcast();
					Intent intent = new Intent("roboid.intent.action.ACTION_REQ");
					intent.putExtra("roboid.intent.extra.PACKAGE_NAME", context.getPackageName());
					intent.putExtra("roboid.intent.extra.ACTION_ID", actionId);
					context.sendBroadcast(intent);
				}
			}
			else
				action.setContext(context);
		}
		return action;
	}
	
	public static void disposeAll()
	{
		synchronized(mActions)
		{
			for(Action action : mActions.values())
				action.release();
			mActions.clear();
		}
	}
	
	Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	private void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	public void setOnStateChangedListener(OnStateChangedListener listener)
	{
		mOnStateChangedListener = listener;
	}
	
	public void setOnCompletedListener(OnCompletedListener listener)
	{
		mOnCompletedListener = listener;
	}
	
	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}
	
	private void registerBroadcast()
	{
		if(mBR != null) return;
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			return;
		}

		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.ACTION_ACK".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ACK);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_STATE".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_STATE);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_COMPLETION".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_COMPLETION);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.ACTION_ERROR".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_ERROR);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("roboid.intent.action.ACTION_ACK");
		intentFilter.addAction("roboid.intent.action.ACTION_STATE");
		intentFilter.addAction("roboid.intent.action.ACTION_COMPLETION");
		intentFilter.addAction("roboid.intent.action.ACTION_ERROR");
		context.registerReceiver(mBR, intentFilter);
	}
	
	boolean connect(Intent intent)
	{
		if(intent == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_NOT_SUPPORTED);
			return false;
		}
		
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			return false;
		}
		
		try
		{
			context.bindService(intent, mActionConnection, Context.BIND_AUTO_CREATE);
		} catch (SecurityException e)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, Action.ERROR_SECURITY);
		}
		return true;
	}

	void disconnect()
	{
		if(mActionBinder == null) return;
		
		try
		{
			mActionBinder.b(mDataChangedCallback);
			mActionBinder.d(mDataRequestCallback);
			mActionBinder = null;
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(this, Action.ERROR_INVALID_CONTEXT);
			}
			else
				context.unbindService(mActionConnection);
		} catch (RemoteException e)
		{
		}
	}
	
	public void dispose()
	{
		release();
		synchronized(mActions)
		{
			mActions.remove(getId());
		}
	}
	
	private void release()
	{
		if(mActive)
			deactivate();
		
		if(mEventHandler != null)
		{
			mEventHandler.removeMessages(MSG_ACK);
			mEventHandler.removeMessages(MSG_STATE);
			mEventHandler.removeMessages(MSG_COMPLETION);
			mEventHandler.removeMessages(MSG_ERROR);
		}
		disconnect();
		
		if(mBR != null)
		{
			Context context = getContext();
			if(context == null)
			{
				if(mOnErrorListener != null)
					mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			}
			else
				context.unregisterReceiver(mBR);
			mBR = null;
		}
		mPrepared = false;
		mDisposed = true;
		mIntents.clear();
		
		if(mOnStateChangedListener != null)
			mOnStateChangedListener.onStateChanged(this, STATE_DISPOSED);
		
		mOnStateChangedListener = null;
		mOnCompletedListener = null;
		mOnErrorListener = null;
	}
	
	public boolean activate()
	{
		if(mDisposed)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mActive)
			deactivate();
		mActive = true;
		
		Intent intent = new Intent("roboid.intent.action.ACTION_ACTIVATE");
		intent.putExtra("roboid.intent.extra.ACTION_ID", getId());
		if(mPrepared)
			context.sendBroadcast(intent);
		else
			mIntents.add(intent);
		return true;
	}
	
	public boolean deactivate()
	{
		if(mDisposed)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		mActive = false;
		
		Intent intent = new Intent("roboid.intent.action.ACTION_DEACTIVATE");
		intent.putExtra("roboid.intent.extra.ACTION_ID", getId());
		if(mPrepared)
			context.sendBroadcast(intent);
		else
			mIntents.add(intent);
		return true;
	}
	
	void handleSimulacrum(byte[] simulacrum, long timestamp)
	{
		if(decodeSimulacrum(simulacrum))
		{
			synchronized(mListeners)
			{
				for(DeviceDataChangedListener listener : mListeners)
					notifyDataChanged(listener, timestamp);
			}
		}
	}
	
	abstract boolean decodeSimulacrum(byte[] simulacrum);
	abstract void notifyDataChanged(DeviceDataChangedListener listener, long timestamp);
	
	byte[] encodeSimulacrum()
	{
		return null;
	}
	
	private static class EventHandler extends Handler
	{
		private final Action mAction;
		
		EventHandler(Action action)
		{
			super();
			mAction = action;
		}
		
		EventHandler(Action action, Looper looper)
		{
			super(looper);
			mAction = action;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_ACK:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					Context context = mAction.getContext();
					if(context == null)
					{
						Action.OnErrorListener listener = mAction.mOnErrorListener;
						if(listener != null)
							listener.onError(mAction, Action.ERROR_INVALID_CONTEXT);
						return;
					}
					
					String packageName = intent.getStringExtra("roboid.intent.extra.PACKAGE_NAME");
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(packageName == null || !packageName.equals(context.getPackageName())) return;
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					Intent i = intent.getParcelableExtra("roboid.intent.extra.ACTION");
					mAction.connect(i);
				}
				break;
			case MSG_STATE:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					int state = intent.getIntExtra("roboid.intent.extra.ACTION_STATE", STATE_NONE);
					if(state != STATE_NONE)
					{
						Action.OnStateChangedListener listener = mAction.mOnStateChangedListener;
						if(listener != null)
							listener.onStateChanged(mAction, state);
					}
				}
				break;
			case MSG_COMPLETION:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					mAction.mActive = false;
					Action.OnCompletedListener listener = mAction.mOnCompletedListener;
					if(listener != null)
						listener.onCompleted(mAction);
				}
				break;
			case MSG_ERROR:
				{
					if(mAction == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String actionId = intent.getStringExtra("roboid.intent.extra.ACTION_ID");
					if(actionId == null || !actionId.equals(mAction.getId())) return;
					
					int errorCode = intent.getIntExtra("roboid.intent.extra.ACTION_ERROR", ERROR_NONE);
					if(errorCode != ERROR_NONE)
					{
						Action.OnErrorListener listener = mAction.mOnErrorListener;
						if(listener != null)
							listener.onError(mAction, errorCode);
					}
				}
				break;
			}
		}
	}
}