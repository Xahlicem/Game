package com.xahlicem.game.graphics;

import java.util.Random;

public class Screen {
	private static final int MAP_SIZE = 4;
	private static final int MAP_SIZE_MASK = MAP_SIZE - 1;
	private int width, height;

	public int[] pixels;
	public int tiles[] = new int[MAP_SIZE * MAP_SIZE];

	private Random random = new Random();

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];

		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = random.nextInt(0xFFFFFF);
		}
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
	}

	public void draw(int camX, int camY, int[] pixels) {

		for (int y = 0; y < height; y++) {
			int yOffset = y + camY;
			if (y < 0 || y >= height) continue;
			for (int x = 0; x < width; x++) {
				int xOffset = x + camX;
				if (x < 0 || x >= width) continue;
				//int tileIndex = (x >> 4) + (y >> 4) * 64;
				int tileIndex = ((xOffset >> 4) & MAP_SIZE_MASK) + ((yOffset >> 4) & MAP_SIZE_MASK) * MAP_SIZE;
				if ((tiles[tileIndex]&2) == 2)pixels[x + y * width] = Sprite.DIRT.pixels[(xOffset&15) + (yOffset&15)*16];//tiles[tileIndex];
				else if ((tiles[tileIndex]&1) == 1)pixels[x + y * width] = Sprite.GRASS.pixels[(xOffset&15) + (yOffset&15)*16];
				else pixels[x + y * width] = Sprite.WATER.pixels[(xOffset&15) + (yOffset&15)*16];
			}

		}
	}
	
	public void drawSprite(int xPos, int yPos, Sprite sprite) {
		for (int y = 0; y < sprite.size; y++) {
			int ya = y + yPos;
			for (int x = 0; x < sprite.size; x++) {
				int xa = x + xPos;
			}
		}
	}
}
