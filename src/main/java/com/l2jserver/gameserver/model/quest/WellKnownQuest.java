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
package com.l2jserver.gameserver.model.quest;

import java.util.Objects;

import com.l2jserver.gameserver.model.interfaces.IQuestTemplate;

public enum WellKnownQuest implements IQuestTemplate {
	REPENT_YOUR_SINS(442, "Q00422_RepentYourSins");

	private final int _id;
	private final String _name;

	private WellKnownQuest(int id, String name) {
		Objects.requireNonNull(name);
		if (id <= 0) {
			throw new IllegalArgumentException("quest id must be above 0");
		}
		if (name.isBlank()) {
			throw new IllegalArgumentException("quest name must not be blank");
		}

		_id = id;
		_name = name;
	}
	
	public int getQuestId() {
		return _id;
	}
	
	public String getQuestName() {
		return _name;
	}
}
