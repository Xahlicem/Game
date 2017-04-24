package com.xahlicem.game.level.tile;

import java.util.Random;

import com.xahlicem.game.graphics.AnimatedSprite;
import com.xahlicem.game.graphics.Screen;

public class RandomAnimatedTile extends AnimatedTile {

	public static final Random R = new Random();
	public static final int CHANCE_MULTI = 100 * 60 * 10;

	protected final int chance;
	protected boolean active = false;

	public RandomAnimatedTile(int chance, int[] delay, AnimatedSprite sprites) {
		super(delay, sprites);
		this.chance = chance;
	}

	public RandomAnimatedTile(int chance, int delay, AnimatedSprite sprites) {
		super(delay, sprites);
		this.chance = chance;
	}

	public void tick() {
		if (active) {
			super.tick();
			if (delayIndex == 0) active = false;
		}
	}

	public void draw(int x, int y, Screen screen) {
		if (!active && R.nextInt(CHANCE_MULTI) <= chance) active = true;
		super.draw(x, y, screen);
	}

}
