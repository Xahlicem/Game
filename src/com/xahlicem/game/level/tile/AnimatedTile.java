package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.AnimatedSprite;

public class AnimatedTile extends Tile {

	protected final int[] delay;
	protected int delayIndex = 0;
	protected int ticks = 0;
	protected AnimatedSprite sprites;

	public static final AnimatedTile GRASS = new AnimatedTile(new int[] { 150, 30, 45, 15 }, AnimatedSprite.GRASS);
	public static final AnimatedTile WATER = new AnimatedTile(15, AnimatedSprite.WATER);

	public AnimatedTile(int[] delay, AnimatedSprite sprites) {
		super(sprites);
		this.sprites = sprites;
		this.delay = delay;
	}

	public AnimatedTile(int delay, AnimatedSprite sprites) {
		super(sprites);
		this.sprites = sprites;
		this.delay = new int[] { delay };
	}

	public Tile tick() {
		ticks++;
		if (ticks == delay[delayIndex]) {
			delayIndex = (++delayIndex) % delay.length;
			ticks = 0;
			sprites.nextFrame();
		}
		return this;
	}
}
