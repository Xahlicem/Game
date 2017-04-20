package com.xahlicem.game.helpers;

import java.util.Random;

public class Screen {
	private static final int MAP_SIZE = 64;
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

	public void draw(int camX, int camY) {

		for (int y = 0; y < height; y++) {
			int yOffset = y + camY;
			if (y < 0 || y >= height) break;
			for (int x = 0; x < width; x++) {
				int xOffset = x + camX;
				if (x < 0 || x >= width) break;
				//int tileIndex = (x >> 4) + (y >> 4) * 64;
				int tileIndex = ((xOffset >> 4) & MAP_SIZE_MASK) + ((yOffset >> 4) & MAP_SIZE_MASK) * MAP_SIZE;
				pixels[x + y * width] = tiles[tileIndex];
			}

		}
	}
}
