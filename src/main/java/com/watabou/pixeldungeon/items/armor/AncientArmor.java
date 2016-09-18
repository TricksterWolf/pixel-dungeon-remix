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
package com.watabou.pixeldungeon.items.armor;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.actors.hero.HeroSubClass;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.utils.GLog;


public class AncientArmor extends Armor {

	private static final String TXT_UNICORN	= Game.getVar(R.string.AncientArmor_NotUnicorn);

	public AncientArmor() {
		super( 6 );
		image = 5;
	}

	@Override
	public boolean doEquip( Hero hero ) {
		if (hero.heroClass == HeroClass.UNICORN) {
			return super.doEquip( hero );
		} else {
			GLog.w( TXT_UNICORN );
			return false;
		}
	}

	@Override
	public String desc() {
		return Game.getVar(R.string.AncientArmor_Desc);
	}

}
