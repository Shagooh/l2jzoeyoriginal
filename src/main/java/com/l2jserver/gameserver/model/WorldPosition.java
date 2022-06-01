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

import com.l2jserver.gameserver.model.interfaces.IImmutablePosition;
import com.l2jserver.gameserver.model.interfaces.IPosition;

public abstract class WorldPosition implements IPosition {
	protected int _x, _y, _z;

	protected WorldPosition(int x, int y, int z) {
		if (x < L2World.MAP_MIN_X || x > L2World.MAP_MAX_X) {
			throw new IllegalArgumentException("x coordinate out of bounds");
		}
		if (y < L2World.MAP_MIN_Y || y > L2World.MAP_MAX_Y) {
			throw new IllegalArgumentException("y coordinate out of bounds");
		}
		if (z < Short.MIN_VALUE || z > Short.MAX_VALUE) {
			throw new IllegalArgumentException("z coordinate out of bounds");
		}

		_x = x;
		_y = y;
		_z = z;
	}

	protected WorldPosition(IImmutablePosition pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}
	
	protected WorldPosition(IPosition pos) {
		this(pos.getImmutablePosition());
	}

	@Override
	public int getX() {
		return _x;
	}
	
	@Override
	public int getY() {
		return _y;
	}
	
	@Override
	public int getZ() {
		return _z;
	}
	
}
