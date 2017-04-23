package com.xahlicem.game.graphics;

public class Sprite {

	private final int size;
	private int x, y;
	public int[] pixels;
	private SpriteSheet sheet;

	public static final Sprite DIRT = new Sprite(16, 0, 0, SpriteSheet.TILES);
	public static final Sprite GRASS = new Sprite(16, 1, 0, SpriteSheet.TILES);
	public static final Sprite WATER = new Sprite(16, 2, 0, SpriteSheet.TILES);

	public Sprite(int size, int x, int y, SpriteSheet sheet) {
		this.size = size;
		this.x = x * size;
		this.y = y * size;
		this.sheet = sheet;
		pixels = new int[size*size];
		load();
	}

	private void load() {
		for (int y = 0; y < size; y++)
			for (int x = 0; x < size; x++)
				pixels[x + y * size] = sheet.pixels[(this.x + x) + (this.y + y) * sheet.size];
	}
}
