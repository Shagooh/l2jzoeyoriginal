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
package com.l2jserver.gameserver.model.interfaces;

import com.l2jserver.gameserver.model.Location;

/**
 * Object world location storage and update interface.
 * @author Zoey76
 */
public interface IPositionable extends ILocational, IMutablePosition {
	/**
	 * Sets the heading of this object.
	 * @param heading the new heading
	 */
	void setHeading(int heading);
	
	/**
	 * Changes the location of this object.
	 * @param x new x coordinate
	 * @param y new y coordinate
	 * @param z new z coordinate
	 */
	default void setLocation(int x, int y, int z) {
		setXYZ(x, y, z);
	}
	
	/**
	 * Changes the location of this object.
	 * @param 
	 */
	void setLocation(int x, int y, int z, int heading, int instanceId);

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	default void setLocation(int x, int y, int z, int heading) {
		Location loc = getLocation();
		setLocation(x, y, z, heading, loc.getInstanceId());
	}
	
	/**
	 * Changes the location of this object.
	 * @param loc location from which to update the location
	 */
	void setLocation(Location loc);
	
	/**
	 * Changes the location of this object.
	 * @param loc location from which the x, y, z and instanceId are taken
	 * @param heading the new heading
	 */
	default void setLocation(Location loc, int heading) {
		setLocation(loc.getX(), loc.getY(), loc.getZ(), heading, loc.getInstanceId());
	}

	/**
	 * Changes the location of this object.
	 * @param loc location from which the x, y and z coordinates are taken
	 * @param heading the new heading
	 * @param instanceId the new instanceId
	 */
	default void setLocation(Location loc, int heading, int instanceId) {
		setLocation(loc.getX(), loc.getY(), loc.getZ(), heading, instanceId);
	}
	
	/**
	 * Changes the location of this object.
	 * @param loc locational from which to update the location
	 */
	default void setLocation(ILocational loc) {
		setLocation(loc.getLocation());
	}
	
	/**
	 * Changes the location of this object.
	 * @param loc locational from which the x, y, z and instanceId are taken
	 * @param heading the new heading
	 */
	default void setLocation(ILocational loc, int heading) {
		setLocation(loc.getLocation(), heading);
	}
	
	/**
	 * Changes the location of this object.
	 * @param loc locational from which the x, y and z coordinates are taken
	 * @param heading the new heading
	 * @param instanceId the new instanceId
	 */
	default void setLocation(ILocational loc, int heading, int instanceId) {
		setLocation(loc.getLocation(), heading, instanceId);
	}
}