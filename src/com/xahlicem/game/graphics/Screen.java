package com.xahlicem.game.graphics;

import java.util.Arrays;

public class Screen {
	public int width;
	public int height;

	private int xOffset, yOffset;

	public int[] pixels;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public Screen(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

	public void clear() {
		// for (int i = 0; i < pixels.length; i++)
		// pixels[i] = 0;
		Arrays.fill(pixels, 0);
	}

	public void drawSprite(int xPos, int yPos, Sprite sprite, int darkness) {
		xPos -= xOffset;
		yPos -= yOffset;
		for (int y = 0; y < sprite.size; y++) {
			int ya = y + yPos;
			for (int x = 0; x < sprite.size; x++) {
				int xa = x + xPos;
				if (xa < 0 || xa >= width || ya < 0 || ya >= height) continue;
				pixels[xa + ya * width] = sprite.pixels[x + y * sprite.size] & darkness;
			}
		}
	}

	public void setOffset(int x, int y) {
		xOffset = x;
		yOffset = y;
	}
}
