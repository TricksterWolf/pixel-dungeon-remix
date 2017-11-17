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
package com.annatala.pixelponies.plants;

import com.annatala.pixelponies.android.util.TrackedRuntimeException;
import com.annatala.pixelponies.android.R;
import com.annatala.noosa.Game;
import com.annatala.noosa.audio.Sample;
import com.annatala.pixelponies.Assets;
import com.annatala.pixelponies.Dungeon;
import com.annatala.pixelponies.actors.Char;
import com.annatala.pixelponies.actors.hero.Hero;
import com.annatala.pixelponies.actors.hero.HeroSubClass;
import com.annatala.pixelponies.effects.CellEmitter;
import com.annatala.pixelponies.effects.SpellSprite;
import com.annatala.pixelponies.effects.particles.LeafParticle;
import com.annatala.pixelponies.items.Generator;
import com.annatala.pixelponies.items.Item;
import com.annatala.pixelponies.items.food.Food;
import com.annatala.pixelponies.levels.Terrain;
import com.annatala.pixelponies.sprites.PlantSprite;
import com.annatala.utils.Utils;
import com.annatala.utils.Bundlable;
import com.annatala.utils.Bundle;
import com.annatala.utils.Random;

import java.util.ArrayList;

public class Plant implements Bundlable {

	public String plantName;

	public int image;
	public int pos;

	public PlantSprite sprite;

	public void activate(Char ch) {

		// TODO: Cannibalize for zebras, maybe.
//		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
//			Buff.affect(ch, Barkskin.class).level(ch.ht() / 3);
//		}
		
		effect(pos, ch);
		
		wither();
	}

	public void wither() {
		Dungeon.level.uproot(pos);

		sprite.kill();
		if (Dungeon.visible[pos]) {
			CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6);
		}

		if (Dungeon.hero.subClass == HeroSubClass.FARMER) {
			if (Random.Int(5) == 0) {
				Dungeon.level.drop(Generator.random(Generator.Category.SEED),
						pos).sprite.drop();
			}
			if (Random.Int(5) == 0) {
				Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
			}
		}
	}

	private static final String POS = "pos";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		pos = bundle.getInt(POS);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(POS, pos);
	}

	public boolean dontPack() {
		return false;
	}
	
	public String desc() {
		return null;
	}
	
	public void effect(int pos, Char ch) {
		
	}

	public static class Seed extends Item {

		public static final String AC_PLANT = Game
				.getVar(R.string.Plant_ACPlant);
		private static final String TXT_INFO = Game.getVar(R.string.Plant_Info);
		protected static final String TXT_SEED = Game
				.getVar(R.string.Plant_Seed);

		private static final float TIME_TO_PLANT = 1f;

		{
			stackable = true;
			defaultAction = AC_THROW;
		}

		protected Class<? extends Plant> plantClass;
		protected String plantName;

		public Class<? extends Item> alchemyClass;

		@Override
		public ArrayList<String> actions(Hero hero) {
			ArrayList<String> actions = super.actions(hero);
			actions.add(AC_PLANT);
			actions.add(Food.AC_EAT);
			return actions;
		}

		@Override
		protected void onThrow(int cell) {
			if (Dungeon.level.map[cell] == Terrain.ALCHEMY || Dungeon.level.pit[cell]) {
				super.onThrow(cell);
			} else {
				Dungeon.level.plant(this, cell);
			}
		}

		@Override
		public void execute(Hero hero, String action) {
			if (action.equals(AC_PLANT)) {

				hero.spend(TIME_TO_PLANT);
				hero.busy();
				((Seed) detach(hero.belongings.backpack)).onThrow(hero.getPos());

				hero.getSprite().operate(hero.getPos());

			} else if (action.equals(Food.AC_EAT)) {
				detach(hero.belongings.backpack);

				hero.getSprite().operate(hero.getPos());
				hero.busy();

				SpellSprite.show(hero, SpellSprite.FOOD);
				Sample.INSTANCE.play(Assets.SND_EAT);

				hero.spend(Food.TIME_TO_EAT);
			}

			super.execute(hero, action);
		}
		
		@Override
		public Item burn(int cell){
			return null;
		}

		public Plant couch(int pos) {
			try {
				Sample.INSTANCE.play(Assets.SND_PLANT);
				Plant plant = plantClass.newInstance();
				plant.pos = pos;
				return plant;
			} catch (Exception e) {
				throw new TrackedRuntimeException(e);
			}
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}

		@Override
		public int price() {
			return 10 * quantity();
		}

		@Override
		public String info() {
			return Utils.format(TXT_INFO, Utils.indefinite(plantName), desc());
		}
	}
}