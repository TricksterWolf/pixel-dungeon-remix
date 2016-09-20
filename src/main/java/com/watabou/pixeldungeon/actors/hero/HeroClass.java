/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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

package com.watabou.pixeldungeon.actors.hero;

import android.support.annotation.NonNull;

import com.nyrds.pixeldungeon.items.guts.weapon.melee.Claymore;
import com.nyrds.pixeldungeon.items.necropolis.BlackSkull;
import com.nyrds.pixeldungeon.items.necropolis.BladeOfSouls;
import com.nyrds.pixeldungeon.ml.BuildConfig;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.items.TomeOfMastery;
import com.watabou.pixeldungeon.items.armor.ClassArmor;
import com.watabou.pixeldungeon.items.armor.ClothArmor;
import com.watabou.pixeldungeon.items.food.Ration;
import com.watabou.pixeldungeon.items.potions.PotionOfLiquidFlame;
import com.watabou.pixeldungeon.items.potions.PotionOfHonesty;
import com.watabou.pixeldungeon.items.potions.PotionOfToxicGas;
import com.watabou.pixeldungeon.items.rings.RingOfShadows;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.watabou.pixeldungeon.items.wands.WandOfMagicMissile;
import com.watabou.pixeldungeon.items.weapon.melee.Dagger;
import com.watabou.pixeldungeon.items.weapon.melee.Knuckles;
import com.watabou.pixeldungeon.items.weapon.melee.ShortSword;
import com.watabou.pixeldungeon.items.weapon.melee.WoodenBow;
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang;
import com.watabou.pixeldungeon.items.weapon.missiles.CommonArrow;
import com.watabou.pixeldungeon.items.weapon.missiles.Dart;
import com.watabou.pixeldungeon.ui.QuickSlot;
import com.watabou.pixeldungeon.actors.Gender;
import com.watabou.utils.Bundle;

public enum HeroClass {

	EARTH_PONY(
			Game.getVar(R.string.HeroClass_EarthPony),
			Game.getVars(R.array.HeroClass_EarthPonyPerks),
			Gender.MASCULINE,
			false,
			new HeroSubClass[]
					{ HeroSubClass.FARMER, HeroSubClass.BARD, HeroSubClass.NONE }
	),
	MAGE(
			Game.getVar(R.string.HeroClass_Mag),
			Game.getVars(R.array.HeroClass_MagPerks),
			Gender.MASCULINE,
			false,
			new HeroSubClass[]
					{ HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK, HeroSubClass.NONE }
	),
	ROGUE(
			Game.getVar(R.string.HeroClass_Rog),
			Game.getVars(R.array.HeroClass_RogPerks),
			Gender.MASCULINE,
			false,
			new HeroSubClass[]
					{ HeroSubClass.FREERUNNER, HeroSubClass.ASSASSIN, HeroSubClass.NONE }
	),
	HUNTRESS(
			Game.getVar(R.string.HeroClass_Hun),
			Game.getVars(R.array.HeroClass_HunPerks),
			Gender.FEMININE,
			false,
			new HeroSubClass[]
					{ HeroSubClass.SNIPER, HeroSubClass.WARDEN, HeroSubClass.NONE }
	),
	ELF(
			Game.getVar(R.string.HeroClass_Elf),
			Game.getVars(R.array.HeroClass_ElfPerks),
			Gender.MASCULINE,
			false,
			new HeroSubClass[]
					{ HeroSubClass.SCOUT, HeroSubClass.SHAMAN, HeroSubClass.NONE }
	);

	private final String title;
	private final String[] perks;
	private final Gender gender;
	private final boolean hasWings;
	private HeroSubClass[] subClasses;

	HeroClass(String title, String[] perks, Gender gender, boolean hasWings, HeroSubClass[] subClasses) {
		this.title = title;
		this.perks = perks;
		this.gender = gender;
		this.hasWings = hasWings;
		this.subClasses = subClasses;
	}

	public void initHero(Hero hero) {
		hero.heroClass = this;
		hero.setGender(this.gender);
		initCommon(hero);

		switch (this) {

			case EARTH_PONY:
				initEarth(hero);
				break;

			case MAGE:
				initMage(hero);
				break;

			case ROGUE:
				initRogue(hero);
				break;

			case HUNTRESS:
				initHuntress(hero);
				break;

			case ELF:
				initElf(hero);
				break;


		}

		if (Badges.isUnlocked(masteryBadge()) && hero.getDifficulty() < 3) {
			new TomeOfMastery().collect(hero);
		}

		hero.updateAwareness();
	}

	private static void initDebug(Hero hero) {
		for(int i = 0;i<100;i++) {
			hero.collect(new ScrollOfMagicMapping());
			hero.collect(new PotionOfToxicGas());
			hero.collect(new PotionOfLiquidFlame());
			hero.collect(new ScrollOfTeleportation());
		}

		Badges.validateBossSlain(Badges.Badge.BOSS_SLAIN_3);

		hero.collect(new TomeOfMastery());
		hero.collect(new Claymore().identify().upgrade(100));

		hero.collect(new BlackSkull());
		hero.collect(new BladeOfSouls().identify());

		hero.ht(1000);
		hero.hp(1000);
		hero.attackSkill = 1000;
		//hero.defenseSkill = 1000;

	}

	private static void initCommon(Hero hero) {
		(hero.belongings.armor = new ClothArmor()).identify();
		hero.collect(new Ration());
		if(BuildConfig.DEBUG) initDebug(hero);
		QuickSlot.cleanStorage();
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
		case EARTH_PONY:
			return Badges.Badge.MASTERY_EARTH;
		case MAGE:
			return Badges.Badge.MASTERY_MAGE;
		case ROGUE:
			return Badges.Badge.MASTERY_ROGUE;
		case HUNTRESS:
			return Badges.Badge.MASTERY_HUNTRESS;
		case ELF:
			return Badges.Badge.MASTERY_ELF;
		}
		return null;
	}

	private void initEarth(Hero hero) {
		hero.setHonesty(hero.honesty() + 1);
		hero.setLaughter(hero.laughter() + 1);
		hero.setMagic(hero.magic() - 1);
		hero.ht(hero.ht() + 5);
		hero.hp(hero.ht());

		(hero.belongings.weapon = new ShortSword()).identify();

		hero.collect(new Dart(8));

		QuickSlot.selectItem(Dart.class, 0);

		new PotionOfHonesty().setKnown();
	}

	private static void initMage(Hero hero) {
		(hero.belongings.weapon = new Knuckles()).identify();

		WandOfMagicMissile wand = new WandOfMagicMissile();
		hero.collect(wand.identify());

		QuickSlot.selectItem(wand, 0);

		new ScrollOfIdentify().setKnown();
	}

	private static void initRogue(Hero hero) {
		(hero.belongings.weapon = new Dagger()).identify();
		(hero.belongings.ring1 = new RingOfShadows()).upgrade().identify();

		hero.collect(new Dart(8).identify());

		hero.belongings.ring1.activate(hero);

		QuickSlot.selectItem(Dart.class, 0);

		new ScrollOfMagicMapping().setKnown();
	}

	private static void initHuntress(Hero hero) {
		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		(hero.belongings.weapon = new Dagger()).identify();
		Boomerang boomerang = new Boomerang();
		hero.collect(boomerang.identify());

		QuickSlot.selectItem(boomerang, 0);
	}

	private void initElf(Hero hero) {
		hero.setHonesty(hero.honesty() - 1);

		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		(hero.belongings.armor = new ClothArmor()).upgrade().identify();
		(hero.belongings.weapon = new WoodenBow()).upgrade().identify();

		hero.collect(new Dagger().upgrade().identify());
		hero.collect(new CommonArrow(20));

		QuickSlot.selectItem(CommonArrow.class, 0);
	}



	private void initUnicorn(Hero hero) {
		hero.setGenerosity(hero.generosity() + 1);
		hero.setMagic(hero.magic() + 1);
		hero.setLaughter(hero.laughter() - 1);

		(hero.belongings.weapon = new Knuckles()).identify();

		WandOfMagicMissile wand = new WandOfMagicMissile();
		hero.collect(wand.identify());

		QuickSlot.selectItem(wand, 0);

		new ScrollOfIdentify().setKnown();
	}

	@Override
	public String toString() {
		return title;
	}

	@NonNull
	public String[] perks() {
		return perks;
	}

	public Gender gender() {
		return gender;
	}

	public boolean hasWings() { return hasWings; }

	public HeroSubClass firstWay() { return this.subClasses[0]; }
	public HeroSubClass secondWay() { return this.subClasses[1]; }
	public HeroSubClass secretWay() { return this.subClasses[2]; }

	private static final String CLASS = "class";

	public void storeInBundle(Bundle bundle) {
		bundle.put(CLASS, title);
	}

	public static HeroClass restoreFromBundle(Bundle bundle) {
		String value = bundle.getString(CLASS);
		for (HeroClass heroClass : HeroClass.values()) {
			if (heroClass.title.equals(value)) {
				return heroClass;
			}
		}
		return null;
	}

	public ClassArmor classArmor() {
		return null;
	}


}