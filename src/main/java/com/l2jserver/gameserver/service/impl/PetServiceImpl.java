/*
 * Copyright © 2004-2020 L2J Server
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
package com.l2jserver.gameserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.l2jserver.gameserver.dao.PetDAO;
import com.l2jserver.gameserver.data.xml.impl.PetDataTable;
import com.l2jserver.gameserver.model.L2PetData;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.service.PetService;

/**
 * Pet Service implementation.
 * @author Zoey76
 * @version 2.6.2.0
 */
@Service
public class PetServiceImpl implements PetService {
	
	@Autowired
	private PetDAO petDAO;
	
	@Override
	public L2PetInstance spawn(L2NpcTemplate template, L2PcInstance owner, L2ItemInstance control) {
		if (L2World.getInstance().getPet(owner.getObjectId()) != null) {
			return null; // owner has a pet listed in world
		}
		final L2PetData data = PetDataTable.getInstance().getPetData(template.getId());
		
		final L2PetInstance pet = petDAO.load(control, template, owner);
		if (pet != null) {
			pet.setTitle(owner.getName());
			if (data.isSynchLevel() && (pet.getLevel() != owner.getLevel())) {
				pet.getStat().setLevel(owner.getLevel());
				pet.getStat().setExp(pet.getStat().getExpForLevel(owner.getLevel()));
			}
			L2World.getInstance().addPet(owner.getObjectId(), pet);
		}
		return pet;
	}
}
