/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.annatala.pixelponies.actors.mobs;

import com.annatala.pixelponies.android.util.JsonHelper;
import com.annatala.pixelponies.android.util.TrackedRuntimeException;
import com.annatala.pixelponies.actors.mobs.common.MobFactory;
import com.annatala.pixelponies.actors.mobs.spiders.SpiderMind;
import com.annatala.pixelponies.actors.mobs.spiders.SpiderMindAmber;
import com.annatala.pixelponies.actors.mobs.spiders.SpiderServant;
import com.annatala.pixelponies.actors.mobs.spiders.SpiderGuard;
import com.annatala.noosa.Game;
import com.annatala.utils.Random;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Bestiary {

	static JSONObject mobsData;

	private static Class<? extends Mob> getMobFromExternalBestiary(int depth, String levelKind) {
		if (mobsData == null) {
			mobsData = JsonHelper.readFile("levelsDesc/Bestiary.json");
			if (mobsData == null) {
				throw new TrackedRuntimeException("malformed levelsDesc/Bestiary.json");
			}
		}

		try {
			JSONObject levelDesc = mobsData.getJSONObject(levelKind);
			
			String depthString = Integer.toString(depth);
			
			if(!levelDesc.has(depthString)) {
				depthString = "any";
			}
			
			JSONObject depthDesc = levelDesc.getJSONObject(depthString);

			ArrayList<Float> chances = new ArrayList<>();
			ArrayList<String> names = new ArrayList<>();

			Iterator<?> keys = depthDesc.keys();

			while (keys.hasNext()) {
				String mobClassName = (String) keys.next();
				names.add(mobClassName);
				float chance = (float) depthDesc.getDouble(mobClassName);
				chances.add(chance);
			}
			String selectedMobClass = (String) names.toArray()[Random.chances(chances.toArray(new Float[chances.size()]))];
			return MobFactory.mobClassByName(selectedMobClass);
			
		} catch (JSONException e) {
			Game.toast(e.getMessage());
		}
		return MobFactory.mobClassRandom();
	}

	public static Mob mob(int depth, String levelKind) {
		Class<? extends Mob> cl = getMobFromExternalBestiary(depth, levelKind);
		try {
			return cl.newInstance();
		} catch (Exception e) {
			throw new TrackedRuntimeException(e);
		}
	}

	public static Mob mutable(int depth, String levelKind) {
		Class<? extends Mob> cl = getMobFromExternalBestiary(depth, levelKind);

		// These are cool enough that luck shouldn't influence them showing up.
		if (Random.Int(30) == 0) {
			if (cl == Rat.class) {
				cl = Albino.class;
			} else if (cl == Thief.class) {
				cl = Bandit.class;
			} else if (cl == Brute.class) {
				cl = Shielded.class;
			} else if (cl == Monk.class) {
				cl = Senior.class;
			} else if (cl == Scorpio.class) {
				cl = Acidic.class;
			} else if (cl == SpiderServant.class) {
				cl = SpiderGuard.class;
			} else if (cl == SpiderMind.class) {
				cl = SpiderMindAmber.class;
			} else if (cl == DeathKnight.class) {
				cl = DreadKnight.class;
			}
		}
		
		try {
			return cl.newInstance();
		} catch (Exception e) {
			throw new TrackedRuntimeException(e);
		}
	}
}
