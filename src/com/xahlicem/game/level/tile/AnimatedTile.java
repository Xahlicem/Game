package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.AnimatedSprite;

public class AnimatedTile extends Tile {

	protected final int[] delay;
	protected int delayIndex = 0;
	protected int ticks = 0;
	protected AnimatedSprite sprites;
	protected int currentFrame = 0;

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
