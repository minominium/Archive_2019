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

package org.smartrobot.android.web;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.roboid.robot.Robot;
import org.smartrobot.android.AbstractRobot;
import org.smartrobot.android.SmartRobot;
import org.smartrobot.android.action.AbstractAction;
import org.smartrobot.android.action.Action;
import org.smartrobot.android.clip.ClipPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RobotWebView extends WebView implements SmartRobot.Callback
{
	final SmartRobotHandler mSmartRobotHandler = new SmartRobotHandler();
	final HashMap<String, Action> mActions = new HashMap<String, Action>();
	final ArrayList<String> mMessages = new ArrayList<String>();
	private WebThread mWebThread;
	private final Object mLock = new Object();
	Robot mRobot;
	boolean mInitialized;
	boolean mActivated;
	boolean mPageLoaded;
	
	public RobotWebView(Context context)
	{
		super(context);
		init();
	}
	
	public RobotWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public RobotWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	
	private void init()
	{
		getSettings().setJavaScriptEnabled(true);
		addJavascriptInterface(mSmartRobotHandler, "SmartRobotHandler");
		setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				mPageLoaded = false;
			}

			@Override
			public void onPageFinished(WebView view, String url)
			{
				if(mInitialized)
					initialize();
				if(mActivated)
					loadUrl("javascript:onActivated()");
				mPageLoaded = true;
			}
		});
	}
	
	void startWebThread()
	{
		synchronized(mLock)
		{
			WebThread oldThread = mWebThread;
			mWebThread = null;
			if(oldThread != null)
				oldThread.cancel();
			WebThread newThread = new WebThread();
			newThread.start();
			mWebThread = newThread;
		}
	}
	
	void stopWebThread()
	{
		synchronized(mLock)
		{
			WebThread oldThread = mWebThread;
			mWebThread = null;
			if(oldThread != null)
				oldThread.cancel();
		}
	}
	
	void initialize()
	{
		Robot robot = mRobot;
		if(robot == null) return;
		StringBuilder sb = new StringBuilder("javascript:SmartRobot.initialized('");
		sb.append(robot.getId());
		sb.append("','");
		sb.append(robot.getName());
		sb.append("')");
		loadUrl(sb.toString());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInitialized(Robot robot)
	{
		if(robot == null) return;
		mRobot = robot;
		if(mPageLoaded)
			initialize();
		mInitialized = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivated()
	{
		if(mPageLoaded)
			loadUrl("javascript:onActivated()");
		startWebThread();
		mActivated = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDeactivated()
	{
		mActivated = false;
		stopWebThread();
		if(mPageLoaded)
			loadUrl("javascript:onDeactivated()");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisposed()
	{
		mInitialized = false;
		if(mPageLoaded)
			loadUrl("javascript:onDisposed()");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExecute()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStateChanged(int state)
	{
		if(!mPageLoaded) return;
		StringBuilder sb = new StringBuilder("javascript:onStateChanged(");
		sb.append(state);
		sb.append(")");
		loadUrl(sb.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onNameChanged(String name)
	{
		if(!mPageLoaded) return;
		StringBuilder sb = new StringBuilder("javascript:SmartRobot.nameChanged('");
		sb.append(name);
		sb.append("')");
		loadUrl(sb.toString());
	}
	
	class WebThread extends Thread
	{
		private volatile boolean mStopped = false;
		
		@Override
		public void run()
		{
			long time = System.currentTimeMillis();
			long currentTime;
			while(!mStopped && !isInterrupted())
			{
				currentTime = System.currentTimeMillis();
				if(currentTime > time)
				{
					loadUrl("javascript:SmartRobot.encode()");
					if(mStopped) break;
					
					if(mRobot != null)
					{
						StringBuilder sb = new StringBuilder("javascript:SmartRobot.decode(");
						sb.append("{ \"simulacrum\" : [");
						long timestamp = System.nanoTime();
						((AbstractRobot)mRobot).encodeJson(sb, timestamp);
						synchronized(mActions)
						{
							for(Action action : mActions.values())
								((AbstractAction)action).encodeJson(sb, timestamp);
						}
						synchronized(mMessages)
						{
							for(String msg : mMessages)
								sb.append(msg);
							mMessages.clear();
						}
						sb.append("]})");
						if(mStopped) break;
						
						loadUrl(sb.toString());
					}
					time += 20;
				}
				
				if(mStopped) break;
				try
				{
					Thread.sleep(5);
				} catch (InterruptedException e)
				{
					break;
				}
			}
			mSmartRobotHandler.clear();
		}
		
		void cancel()
		{
			mStopped = true;
			interrupt();
		}
	}
	
	class SmartRobotHandler implements Action.OnStateChangedListener, Action.OnCompletedListener, Action.OnErrorListener, ClipPlayer.OnCompletedListener, ClipPlayer.OnErrorListener
	{
		private final SparseArray<ClipPlayer> mClipPlayers = new SparseArray<ClipPlayer>();
		
		public void decode(String json)
		{
			try
			{
				JSONArray jsonArray = new JSONArray(json);
				
				JSONArray ja;
				int type, command;
				String actionId = null;
				int clipPlayerId = 0;
				int sz = jsonArray.length();
				for(int i = 0; i < sz; ++i)
				{
					try
					{
						ja = jsonArray.getJSONArray(i);
						type = ja.getInt(0);
						switch(type)
						{
						case 0:
							{
								((AbstractRobot)mRobot).decodeJson(ja);
							}
							break;
						case 2:
							{
								Action action = null;
								synchronized(mActions)
								{
									action = mActions.get(ja.getString(1));
								}
								if(action != null)
									((AbstractAction)action).decodeJson(ja);
							}
							break;
						case 4:
							{
								command = ja.getInt(1);
								if(command != 4)
									actionId = ja.getString(2);
								switch(command)
								{
								case 0:
									obtainAction(actionId);
									break;
								case 1:
									disposeAction(actionId);
									break;
								case 2:
									activateAction(actionId);
									break;
								case 3:
									deactivateAction(actionId);
									break;
								case 4:
									disposeAllActions();
									break;
								}
							}
							break;
						case 5:
							{
								command = ja.getInt(1);
								if(command != 6)
									clipPlayerId = ja.getInt(2);
								switch(command)
								{
								case 0:
									obtainClipPlayer(clipPlayerId);
									break;
								case 1:
									openUrl(clipPlayerId, ja.getString(3));
									break;
								case 2:
									openResource(clipPlayerId, ja.getString(3), ja.getString(4));
									break;
								case 3:
									closeClipPlayer(clipPlayerId);
									break;
								case 4:
									playClipPlayer(clipPlayerId);
									break;
								case 5:
									stopClipPlayer(clipPlayerId);
									break;
								case 6:
									closeAllClipPlayers();
									break;
								}
							}
							break;
						}
					} catch (Exception e)
					{
					}
				}
			} catch (Exception e)
			{
			}
		}
		
		boolean obtainAction(String actionId)
		{
			if(actionId == null) return false;
			Action action = null;
			synchronized(mActions)
			{
				action = mActions.get(actionId);
				if(action != null) return true;
				
				action = Action.obtain(getContext(), actionId);
				if(action == null) return false;
				mActions.put(actionId, action);
			}
			action.setOnStateChangedListener(this);
			action.setOnCompletedListener(this);
			action.setOnErrorListener(this);
			return true;
		}
		
		void disposeAllActions()
		{
			synchronized(mActions)
			{
				for(Action action : mActions.values())
				{
					action.clearDeviceDataChangedListener();
				}
				mActions.clear();
			}
			Action.disposeAll();
		}
		
		void disposeAction(String actionId)
		{
			if(actionId == null) return;
			Action action = null;
			synchronized(mActions)
			{
				action = mActions.get(actionId);
				if(action == null) return;
				mActions.remove(actionId);
			}
			action.clearDeviceDataChangedListener();
			action.dispose();
		}
		
		boolean activateAction(String actionId)
		{
			if(actionId == null) return false;
			Action action = null;
			synchronized(mActions)
			{
				action = mActions.get(actionId);
			}
			if(action == null) return false;
			return action.activate();
		}
		
		boolean deactivateAction(String actionId)
		{
			if(actionId == null) return false;
			Action action = null;
			synchronized(mActions)
			{
				action = mActions.get(actionId);
			}
			if(action == null) return false;
			return action.deactivate();
		}
		
		boolean obtainClipPlayer(int clipPlayerId)
		{
			ClipPlayer clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer != null) return true;
			
			clipPlayer = ClipPlayer.obtain(getContext(), clipPlayerId);
			if(clipPlayer == null) return false;
			mClipPlayers.put(clipPlayerId, clipPlayer);
			clipPlayer.setOnCompletedListener(this);
			clipPlayer.setOnErrorListener(this);
			return true;
		}
		
		boolean openUrl(int clipPlayerId, String url)
		{
			ClipPlayer clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null) return false;
			return clipPlayer.open(url);
		}
		
		boolean openResource(int clipPlayerId, String packageName, String resName)
		{
			ClipPlayer clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null) return false;
			return clipPlayer.open(packageName, resName);
		}
		
		void closeAllClipPlayers()
		{
			ClipPlayer.closeAll();
		}
		
		void closeClipPlayer(int clipPlayerId)
		{
			ClipPlayer clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null) return;
			clipPlayer.close();
		}
		
		boolean playClipPlayer(int clipPlayerId)
		{
			ClipPlayer clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null) return false;
			return clipPlayer.play();
		}
		
		boolean stopClipPlayer(int clipPlayerId)
		{
			ClipPlayer clipPlayer = mClipPlayers.get(clipPlayerId);
			if(clipPlayer == null) return false;
			return clipPlayer.stop();
		}
		
		void clear()
		{
			disposeAllActions();
			closeAllClipPlayers();
			mClipPlayers.clear();
		}
		
		@Override
		public void onStateChanged(Action action, int state)
		{
			StringBuilder sb = new StringBuilder(",[4,1,'");
			sb.append(action.getId());
			sb.append("',");
			sb.append(state);
			sb.append("]");
			synchronized(mMessages)
			{
				mMessages.add(sb.toString());
			}
		}
		
		@Override
		public void onCompleted(Action action)
		{
			StringBuilder sb = new StringBuilder(",[4,2,'");
			sb.append(action.getId());
			sb.append("']");
			synchronized(mMessages)
			{
				mMessages.add(sb.toString());
			}
		}
		
		@Override
		public void onError(Action action, int errorCode)
		{
			StringBuilder sb = new StringBuilder(",[4,3,'");
			sb.append(action.getId());
			sb.append("',");
			sb.append(errorCode);
			sb.append("]");
			synchronized(mMessages)
			{
				mMessages.add(sb.toString());
			}
		}
		
		@Override
		public void onCompleted(ClipPlayer clipPlayer)
		{
			StringBuilder sb = new StringBuilder(",[5,1,");
			sb.append(clipPlayer.getId());
			sb.append("]");
			synchronized(mMessages)
			{
				mMessages.add(sb.toString());
			}
		}

		@Override
		public void onError(ClipPlayer clipPlayer, int errorCode)
		{
			StringBuilder sb = new StringBuilder(",[5,2,");
			sb.append(clipPlayer.getId());
			sb.append(",");
			sb.append(errorCode);
			sb.append("]");
			synchronized(mMessages)
			{
				mMessages.add(sb.toString());
			}
		}
	}
}