/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver;

import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jserver.gameserver.model.actor.L2Character;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Removed TimerThread watcher [DrHouse]
 *
 * @version $Date: 2010/02/02 22:43:00 $
 */
public class GameTimeController
{
	protected static final Logger _log = Logger.getLogger(GameTimeController.class.getName());
	
	public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	
	protected static int _gameTicks;
	protected static long _gameStartTime;
	protected static boolean _isNight = false;
	protected static boolean _interruptRequest = false;
	
	private static final FastMap<Integer, L2Character> _movingObjects = new FastMap<Integer, L2Character>().setShared(true);
	private static final FastList<L2Character> _toDelete = new FastList<L2Character>(20);
	
	protected static TimerThread _timer;
	
	/**
	 * one ingame day is 240 real minutes
	 */
	public static GameTimeController getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private GameTimeController()
	{
		_gameStartTime = System.currentTimeMillis() - 3600000; // offset so that the server starts a day begin
		_gameTicks = 3600000 / MILLIS_IN_TICK; // offset so that the server starts a day begin
		
		_timer = new TimerThread();
		_timer.start();
		
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new BroadcastSunState(), 0, 600000);
		
	}
	
	public boolean isNowNight()
	{
		return _isNight;
	}
	
	public int getGameTime()
	{
		return (_gameTicks / (TICKS_PER_SECOND * 10));
	}
	
	public static int getGameTicks()
	{
		return _gameTicks;
	}
	
	/**
	 * Add a L2Character to movingObjects of GameTimeController.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<BR><BR>
	 *
	 * @param cha The L2Character to add to movingObjects of GameTimeController
	 *
	 */
	public void registerMovingObject(L2Character cha)
	{
		if (cha == null)
			return;
		
		_movingObjects.putIfAbsent(cha.getObjectId(), cha);
	}
	
	/**
	 * Move all L2Characters contained in movingObjects of GameTimeController.<BR><BR>
	 *
	 * <B><U> Concept</U> :</B><BR><BR>
	 * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Update the position of each L2Character </li>
	 * <li>If movement is finished, the L2Character is removed from movingObjects </li>
	 * <li>Create a task to update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED </li><BR><BR>
	 *
	 */
	protected void moveObjects()
	{
		// Go throw the table containing L2Character in movement
		for (L2Character ch : _movingObjects.values())
		{
			// If movement is finished, the L2Character is removed from
			// movingObjects and added to the ArrayList ended
			if (ch.updatePosition(_gameTicks))
				_toDelete.add(ch);				
		}
		
		if (_toDelete.isEmpty())
			return;
		
		/*
		 * Synchronizing on _movingObjects since this map is set as shared so,
		 * on every write operation (add/remove) there is already an internal 
		 * synchronization. This way we avoid the time that every remove()
		 * call would have to wait until acquiring _movingObjects lock due to
		 * concurrent writes.
		 * 
		 * TODO: Possibly this could be done on the fly, removing while iterating over
		 * the map, without using any extra list.
		 * 
		 * TODO: On the other hand, perhaps we could avoid executing some MovingObjectArrived
		 * tasks performing some checks in this thread (during loop): for instance,
		 * only execute it if AI think() will be eventually run.
		 *   
		 * [DrHouse]
		 */
		synchronized (_movingObjects)
		{
			for (L2Character ch : _toDelete)
			{
				_movingObjects.remove(ch.getObjectId());
				ThreadPoolManager.getInstance().executeTask(new MovingObjectArrived(ch));			
			}
		}
		_toDelete.clear();
	}
	
	public void stopTimer()
	{
		_interruptRequest = true;
		_timer.interrupt();
	}
	
	class TimerThread extends Thread
	{
		public TimerThread()
		{
			super("GameTimeController");
			setDaemon(true);
			setPriority(MAX_PRIORITY);
		}
		
		@Override
		public void run()
		{
			int oldTicks;
			long runtime;
			int sleepTime;
			
			for(;;)
			{
				try
				{
					oldTicks = _gameTicks; // save old ticks value to avoid moving objects 2x in same tick
					runtime = System.currentTimeMillis() - _gameStartTime; // from server boot to now
					
					_gameTicks = (int) (runtime / MILLIS_IN_TICK); // new ticks value (ticks now)
					
					if (oldTicks != _gameTicks)
						moveObjects(); // Runs possibly too often
						
					runtime = (System.currentTimeMillis() - _gameStartTime) - runtime;
					
					// calculate sleep time... time needed to next tick minus time it takes to call moveObjects()
					sleepTime = 1 + MILLIS_IN_TICK - ((int) runtime) % MILLIS_IN_TICK;
					
					//_log.finest("TICK: "+_gameTicks);
					
					if (sleepTime > 0)
						Thread.sleep(sleepTime);
				}
				catch (InterruptedException ie)
				{
					if (_interruptRequest)
						return;
					
					ie.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED.<BR><BR>
	 */
	class MovingObjectArrived implements Runnable
	{
		private final L2Character _ended;
		
		MovingObjectArrived(L2Character ended)
		{
			_ended = ended;
		}
		
		public void run()
		{
			try
			{
				if (_ended.hasAI()) // AI could be just disabled due to region turn off
				{
					if (Config.MOVE_BASED_KNOWNLIST)
						_ended.getKnownList().findObjects();
					_ended.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED);
				}
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param rise
	 */
	class BroadcastSunState implements Runnable
	{
		int h;
		boolean tempIsNight;
		
		public void run()
		{
			h = (getGameTime() / 60) % 24; // Time in hour
			tempIsNight = (h < 6);
			
			if (tempIsNight != _isNight)
			{ // If diff day/night state
				_isNight = tempIsNight; // Set current day/night varible to value of temp varible
				DayNightSpawnManager.getInstance().notifyChangeMode();
			}
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final GameTimeController _instance = new GameTimeController();
	}
}
