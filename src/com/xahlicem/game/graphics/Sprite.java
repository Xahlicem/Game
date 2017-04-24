package com.xahlicem.game.graphics;

public class Sprite {

	public final int size;
	public int[] pixels;
	protected SpriteSheet sheet;

	public static final Sprite NULL = new Sprite(16, 0xFF66FF);
	public static final AnimatedSprite WATER = new AnimatedSprite(4, 16, 0, 0, SpriteSheet.TILES);
	public static final Sprite DIRT = new Sprite(16, 1, 0, SpriteSheet.TILES);
	public static final Sprite GRASS = new Sprite(16, 1, 1, SpriteSheet.TILES);
	public static final AnimatedSprite GRASS_GROWN = new AnimatedSprite(4, 16, 2, 0, SpriteSheet.TILES);
	public static final AnimatedSprite GRASS_TALL = new AnimatedSprite(4, 16, 3, 0, SpriteSheet.TILES);
	public static final AnimatedSprite FLOWERS = new AnimatedSprite(4, 16, 4, 0, SpriteSheet.TILES);

	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		this.size = size;
		this.sheet = sheet;
		pixels = new int[size*size];
		load(x * size, y * size, pixels);
	}

	public Sprite(int size, int pixelColor) {
		this.size = size;
		pixels = new int[size*size];
		for (int i = 0; i < pixels.length; i++) pixels[i] = pixelColor;
	}

	protected void load(int xPos, int yPos, int[] pixels) {
		for (int y = 0; y < size; y++)
			for (int x = 0; x < size; x++)
				pixels[x + y * size] = sheet.pixels[(xPos + x) + (yPos + y) * sheet.size];
	}
}
