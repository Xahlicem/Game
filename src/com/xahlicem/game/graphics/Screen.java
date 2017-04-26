package com.xahlicem.game.graphics;

import java.util.Arrays;

public class Screen {
	public int width;
	public int height;
	
	private static final int INVISIBLE = 0xFF88FF;
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
		for (int y = 0; y < sprite.height; y++) {
			int ya = y + yPos;
			for (int x = 0; x < sprite.width; x++) {
				int xa = x + xPos;
				if (xa < 0 || xa >= width || ya < 0 || ya >= height) continue;
				int color = sprite.pixels[x + y * sprite.width];
				if (color == INVISIBLE) continue;
				pixels[xa + ya * width] = color & darkness;
			}
		}
	}

	public void setOffset(int x, int y) {
		xOffset = x;
		yOffset = y;
	}
}
