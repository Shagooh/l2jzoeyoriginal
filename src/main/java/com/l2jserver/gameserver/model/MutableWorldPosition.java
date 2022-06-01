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
import com.l2jserver.gameserver.model.interfaces.IMutablePosition;
import com.l2jserver.gameserver.model.interfaces.IPosition;

public class MutableWorldPosition extends WorldPosition implements IMutablePosition {
	protected MutableWorldPosition(int x, int y, int z) {
		super(x, y, z);
	}
	
	protected MutableWorldPosition(IImmutablePosition pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}
	
	protected MutableWorldPosition(IPosition pos) {
		this(pos.getImmutablePosition());
	}

	@Override
	public IImmutablePosition getImmutablePosition() {
		return new ImmutableWorldPosition(this);
	}
	
	@Override
	public void setXYZ(int x, int y, int z) {
		_x = x;
		_y = y;
		_z = z;
	}
}
