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
package com.annatala.pixelponies.levels.painters;

import com.annatala.pixelponies.items.Generator;
import com.annatala.pixelponies.items.Item;
import com.annatala.pixelponies.items.potions.PotionOfLiquidFlame;
import com.annatala.pixelponies.levels.Level;
import com.annatala.pixelponies.levels.Room;
import com.annatala.pixelponies.levels.Terrain;
import com.annatala.utils.Random;

public class StoragePainter extends Painter {

	public static void paint( Level level, Room room ) {
		
		final int floor = Terrain.EMPTY_SP;
		
		fill( level, room, Terrain.WALL );
		fill( level, room, 1, floor );
		
		int n = Random.IntRange( 3, 4 );
		for (int i=0; i < n; i++) { 
			int pos;
			do {
				pos = room.random(level);
			} while (level.map[pos] != floor);
			level.drop( prize( level ), pos );
		}
		
		room.entrance().set( Room.Door.Type.BARRICADE );
		level.addItemToSpawn( new PotionOfLiquidFlame() );
	}
	
	private static Item prize( Level level ) {
		
		Item prize = level.itemToSpanAsPrize();
		if (prize != null) {
			return prize;
		}
		
		return Generator.random( Random.oneOf( 
			Generator.Category.POTION, 
			Generator.Category.SCROLL,
			Generator.Category.FOOD, 
			Generator.Category.GOLD
		) );
	}
}