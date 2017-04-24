package com.xahlicem.game.graphics;

public class Sprite {

	public final int size;
	public int[] pixels;
	private SpriteSheet sheet;

	public static final Sprite DIRT = new Sprite(16, 0, 0, SpriteSheet.TILES);
	public static final Sprite GRASS = new Sprite(16, 1, 0, SpriteSheet.TILES);
	public static final Sprite WATER = new Sprite(16, 2, 0, SpriteSheet.TILES);

	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		this.size = size;
		this.sheet = sheet;
		pixels = new int[size*size];
		load(x * size, y * size);
	}

	private void load(int xPos, int yPos) {
		for (int y = 0; y < size; y++)
			for (int x = 0; x < size; x++)
				pixels[x + y * size] = sheet.pixels[(xPos + x) + (yPos + y) * sheet.size];
	}
}
