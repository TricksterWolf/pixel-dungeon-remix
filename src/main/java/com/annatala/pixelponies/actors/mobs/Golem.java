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
import com.annatala.pixelponies.actors.buffs.Amok;
import com.annatala.pixelponies.actors.buffs.Sleep;
import com.annatala.pixelponies.actors.buffs.Terror;
import com.annatala.pixelponies.actors.mobs.npcs.Imp;
import com.annatala.pixelponies.items.scrolls.ScrollOfPsionicBlast;
import com.annatala.pixelponies.sprites.GolemSprite;
import com.annatala.utils.Random;

public class Golem extends Mob {
	
	public Golem() {
		spriteClass = GolemSprite.class;
		
		hp(ht(85));
		defenseSkill = 18;
		
		EXP = 12;
		maxLvl = 22;
		
		RESISTANCES.add( ScrollOfPsionicBlast.class );
		
		IMMUNITIES.add( Amok.class );
		IMMUNITIES.add( Terror.class );
		IMMUNITIES.add( Sleep.class );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 20, 40 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	protected float attackDelay() {
		return 1.5f;
	}
	
	@Override
	public int dr() {
		return 12;
	}
	
	@Override
	public void die( Object cause ) {
		Imp.Quest.process( this );
		
		super.die( cause );
	}
}