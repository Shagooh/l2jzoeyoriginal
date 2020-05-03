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

import static com.l2jserver.gameserver.config.Configuration.character;
import static com.l2jserver.gameserver.config.Configuration.general;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.l2jserver.gameserver.dao.FriendDAO;
import com.l2jserver.gameserver.dao.HennaDAO;
import com.l2jserver.gameserver.dao.ItemDAO;
import com.l2jserver.gameserver.dao.PlayerDAO;
import com.l2jserver.gameserver.dao.PremiumItemDAO;
import com.l2jserver.gameserver.dao.RecipeBookDAO;
import com.l2jserver.gameserver.dao.RecipeShopListDAO;
import com.l2jserver.gameserver.dao.SkillDAO;
import com.l2jserver.gameserver.dao.TeleportBookmarkDAO;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.service.PlayerService;

/**
 * Player Service implementation.
 * @author Zoey76
 * @version 2.6.2.0
 */
@Service
public class PlayerServiceImpl implements PlayerService {
	
	private static final Logger LOG = LoggerFactory.getLogger(PlayerServiceImpl.class);
	
	private static final String COND_OVERRIDE_KEY = "cond_override";
	
	@Autowired
	private PlayerDAO playerDAO;
	
	@Autowired
	private SkillDAO skillDAO;
	
	@Autowired
	private RecipeBookDAO recipeBookDAO;
	
	@Autowired
	private HennaDAO hennaDAO;
	
	@Autowired
	private RecipeShopListDAO recipeShopListDAO;
	
	@Autowired
	private TeleportBookmarkDAO teleportBookmarkDAO;
	
	@Autowired
	private FriendDAO friendDAO;
	
	@Autowired
	private PremiumItemDAO premiumItemDAO;
	
	@Autowired
	private ItemDAO itemDAO;
	
	@Override
	public L2PcInstance create(int classId, String accountName, String name, PcAppearance app) {
		// Create a new L2PcInstance with an account name
		L2PcInstance player = new L2PcInstance(classId, accountName, app);
		// Set the name of the L2PcInstance
		player.setName(name);
		// Set Character's create time
		player.setCreateDate(Calendar.getInstance());
		// Set the base class ID to that of the actual class ID.
		player.setBaseClass(player.getClassId());
		// Kept for backwards compatibility.
		player.setNewbie(1);
		// Give 20 recommendations
		player.setRecomLeft(20);
		// Add the player in the characters table of the database
		return playerDAO.insert(player) ? player : null;
	}
	
	@Override
	public L2PcInstance load(int objectId) {
		try {
			final L2PcInstance player = playerDAO.load(objectId);
			if (player == null) {
				return null;
			}
			
			playerDAO.loadCharacters(player);
			
			// Retrieve from the database all items of this L2PcInstance and add them to _inventory
			player.getInventory().restore();
			player.getFreight().restore();
			if (!general().warehouseCache()) {
				player.getWarehouse();
			}
			
			// Retrieve from the database all secondary data of this L2PcInstance
			// Note that Clan, Noblesse and Hero skills are given separately and not here.
			// Retrieve from the database all skills of this L2PcInstance and add them to _skills
			skillDAO.load(player);
			
			player.getMacros().restoreMe();
			
			player.getShortCuts().restoreMe();
			
			hennaDAO.load(player);
			
			teleportBookmarkDAO.load(player);
			
			recipeBookDAO.load(player, true);
			
			if (character().storeRecipeShopList()) {
				recipeShopListDAO.load(player);
			}
			
			premiumItemDAO.load(player);
			
			itemDAO.loadPetInventory(player);
			
			// Reward auto-get skills and all available skills if auto-learn skills is true.
			player.rewardSkills();
			
			friendDAO.load(player);
			
			// Buff and status icons
			if (character().storeSkillCooltime()) {
				player.restoreEffects();
			}
			
			// Restore current CP, HP and MP values
			if (player.getCurrentHp() < 0.5) {
				player.setIsDead(true);
				player.stopHpMpRegeneration();
			}
			
			// Restore pet if exists in the world
			player.setPet(L2World.getInstance().getPet(player.getObjectId()));
			if (player.hasSummon()) {
				player.getSummon().setOwner(player);
			}
			
			// Update the overloaded status of the L2PcInstance
			player.refreshOverloaded();
			// Update the expertise status of the L2PcInstance
			player.refreshExpertisePenalty();
			
			friendDAO.load(player);
			
			if (character().storeUISettings()) {
				player.restoreUISettings();
			}
			
			if (player.isGM()) {
				final long masks = player.getVariables().getLong(COND_OVERRIDE_KEY, PcCondOverride.getAllExceptionsMask());
				player.setOverrideCond(masks);
			}
			return player;
		} catch (Exception e) {
			LOG.error("Failed loading character.", e);
		}
		return null;
	}
}
