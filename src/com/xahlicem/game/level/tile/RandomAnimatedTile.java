package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.AnimatedSprite;

public class RandomAnimatedTile extends AnimatedTile {
	public static final int CHANCE_MULTI = 100 * 60 * 10;

	protected final int chance;
	protected boolean active = false;

	public RandomAnimatedTile(int chance, int[] delay, AnimatedSprite sprites, int color) {
		super(delay, sprites, color);
		this.chance = chance;
	}

	public RandomAnimatedTile(int chance, int delay, AnimatedSprite sprites, int color) {
		super(delay, sprites, color);
		this.chance = chance;
	}

	public void tick() {
		if (!active && R.nextInt(CHANCE_MULTI) <= chance) active = true;
		if (active) {
			super.tick();
			if (delayIndex == 0) active = false;
		}
	}

}
