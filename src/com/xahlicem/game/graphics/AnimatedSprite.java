package com.xahlicem.game.graphics;

public class AnimatedSprite extends Sprite {

	public final int frames;
	protected Sprite[] sprites;

	public AnimatedSprite(int frames, int size, int x, int y, SpriteSheet sheet) {
		super(size, x, y, sheet);
		this.frames = frames;
		loadAnimations(x, y);
	}

	public void loadAnimations(int xPos, int yPos) {
		sprites = new Sprite[frames];
		for (int i = 0; i < sprites.length; i++)
			sprites[i] = new Sprite(size, xPos, yPos + i, sheet);
	}

	public Sprite getFrame(int currentFrame) {
		return sprites[currentFrame];
	}
}
