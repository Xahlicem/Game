package com.xahlicem.game.level.tile;

import java.util.Random;

import com.xahlicem.game.graphics.AnimatedSprite;
import com.xahlicem.game.graphics.Screen;

public class RandomAnimatedTile extends AnimatedTile {
	
	public static final Random R = new Random();
	
	protected final int chance;
	protected boolean active = false;

	public static final RandomAnimatedTile R_GRASS = new RandomAnimatedTile(10, new int[] { 5, 30, 45, 15 }, AnimatedSprite.GRASS);

	public RandomAnimatedTile(int chance, int[] delay, AnimatedSprite sprites) {
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
		if (!active && R.nextInt(100000) <= chance) active = true;
		super.draw(x, y, screen);
	}

}
