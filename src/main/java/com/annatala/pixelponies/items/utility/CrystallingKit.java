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
package com.annatala.pixelponies.items.utility;

import com.annatala.pixelponies.android.R;
import com.annatala.noosa.Game;
import com.annatala.noosa.audio.Sample;
import com.annatala.pixelponies.Assets;
import com.annatala.pixelponies.actors.hero.Hero;
import com.annatala.pixelponies.effects.Speck;
import com.annatala.pixelponies.items.Item;
import com.annatala.pixelponies.items.barding.Barding;
import com.annatala.pixelponies.items.barding.ClassBarding;
import com.annatala.pixelponies.scenes.GameScene;
import com.annatala.pixelponies.sprites.ItemSpriteSheet;
import com.annatala.utils.GLog;
import com.annatala.pixelponies.windows.WndBag;

import java.util.ArrayList;

public class CrystallingKit extends Item {
	
	private static final String TXT_SELECT_BARDING = Game.getVar(R.string.CrystallingKit_SelectBarding);
	private static final String TXT_UPGRADED     = Game.getVar(R.string.CrystallingKit_Upgraded);
	
	private static final float TIME_TO_UPGRADE = 2;
	
	private static final String AC_APPLY = Game.getVar(R.string.CrystallingKit_ACAplly);
	
	{
		name = Game.getVar(R.string.CrystallingKit_Name);
		image = ItemSpriteSheet.KIT;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_APPLY );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals(AC_APPLY)) {
			setCurUser(hero);
			GameScene.selectItem( itemSelector, WndBag.Mode.BARDING, TXT_SELECT_BARDING);
		} else {
			super.execute( hero, action );
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
	
	private void upgrade( Barding barding) {
		
		detach( getCurUser().belongings.backpack );
		
		getCurUser().getSprite().centerEmitter().start( Speck.factory( Speck.KIT ), 0.05f, 10 );
		getCurUser().spend( TIME_TO_UPGRADE );
		getCurUser().busy();
		
		GLog.w( TXT_UPGRADED, barding.name() );
		
		Barding classBarding = ClassBarding.upgrade( getCurUser(), barding);
		if (getCurUser().belongings.barding == barding) {
			getCurUser().belongings.barding = classBarding;
			getCurUser().updateLook();
		} else {
			barding.detach( getCurUser().belongings.backpack );
			getCurUser().collect(classBarding);
		}
		
		getCurUser().getSprite().operate( getCurUser().getPos() );
		Sample.INSTANCE.play( Assets.SND_EVOKE );
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				CrystallingKit.this.upgrade( (Barding)item );
			}
		}
	};
}
