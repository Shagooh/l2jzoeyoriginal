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
 * Interface to represent a mutable world position.
 * @author HorridoJoho
 */
public interface IMutablePosition extends IPosition {
	/**
	 * Sets the x, y and z coordinate.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	void setXYZ(int x, int y, int z);
	
	/**
	 * Sets the x, y and z coordinate.
	 * @param loc the location to take the xyz from
	 */
	default void setXYZ(Location loc) {
		setXYZ(loc.getX(), loc.getY(), loc.getZ());
	}

	/**
	 * Sets the x, y and z coordinate.
	 * @param loc the locational object to take the x, y and z coordinates from
	 */
	default void setXYZ(ILocational loc) {
		setXYZ(loc.getLocation());
	}
}
