package com.xahlicem.game.graphics;

public class AnimatedSprite extends Sprite {

	protected int frames;
	protected int currentFrame = 0;
	public int[][] sprites;

	public static final AnimatedSprite WATER = new AnimatedSprite(3, 16, 2, 0, SpriteSheet.TILES);

	public AnimatedSprite(int frames, int size, int x, int y, SpriteSheet sheet) {
		super(size, x, y, sheet);
		this.frames = frames;
		loadAnimations(x * size, y * size);
	}

	public void loadAnimations(int xPos, int yPos) {
		sprites = new int[frames][];
		for (int i = 0; i < sprites.length; i++) {
			load(xPos, yPos + (i * size), sprites[i]);
		}
	}
}
