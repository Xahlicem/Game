package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.AnimatedSprite;

public class AnimatedTile extends Tile {

	protected final int[] delay;
	protected int delayIndex = 0;
	protected int ticks = 0;
	protected AnimatedSprite sprites;
	protected int currentFrame = 0;

	public AnimatedTile(TileType type, AnimatedSprite sprites, int[] delay) {
		super(type, sprites);
		this.sprites = sprites;
		this.delay = delay;
	}

	public AnimatedTile(TileType type, AnimatedSprite sprites, int delay) {
		super(type, sprites);
		this.sprites = sprites;
		if (delay <= 0) delay = 1;
		this.delay = new int[] { delay };
	}

	public void tick() {
		ticks++;
		if (ticks == delay[delayIndex]) {
			delayIndex = (++delayIndex) % delay.length;
			ticks = 0;
			nextFrame();
		}
	}

	public void nextFrame() {
		currentFrame = (++currentFrame) % sprites.frames;
		sprite = sprites.getFrame(currentFrame);
	}
}
