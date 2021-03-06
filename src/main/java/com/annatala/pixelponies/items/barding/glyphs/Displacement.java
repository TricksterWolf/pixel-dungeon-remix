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
package com.annatala.pixelponies.items.barding.glyphs;

import com.annatala.noosa.Game;
import com.annatala.pixelponies.Dungeon;
import com.annatala.pixelponies.android.R;
import com.annatala.pixelponies.actors.Actor;
import com.annatala.pixelponies.actors.Char;
import com.annatala.pixelponies.items.barding.Barding;
import com.annatala.pixelponies.items.barding.Barding.Glyph;
import com.annatala.pixelponies.items.wands.WandOfBlink;
import com.annatala.pixelponies.sprites.ItemSprite;
import com.annatala.pixelponies.sprites.ItemSprite.Glowing;
import com.annatala.utils.Utils;
import com.annatala.utils.Random;

public class Displacement extends Glyph {

	private static final String TXT_DISPLACEMENT = Game.getVar(R.string.Displacement_Txt);
	
	private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x66AAFF );
	
	@Override
	public int proc(Barding barding, Char attacker, Char defender, int damage ) {

		if (Dungeon.bossLevel()) {
			return damage;
		}
		
		int nTries = (barding.level() < 0 ? 1 : barding.level() + 1) * 5;
		for (int i=0; i < nTries; i++) {
			int pos = Random.Int( Dungeon.level.getLength() );
			if (Dungeon.visible[pos] && Dungeon.level.passable[pos] && Actor.findChar( pos ) == null) {
				
				WandOfBlink.appear( defender, pos );
				Dungeon.level.press( pos, defender );
				Dungeon.observe();

				break;
			}
		}
		
		return damage;
	}
	
	@Override
	public String name( String weaponName) {
		return Utils.format( TXT_DISPLACEMENT, weaponName );
	}

	@Override
	public Glowing glowing() {
		return BLUE;
	}
}
