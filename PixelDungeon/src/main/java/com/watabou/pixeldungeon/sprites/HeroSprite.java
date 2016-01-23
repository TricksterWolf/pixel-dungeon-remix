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
package com.watabou.pixeldungeon.sprites;

import android.graphics.RectF;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.effects.Hat;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class HeroSprite extends CharSprite {

	private static final int FRAME_WIDTH = 12;
	private static final int FRAME_HEIGHT = 15;

	private static final int RUN_FRAMERATE = 20;

	private TextureFilm tiers;

	private Animation fly;

	private Tweener  jumpTweener;
	private Callback jumpCallback;

	private Hat hat;
	
	private int lastTier = -1;

	public HeroSprite(Hero hero) {
		super();

		link(hero);

		texture(HeroClass.spritesheet(hero));
		updateArmor(hero.tier());
		
		idle();
		
		//hat = new Hat.Test(this);
		//hat.setVisible(true);
	}

	public HeroSprite(Hero hero, boolean b) {
		super();

		texture(HeroClass.spritesheet(hero));
		updateArmor(hero.tier());

		idle();
	}

	public void updateState(Hero hero) {
		GameScene.updateHeroSprite(hero);
	}

	public void updateArmor(int tier) {

		TextureFilm film = new TextureFilm(tiers(), tier, FRAME_WIDTH,
				FRAME_HEIGHT);

		idle = new Animation(1, true);
		idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);

		run = new Animation(RUN_FRAMERATE, true);
		run.frames(film, 2, 3, 4, 5, 6, 7);

		die = new Animation(20, false);
		die.frames(film, 8, 9, 10, 11, 12, 11);

		attack = new Animation(15, false);
		attack.frames(film, 13, 14, 15, 0);

		zap = attack.clone();

		operate = new Animation(8, false);
		operate.frames(film, 16, 17, 16, 17);

		fly = new Animation(1, true);
		fly.frames(film, 18);
	}

	@Override
	public void place(int p) {
		super.place(p);
		Camera.main.target = this;
	}

	@Override
	public void move(int from, int to) {
		super.move(from, to);
		if (ch.flying) {
			play(fly);
		}
		Camera.main.target = this;
	}

	public void jump(int from, int to, Callback callback) {
		jumpCallback = callback;

		int distance = Dungeon.level.distance(from, to);
		jumpTweener = new JumpTweener(this, worldToCamera(to), distance * 4,
				distance * 0.1f);
		jumpTweener.listener = this;
		getParent().add(jumpTweener);

		turnTo(from, to);
		play(fly);
	}

	@Override
	public void onComplete(Tweener tweener) {
		if (tweener == jumpTweener) {

			if (getVisible() && Dungeon.level.water[ch.getPos()] && !ch.flying) {
				GameScene.ripple(ch.getPos());
			}
			if (jumpCallback != null) {
				jumpCallback.call();
			}

		} else {
			super.onComplete(tweener);
		}
	}

	@Override
	public void update() {
		sleeping = ((Hero) ch).restoreHealth;

		super.update();
	}

	public boolean sprint(boolean on) {
		run.delay = on ? 0.625f / RUN_FRAMERATE : 1f / RUN_FRAMERATE;
		return on;
	}

	public TextureFilm tiers() {
		if (tiers == null) {
			// Sprites for all classes are the same in size
			SmartTexture texture = TextureCache.get(Assets.ROGUE);
			tiers = new TextureFilm(texture, texture.width, FRAME_HEIGHT);
		}

		return tiers;
	}

	public Image avatar(Hero hero) {
		if (hero.tier() == lastTier) {
			return null;
		}

		RectF patch = tiers().get(hero.tier());
		Image avatar = new Image(HeroClass.spritesheet(hero));
		RectF frame = avatar.texture.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT);
		frame.offset(patch.left, patch.top);
		avatar.frame(frame);
		lastTier = hero.tier();
		return avatar;

	}

	private static class JumpTweener extends Tweener {

		public Visual visual;

		public PointF start;
		public PointF end;

		public float height;

		public JumpTweener(Visual visual, PointF pos, float height, float time) {
			super(visual, time);

			this.visual = visual;
			start = visual.point();
			end = pos;

			this.height = height;
		}

		@Override
		protected void updateValues(float progress) {
			visual.point(PointF.inter(start, end, progress).offset(0,
					-height * 4 * progress * (1 - progress)));
		}
	}
}