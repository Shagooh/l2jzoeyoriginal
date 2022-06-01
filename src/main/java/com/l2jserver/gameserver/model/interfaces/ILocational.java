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
 * Object world location storage interface.
 * @author xban1x
 */
public interface ILocational extends IPosition {
	/** @return the heading */
	int getHeading();
	
	/**
	 * Returns the instance zone id. When not participating in an instance, 0 is
	 * returned.
	 * @return the instance zone id
	 */
	int getInstanceId();
	
	/**
	 * @return a snapshot of the objects location as a {@link Locational}
	 */
	Location getLocation();
}