/*
 * Copyright © 2004-2022 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.interfaces.IImmutablePosition;
import com.l2jserver.gameserver.model.interfaces.ILocational;

/**
 * Location data transfer object.<br>
 * Contains coordinates data, heading and instance Id.
 * @author Zoey76
 */
public class Location implements ILocational, IImmutablePosition {
	private final int _x, _y, _z;
	private int _heading;
	private int _instanceId;

	public Location(int x, int y, int z, int heading, int instanceId) {
		_x = x;
		_y = y;
		_z = z;
		if (heading < 0) {
			_heading = Rnd.nextInt(61794);
		} else {
			_heading = heading;
		}
		_instanceId = instanceId;
	}	
	
	public Location(int x, int y, int z) {
		this(x, y, z, 0, -1);
	}
	
	public Location(int x, int y, int z, int heading) {
		this(x, y, z, heading, -1);
	}

	public Location(Location loc) {
		this(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), loc.getInstanceId());
	}
	
	public Location(Location loc, int heading) {
		this(loc.getX(), loc.getY(), loc.getZ(), heading, loc.getInstanceId());
	}

	public Location(Location loc, int heading, int instanceId) {
		this(loc.getX(), loc.getY(), loc.getZ(), heading, instanceId);
	}
	
	public Location(ILocational loc) {
		this(loc.getLocation());
	}
	
	public Location(ILocational loc, int heading) {
		this(loc.getLocation(), heading);
	}
	
	public Location(ILocational loc, int heading, int instanceId) {
		this(loc.getLocation(), heading, instanceId);
	}
	
	/**
	 * Get the x coordinate.
	 * @return the x coordinate
	 */
	@Override
	public int getX() {
		return _x;
	}
	
	/**
	 * Get the y coordinate.
	 * @return the y coordinate
	 */
	@Override
	public int getY() {
		return _y;
	}
	
	/**
	 * Get the z coordinate.
	 * @return the z coordinate
	 */
	@Override
	public int getZ() {
		return _z;
	}
	
	/**
	 * Get the heading.
	 * @return the heading
	 */
	@Override
	public int getHeading() {
		return _heading;
	}
	
	/**
	 * Get the instance Id.
	 * @return the instance Id
	 */
	@Override
	public int getInstanceId() {
		return _instanceId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			final Location loc = (Location) obj;
			return (getX() == loc.getX()) && (getY() == loc.getY()) && (getZ() == loc.getZ()) && (getHeading() == loc.getHeading()) && (getInstanceId() == loc.getInstanceId());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "] X: " + getX() + " Y: " + getY() + " Z: " + getZ() + " Heading: " + _heading + " InstanceId: " + _instanceId;
	}
	
	@Override
	public IImmutablePosition getImmutablePosition() {
		return this;
	}

	@Override
	public Location getLocation() {
		return this;
	}
}
