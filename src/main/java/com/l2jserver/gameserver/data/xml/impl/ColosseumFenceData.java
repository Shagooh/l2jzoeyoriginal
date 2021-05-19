/*
 * Copyright (C) 2004-2021 L2J Server
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
package com.l2jserver.gameserver.data.xml.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.actor.instance.L2ColosseumFence;
import com.l2jserver.gameserver.model.actor.instance.L2ColosseumFence.FenceState;
import com.l2jserver.gameserver.util.IXmlReader;

/**
 * @author HorridoJoho
 */
public final class ColosseumFenceData implements IXmlReader {
	private static final Logger LOG = LoggerFactory.getLogger(ColosseumFenceData.class);

	private final Map<Integer, List<L2ColosseumFence>> _static = new HashMap<>();
	private final Map<Integer, List<L2ColosseumFence>> _dynamic = new ConcurrentHashMap<>();
	
	protected ColosseumFenceData() {
		load();
	}
	
	@Override
	public void load() {
		_static.clear();
		parseDatapackFile("data/colosseum_fences.xml");
		LOG.info("Loaded {} static fences.", _static.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList fences = root.getElementsByTagName("colosseumfence");
		for (int i = 0; i < fences.getLength(); ++i) {
			Element fence = (Element) fences.item(i);
			int x = Integer.parseInt(fence.getAttribute("x"));
			int y = Integer.parseInt(fence.getAttribute("y"));
			int z = Integer.parseInt(fence.getAttribute("z"));
			int minZ = Integer.parseInt(fence.getAttribute("min_z"));
			int maxZ = Integer.parseInt(fence.getAttribute("max_z"));
			int width = Integer.parseInt(fence.getAttribute("width"));
			int height = Integer.parseInt(fence.getAttribute("height"));
			L2ColosseumFence fenceInstance = new L2ColosseumFence(0, x, y, z, minZ, maxZ, width, height, FenceState.CLOSED);
			Integer region = MapRegionManager.getInstance().getMapRegionLocId(fenceInstance);
			if (!_static.containsKey(region)) {
				_static.put(region, new ArrayList<L2ColosseumFence>());
			}
			_static.get(region).add(fenceInstance);
			fenceInstance.spawnMe();
		}
	}
	
	public L2ColosseumFence addDynamic(int x, int y, int z, int minZ, int maxZ, int width, int height) {
		L2ColosseumFence fence = new L2ColosseumFence(0, x, y, z, minZ, maxZ, width, height, FenceState.CLOSED);
		Integer region = MapRegionManager.getInstance().getMapRegionLocId(fence);
		if (!_dynamic.containsKey(region)) {
			_dynamic.put(region, new ArrayList<L2ColosseumFence>());
		}
		_dynamic.get(region).add(fence);
		fence.spawnMe();
		return fence;
	}
	
	public boolean checkIfFencesBetween(int x, int y, int z, int tx, int ty, int tz, int instanceId) {
		Collection<L2ColosseumFence> allFences;
		if ((instanceId > 0) && (InstanceManager.getInstance().getInstance(instanceId) != null)) {
			allFences = InstanceManager.getInstance().getInstance(instanceId).getFences();
		} else {
			allFences = new ArrayList<>(_static.size() + _dynamic.size());
			allFences.addAll(_static.get(MapRegionManager.getInstance().getMapRegionLocId(x, y)));
			allFences.addAll(_dynamic.get(MapRegionManager.getInstance().getMapRegionLocId(x, y)));
		}
		
		for (L2ColosseumFence fence : allFences) {
			if ((fence.getFenceState() == FenceState.CLOSED) && (fence.isInsideFence(x, y, z) != fence.isInsideFence(tx, ty, tz))) {
				return true;
			}
		}
		
		return false;
	}
	
	private static final class SingletonHolder {
		protected static final ColosseumFenceData INSTANCE = new ColosseumFenceData();
	}
	
	public static ColosseumFenceData getInstance() {
		return SingletonHolder.INSTANCE;
	}
}
