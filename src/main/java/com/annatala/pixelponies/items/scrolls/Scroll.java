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
package com.annatala.pixelponies.items.scrolls;

import com.annatala.pixelponies.android.util.TrackedRuntimeException;
import com.annatala.pixelponies.android.R;
import com.annatala.noosa.Game;
import com.annatala.pixelponies.Badges;
import com.annatala.pixelponies.actors.buffs.Blindness;
import com.annatala.pixelponies.actors.hero.Hero;
import com.annatala.pixelponies.items.Item;
import com.annatala.pixelponies.items.ItemStatusHandler;
import com.annatala.pixelponies.sprites.ItemSpriteSheet;
import com.annatala.utils.GLog;
import com.annatala.utils.Utils;
import com.annatala.utils.Bundle;
import com.annatala.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Scroll extends Item {

	private static final String TXT_BLINDED	= Game.getVar(R.string.Scroll_Blinded);

	public static final String AC_READ    	= Game.getVar(R.string.Scroll_ACRead);
	
	protected static final float TIME_TO_READ	= 1f;
	
	private static final Class<?>[] scrolls = {
		ScrollOfIdentify.class, 
		ScrollOfMagicMapping.class, 
		ScrollOfRecharging.class, 
		ScrollOfRemoveCurse.class, 
		ScrollOfTeleportation.class, 
		ScrollOfUpgrade.class, 
		ScrollOfChallenge.class,
		ScrollOfTerror.class,
		ScrollOfLullaby.class,
		ScrollOfWeaponUpgrade.class,
		ScrollOfPsionicBlast.class,
		ScrollOfMirrorImage.class,
		ScrollOfDomination.class,
		ScrollOfCurse.class,
		ScrollOfLoyalOath.class
	};

	private static final Class<?>[] inscribableScrolls = {
		ScrollOfIdentify.class, 
		ScrollOfMagicMapping.class, 
		ScrollOfRecharging.class, 
		ScrollOfRemoveCurse.class, 
		ScrollOfTeleportation.class, 
		ScrollOfUpgrade.class, 
		ScrollOfChallenge.class,
		ScrollOfTerror.class,
		ScrollOfLullaby.class,
		ScrollOfPsionicBlast.class,
		ScrollOfMirrorImage.class,
		ScrollOfDomination.class,
		ScrollOfCurse.class,
		ScrollOfLoyalOath.class
	};
	
	private static String[] runes = null;
	
	private static final Integer[] images = {
		ItemSpriteSheet.SCROLL_PRIRPA,
		ItemSpriteSheet.SCROLL_SANKEN,
		ItemSpriteSheet.SCROLL_KURPA,
		ItemSpriteSheet.SCROLL_ERSKI,
		ItemSpriteSheet.SCROLL_SULAT,
		ItemSpriteSheet.SCROLL_FARGEN,
		ItemSpriteSheet.SCROLL_PLUMATU,
		ItemSpriteSheet.SCROLL_ADOSHEN,
		ItemSpriteSheet.SCROLL_BLUSKEN,
		ItemSpriteSheet.SCROLL_SERENDA,
		ItemSpriteSheet.SCROLL_KONDA,
		ItemSpriteSheet.SCROLL_VAIOR,
		ItemSpriteSheet.SCROLL_TANAGRETU,
		ItemSpriteSheet.SCROLL_YAVAYA,
		ItemSpriteSheet.SCROLL_IBEKU,
		ItemSpriteSheet.SCROLL_MALENKI,
		ItemSpriteSheet.SCROLL_METAR
	};
	
	private static ItemStatusHandler<Scroll> handler;
	
	private static String[] getRunes(){
		if(runes == null){
			runes = Game.getVars(R.array.Scroll_Runes);
		}
		return runes;
	}
	
	private String rune;
	
	@SuppressWarnings("unchecked")
	public static void initLabels() {
		handler = new ItemStatusHandler<>((Class<? extends Scroll>[]) scrolls, getRunes(), images);
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}
	
	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>((Class<? extends Scroll>[]) scrolls, getRunes(), images, bundle);
	}
	
	public Scroll() {
		stackable     = true;
		defaultAction = AC_READ;
		
		if (this instanceof BlankScroll){
			return;
		}
		
		image = handler.image( this );
		rune  = handler.label( this );
	}
	
	static public Scroll createRandomScroll(){
		try {
			return (Scroll) Random.element(inscribableScrolls).newInstance();
		} catch (Exception e) {
			throw new TrackedRuntimeException(e);
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_READ );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_READ )) {
			
			if (hero.buff( Blindness.class ) != null) {
				GLog.w( TXT_BLINDED );
			} else {
				setCurUser(hero);
				curItem = detach( hero.belongings.backpack );
				
				doRead();
			}
			
		} else {
		
			super.execute( hero, action );
			
		}
	}
	
	abstract protected void doRead();
	
	public boolean isKnown() {
		return handler.isKnown( this );
	}
	
	public void setKnown() {
		if (!isKnown()) {
			handler.know( this );
		}
		
		Badges.validateAllScrollsIdentified();
	}
	
	@Override
	public Item identify() {
		setKnown();
		return super.identify();
	}
	
	@Override
	public String name() {
		return isKnown() ? name : Utils.format(Game.getVar(R.string.Scroll_Name), rune);
	}
	
	@Override
	public String info() {
		return isKnown() ? desc() : Utils.format(Game.getVar(R.string.Scroll_Info), rune);
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	public static HashSet<Class<? extends Scroll>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Scroll>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler.known().size() == scrolls.length;
	}
	
	@Override
	public int price() {
		return 15 * quantity();
	}
	
	@Override
	public Item burn(int cell){
		return null;
	}
}
