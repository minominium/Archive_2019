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

package org.smartrobot.android.clip;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

public final class ClipPlayer
{
	private static final int ERROR_NONE = 0;
	public static final int ERROR_INVALID_ID = -1;
	public static final int ERROR_INVALID_CONTEXT = -2;
	public static final int ERROR_INVALID_URL = -3;
	public static final int ERROR_INVALID_PACKAGE = -4;
	public static final int ERROR_INVALID_RESOURCE = -5;
	public static final int ERROR_INVALID_CLIP = -6;
	public static final int ERROR_INVALID_FILE = -7;
	public static final int ERROR_ILLEGAL_STATE = -8;
	
	private static final int MSG_COMPLETION = 1;
	private static final int MSG_ERROR = 2;
	
	public interface OnCompletedListener
	{
		void onCompleted(ClipPlayer clipPlayer);
	}
	
	public interface OnErrorListener
	{
		void onError(ClipPlayer clipPlayer, int errorCode);
	}
	
	private WeakReference<Context> mContext;
	OnCompletedListener mOnCompletedListener;
	OnErrorListener mOnErrorListener;
	static final SparseArray<ClipPlayer> mClipPlayers = new SparseArray<ClipPlayer>();
	final String mRequestCode;
	private final int mID;
	private boolean mOpen;
	boolean mPlaying;
	private BroadcastReceiver mBR;
	private final EventHandler mEventHandler;
	
	private ClipPlayer(String packageName, int id)
	{
		mID = id;
		mRequestCode = packageName + "_" + id;
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
	
	public static ClipPlayer obtain(Context context, int clipPlayerId)
	{
		if(context == null) return null;
		String packageName = context.getPackageName();
		if(packageName == null) return null;
		ClipPlayer clipPlayer = null;
		synchronized(mClipPlayers)
		{
			clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null)
			{
				clipPlayer = new ClipPlayer(packageName, clipPlayerId);
				mClipPlayers.put(clipPlayerId, clipPlayer);
			}
		}
		clipPlayer.setContext(context);
		return clipPlayer;
	}
	
	public static void closeAll()
	{
		synchronized(mClipPlayers)
		{
			int sz = mClipPlayers.size();
			for(int i = 0; i < sz; ++i)
			{
				mClipPlayers.valueAt(i).release();
			}
			mClipPlayers.clear();
		}
	}
	
	public int getId()
	{
		return mID;
	}
	
	private Context getContext()
	{
		if(mContext == null) return null;
		return mContext.get();
	}
	
	void setContext(Context context)
	{
		Context applicationContext = context.getApplicationContext();
		if(applicationContext == null)
			mContext = new WeakReference<Context>(context);
		else
			mContext = new WeakReference<Context>(applicationContext);
	}
	
	public void setOnCompletedListener(OnCompletedListener listener)
	{
		mOnCompletedListener = listener;
	}
	
	public void setOnErrorListener(OnErrorListener listener)
	{
		mOnErrorListener = listener;
	}
	
	public boolean open(String url)
	{
		if(url == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_URL);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_URL", url);
		context.sendBroadcast(intent);
		return true;
	}
	
	public boolean open(String packageName, int resid)
	{
		if(packageName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_PACKAGE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_PACKAGE_NAME", packageName);
		intent.putExtra("roboid.intent.extra.CLIP_RESOURCE_ID", resid);
		context.sendBroadcast(intent);
		return true;
	}
	
	public boolean open(String packageName, String resName)
	{
		if(packageName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_PACKAGE);
			return false;
		}
		if(resName == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_RESOURCE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mOpen) close();
		mOpen = true;
		
		synchronized(mClipPlayers)
		{
			ClipPlayer player = mClipPlayers.get(getId());
			if(player == null)
				mClipPlayers.put(getId(), this);
		}
		
		registerBroadcast(context);
		
		Intent intent = new Intent("roboid.intent.action.CLIP_OPEN");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		intent.putExtra("roboid.intent.extra.CLIP_PACKAGE_NAME", packageName);
		intent.putExtra("roboid.intent.extra.CLIP_RESOURCE_ID", resName);
		context.sendBroadcast(intent);
		return true;
	}
	
	private void registerBroadcast(Context context)
	{
		if(mBR != null) return;
		mBR = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				if("roboid.intent.action.CLIP_COMPLETION".equals(action))
				{
					if(mEventHandler != null)
					{
						Message msg = mEventHandler.obtainMessage(MSG_COMPLETION);
						msg.obj = intent;
						msg.sendToTarget();
					}
				}
				else if("roboid.intent.action.CLIP_ERROR".equals(action))
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
		intentFilter.addAction("roboid.intent.action.CLIP_COMPLETION");
		intentFilter.addAction("roboid.intent.action.CLIP_ERROR");
		context.registerReceiver(mBR, intentFilter);
	}
	
	public void close()
	{
		release();
		synchronized(mClipPlayers)
		{
			mClipPlayers.remove(getId());
		}
	}
	
	private void release()
	{
		if(mPlaying)
			stop();
		mOpen = false;

		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
		}
		else
		{
			if(mRequestCode != null)
			{
				Intent intent = new Intent("roboid.intent.action.CLIP_CLOSE");
				intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
				context.sendBroadcast(intent);
			}
			if(mBR != null)
			{
				context.unregisterReceiver(mBR);
				mBR = null;
			}
		}
	}
	
	public boolean play()
	{
		if(mOpen == false)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		if(mPlaying)
			stop();
		mPlaying = true;
		
		Intent intent = new Intent("roboid.intent.action.CLIP_PLAY");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		context.sendBroadcast(intent);
		return true;
	}
	
	public boolean stop()
	{
		if(mOpen == false)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_ILLEGAL_STATE);
			return false;
		}
		if(mRequestCode == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_ID);
			return false;
		}
		Context context = getContext();
		if(context == null)
		{
			if(mOnErrorListener != null)
				mOnErrorListener.onError(this, ERROR_INVALID_CONTEXT);
			return false;
		}
		mPlaying = false;
		
		Intent intent = new Intent("roboid.intent.action.CLIP_STOP");
		intent.putExtra("roboid.intent.extra.CLIP_REQUEST_CODE", mRequestCode);
		context.sendBroadcast(intent);
		return true;
	}
	
	private static class EventHandler extends Handler
	{
		private final ClipPlayer mClipPlayer;
		
		EventHandler(ClipPlayer clipPlayer)
		{
			super();
			mClipPlayer = clipPlayer;
		}
		
		EventHandler(ClipPlayer clipPlayer, Looper looper)
		{
			super(looper);
			mClipPlayer = clipPlayer;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_COMPLETION:
				{
					if(mClipPlayer == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String requestCode = intent.getStringExtra("roboid.intent.extra.CLIP_REQUEST_CODE");
					if(requestCode == null || !requestCode.equals(mClipPlayer.mRequestCode))
						return;
					
					mClipPlayer.mPlaying = false;
					ClipPlayer.OnCompletedListener listener = mClipPlayer.mOnCompletedListener;
					if(listener != null)
						listener.onCompleted(mClipPlayer);
				}
				break;
			case MSG_ERROR:
				{
					if(mClipPlayer == null) return;
					Intent intent = (Intent)msg.obj;
					if(intent == null) return;
					
					String requestCode = intent.getStringExtra("roboid.intent.extra.CLIP_REQUEST_CODE");
					if(requestCode == null || !requestCode.equals(mClipPlayer.mRequestCode))
						return;
					
					int errorCode = intent.getIntExtra("roboid.intent.extra.CLIP_ERROR", ERROR_NONE);
					if(errorCode != ERROR_NONE)
					{
						ClipPlayer.OnErrorListener listener = mClipPlayer.mOnErrorListener;
						if(listener != null)
							listener.onError(mClipPlayer, errorCode);
					}
				}
				break;
			}
		}
	}
}
