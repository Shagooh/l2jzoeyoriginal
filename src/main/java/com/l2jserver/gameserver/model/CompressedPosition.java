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
import com.l2jserver.gameserver.model.interfaces.ILocational;
import com.l2jserver.gameserver.model.interfaces.IMutablePosition;
import com.l2jserver.gameserver.model.interfaces.IPosition;

/** Baseclass for CompressedPositions */
public abstract class CompressedPosition implements IPosition {
	/** Immutable version of CompressedPosition */
	public static class Immutable extends CompressedPosition implements IImmutablePosition {
		public Immutable(int x, int y, int z) {
			super(x, y, z);
		}
	}

	/** Mutable version of CompressedPosition */
	public static class Mutable extends CompressedPosition implements IMutablePosition {
		public Mutable(int x, int y, int z) {
			super(x, y, z);
		}

/*		@Override
		public void setX(int x) {
			setXYZ(x, getY(), getZ());
		}

		@Override
		public void setY(int y) {
			setXYZ(getX(), y, getZ());
		}

		@Override
		public void setZ(int z) {
			setXYZ(getX(), getY(), z);
		}*/

		@Override
		public void setXYZ(int x, int y, int z) {
			_compressed = compressPosition(x, y, z);
		}

		@Override
		public void setXYZ(ILocational loc) {
			IPosition pos = loc.getImmutablePosition();
			setXYZ(pos.getX(), pos.getY(), pos.getZ());
		}

		@Override
		public IImmutablePosition getImmutablePosition() {
			long compressed = _compressed;
			return new Immutable(extractX(compressed), extractY(compressed), extractZ(compressed));
		}
	}

	private static final int X_BITS = Long.SIZE - Long.numberOfLeadingZeros(L2World.MAP_MAX_X - L2World.MAP_MIN_X);
	private static final int Y_BITS = Long.SIZE - Long.numberOfLeadingZeros(L2World.MAP_MAX_Y - L2World.MAP_MIN_Y);
	private static final int Z_BITS = Long.SIZE - Long.numberOfLeadingZeros(Short.MAX_VALUE - Short.MIN_VALUE);
	
	private static final long X_MASK = 0xFFFFFFFFFFFFFFFFL >>> (Long.SIZE - X_BITS);
	private static final long Y_MASK = 0xFFFFFFFFFFFFFFFFL >>> (Long.SIZE - Y_BITS);
	private static final long Z_MASK = 0xFFFFFFFFFFFFFFFFL >>> (Long.SIZE - Z_BITS);
	
	private static final long X_SHIFT = Z_BITS + Y_BITS;
	private static final long Y_SHIFT = Z_BITS;
	private static final long Z_SHIFT = 0;

	private static long compressPosition(int x, int y, int z) {
		return (x - L2World.MAP_MIN_X << X_SHIFT) |
			(y - L2World.MAP_MIN_Y << Y_SHIFT) |
			(z - Short.MIN_VALUE << Z_SHIFT);
	}
	
	private static int extractX(long compressed) {
		return (int) ((compressed >>> X_SHIFT) & X_MASK) + L2World.MAP_MIN_X;
	}

	private static int extractY(long compressed) {
		return (int) ((compressed >>> Y_SHIFT) & Y_MASK) + L2World.MAP_MIN_Y;
	}

	private static int extractZ(long compressed) {
		return (int) ((compressed >>> Z_SHIFT) & Z_MASK) + Short.MIN_VALUE;
	}
	
	protected long _compressed;
	
	public CompressedPosition(int x, int y, int z) {
		_compressed = compressPosition(x, y, z);
	}
	
	public int getX() {
		return extractX(_compressed);
	}
	
	public int getY() {
		return extractY(_compressed);
	}
	
	public int getZ() {
		return extractZ(_compressed);
	}
}
