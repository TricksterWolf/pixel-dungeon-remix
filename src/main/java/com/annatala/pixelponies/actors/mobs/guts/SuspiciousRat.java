package com.annatala.pixelponies.actors.mobs.guts;

import com.annatala.pixelponies.android.R;
import com.annatala.noosa.Game;
import com.annatala.noosa.audio.Sample;
import com.annatala.pixelponies.Assets;
import com.annatala.pixelponies.Dungeon;
import com.annatala.pixelponies.actors.Char;
import com.annatala.pixelponies.actors.blobs.ToxicGas;
import com.annatala.pixelponies.actors.mobs.Mob;
import com.annatala.pixelponies.sprites.CharSprite;
import com.annatala.utils.GLog;
import com.annatala.utils.Bundle;
import com.annatala.utils.Callback;
import com.annatala.utils.Random;

/**
 * Created by DeadDie on 12.02.2016
 */
public class SuspiciousRat extends Mob {

	private static final float TIME_TO_HATCH = 4f;

	{
		hp(ht(140));
		defenseSkill = 25;

		EXP = 1;
		maxLvl = 30;

		pacified = true;

		IMMUNITIES.add(ToxicGas.class);
	}

	private static final String RAT_TRANSFORMING_STATE = "rat_transforming_state";

	private boolean transforming = false;

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);

		bundle.put(RAT_TRANSFORMING_STATE, transforming);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		super.restoreFromBundle(bundle);
		transforming = bundle.getBoolean(RAT_TRANSFORMING_STATE);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(10, 15);
	}

	@Override
	public int attackSkill(Char target) {
		return 25;
	}

	@Override
	public int dr() {
		return 2;
	}

	@Override
	public boolean act() {
		if (enemySeen) {
			if (!transforming) {
				spend(TIME_TO_HATCH);
				transforming = true;
				if (Dungeon.visible[getPos()]) {
					getSprite().showStatus(CharSprite.NEGATIVE, Game.getVar(R.string.Goo_StaInfo1));
					GLog.n(Game.getVar(R.string.SuspiciousRat_Info1));
				}
				PlayZap();
				return true;
			} else {
				int suspiciousRatPos = this.getPos();
				if (Dungeon.level.cellValid(suspiciousRatPos)) {
					PseudoRat mob = new PseudoRat();
					mob.setPos(suspiciousRatPos);
					Dungeon.level.spawnMob(mob, 0);
					Sample.INSTANCE.play(Assets.SND_CURSED);
				}
				die(this);
				return true;
			}
		}
		return super.act();
	}

	@Override
	public void onZapComplete() {
		PlayZap();
	}

	public void PlayZap() {
		getSprite().zap(
				getEnemy().getPos(),
				new Callback() {
					@Override
					public void call() {
					}
				});
	}
}
