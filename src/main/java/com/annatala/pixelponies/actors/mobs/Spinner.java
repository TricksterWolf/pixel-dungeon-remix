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

import com.annatala.pixelponies.actors.Char;
import com.annatala.pixelponies.actors.blobs.Blob;
import com.annatala.pixelponies.actors.blobs.Web;
import com.annatala.pixelponies.actors.buffs.Buff;
import com.annatala.pixelponies.actors.buffs.Poison;
import com.annatala.pixelponies.actors.buffs.Roots;
import com.annatala.pixelponies.actors.buffs.Terror;
import com.annatala.pixelponies.items.food.MysteriousHay;
import com.annatala.pixelponies.scenes.GameScene;
import com.annatala.pixelponies.sprites.SpinnerSprite;
import com.annatala.utils.Random;

public class Spinner extends Mob {
	
	public Spinner() {
		spriteClass = SpinnerSprite.class;
		
		hp(ht(50));
		defenseSkill = 14;
		
		EXP = 9;
		maxLvl = 16;
		
		loot = new MysteriousHay();
		lootChance = 0.125f;
		
		FLEEING = new Fleeing();
		
		RESISTANCES.add( Poison.class );
		IMMUNITIES.add( Roots.class );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 12, 16 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20;
	}
	
	@Override
	public int dr() {
		return 6;
	}
	
	@Override
	protected boolean act() {
		boolean result = super.act();
		
		if (state == FLEEING  && buff( Terror.class ) == null && 
			enemySeen && getEnemy().buff( Poison.class ) == null) {
			
			state = HUNTING;
		}
		return result;
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 5 ) < 3 && !Random.luckBonus()) {
			Buff.affect( enemy, Poison.class ).set( Random.Int( 7, 9 ) * Poison.durationFactor( enemy ) );
			state = FLEEING;
		}
		
		return damage;
	}
	
	@Override
	public void move( int step ) {
		if (state == FLEEING) {
			GameScene.add( Blob.seed( getPos(), Random.Int( 5, 7 ), Web.class ) );
		}
		super.move( step );
	}
	
	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff( Terror.class ) == null) {
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}
}
