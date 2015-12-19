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
package com.watabou.pixeldungeon.actors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Statistics;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Actor implements Bundlable {
	
	public static final float TICK	= 1f;

	private float time;
	
	protected abstract boolean act();
	
	protected void spend( float time ) {
		this.time += time;
	}
	
	protected void postpone( float time ) {
		if (this.time < now + time) {
			this.time = now + time;
		}
	}
	
	protected float cooldown() {
		return time - now;
	}
	
	protected void deactivate() {
		time = Float.MAX_VALUE;
	}
	
	protected void onAdd() {}
	
	protected void onRemove() {}
	
	private static final String TIME = "time";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( TIME, time );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		time = bundle.getFloat( TIME );
	}
	
	@Override
	public boolean dontPack() {
		return false;
	}
	
	// **********************
	// *** Static members ***
	
	private static HashSet<Actor> all = new HashSet<Actor>();
	private static Actor current;
	
	private static float now = 0;
	
	private static Map<Integer, Char> chars = new HashMap<Integer, Char>();
	
	public static void clear(int len) {
		
		now = 0;
		chars.clear();
		all.clear();
	}
	
	public static void fixTime() {
		
		if (Dungeon.hero != null && all.contains( Dungeon.hero )) {
			Statistics.duration += now;
		}
		
		float min = Float.MAX_VALUE;
		for (Actor a : all) {
			if (a.time < min) {
				min = a.time;
			}
		}
		for (Actor a : all) {
			a.time -= min;
		}
		now = 0;
	}
	
	public static void init(Level level) {
		clear(level.getLength());
		
		addDelayed( Dungeon.hero, -Float.MIN_VALUE );
		
		for (Mob mob : level.mobs) {
			add( mob );
		}
		
		for (Blob blob : level.blobs.values()) {
			add( blob );
		}
		
		current = null;
	}
	
	public static void occupyCell( Char ch ) {
		chars.put(ch.getPos(), ch);
	}
	
	public static void freeCell( int pos ) {
		chars.remove(pos);
	}
	
	/*protected*/public void next() {
		if (current == this) {
			current = null;
		}
	}
	

	public static void processReaTime(float elapsed) {

		now += elapsed * 10;

		do {
			
			current = null;

			chars.clear();
			
			for (Actor actor : all) {
				if (actor.time < now) {
					current = actor;
				}

				if (actor instanceof Char) {
					Char ch = (Char) actor;
					chars.put(ch.getPos(), ch);
				}
			}

			if(current!= null) {
				current.act();
			}

			if (!Dungeon.hero.isAlive()) {
				break;
			}

		} while (current != null);

	}
	
	public static void process(float elapsed) {
		
		if(PixelDungeon.realtime()) {
			processReaTime(elapsed);
			return;
		}
		
		if (current != null) {
			return;
		}
	
		boolean doNext;
		
		Actor toRemove = null;
		
		do {
			now = Float.MAX_VALUE;
			current = null;
			
			chars.clear();
			
			
			for (Actor actor : all) { 
				if (actor.time < now) {
					now = actor.time;
					current = actor;
				}
				
				if (actor instanceof Char) {
					Char ch = (Char)actor;
					if(!Dungeon.level.cellValid(ch.getPos())) {
						current = null;
						toRemove = actor;
						continue;
					}
					chars.put(ch.getPos(), ch);
				}
			}

			if(toRemove != null) {
				remove(toRemove);
				toRemove = null;
			}
			
			if (current != null) {
				if (current instanceof Char && 
					((Char)current).getSprite() != null &&
					((Char)current).getSprite().isMoving) {
					// If it's character's turn to act, but its sprite 
					// is moving, wait till the movement is over
					current = null;
					break;
				}
				
				doNext = current.act();

				if (doNext && !Dungeon.hero.isAlive()) {
					doNext = false;
					current = null;
				}
			} else {
				doNext = false;
			}
			
		} while (doNext);
	}
	
	public static void add( Actor actor ) {
		add( actor, now );
	}
	
	public static void addDelayed( Actor actor, float delay ) {
		add( actor, now + delay );
	}
	
	private static void add( Actor actor, float time ) {
		
		if (all.contains( actor )) {
			return;
		}
		
		all.add( actor );
		actor.time += time;	// (+=) => (=) ?
		actor.onAdd();
		
		if (actor instanceof Char) {
			Char ch = (Char)actor;
			chars.put(ch.getPos(), ch);
			for (Buff buff : ch.buffs()) {
				all.add( buff );
				buff.onAdd();
			}
		}
	}
	
	public static void remove( Actor actor ) {
		
		if (actor != null) {
			if(current == actor) {
				current = null;
			}
			
			all.remove( actor );
			actor.onRemove();
		}
	}
	
	public static Char findChar(int pos) {
		return chars.get(pos);
	}
	
	public static HashSet<Actor> all() {
		return all;
	}
}
