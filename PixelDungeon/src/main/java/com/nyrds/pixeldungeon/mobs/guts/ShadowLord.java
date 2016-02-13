package com.nyrds.pixeldungeon.mobs.guts;

import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.mobs.Boss;
import com.watabou.utils.Random;

/**
 * Created by DeadDie on 13.02.2016
 */
public class ShadowLord extends Boss {

	public ShadowLord() {
		hp(ht(260));
		defenseSkill = 34;

		EXP = 56;

		lootChance = 0.5f;
	}

	@Override
	public boolean isAbsoluteWalker() {
		return true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20, 60);
	}

	@Override
	public int attackSkill(Char target) {
		return 36;
	}

	@Override
	public int dr() {
		return 2;
	}

}