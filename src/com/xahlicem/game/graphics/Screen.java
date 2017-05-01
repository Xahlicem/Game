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
			if (ya < 0 || ya >= height) continue;
			for (int x = 0; x < sprite.width; x++) {
				int xa = x + xPos;
				if (xa < 0 || xa >= width) continue;
				int color = sprite.pixels[x + y * sprite.width];
				if (color == INVISIBLE) continue;
				pixels[xa + ya * width] = makeDarker(color, darkness);
			}
		}
	}

	public void drawSprite(int xPos, int yPos, Sprite sprite, int... lights) {
		float top = (float) (lights[1]+lights[0] / (sprite.width >> 1)) * .5F;
		float bottom = (float) (lights[2]+lights[0] / (sprite.width >> 1)) * .5F;
		float left = (float) (lights[3]+lights[0] / (sprite.width >> 1)) * .5F;
		float right = (float) (lights[4]+lights[0] / (sprite.width >> 1)) * .5F;
		float light = top;

		xPos -= xOffset;
		yPos -= yOffset;
		int h = sprite.height >> 1;
		int w = sprite.width >> 1;
		for (int y = -h + 1; y < h + 1; y++) {
			int ya = y + h - 1 + yPos;
			if (ya < 0 || ya >= height) continue;
			for (int x = -w + 1; x < w + 1; x++) {
				int xa = x + h - 1 + xPos;
				if (xa < 0 || xa >= width) continue;
				int color = sprite.pixels[x + w - 1 + (y + h - 1) * sprite.width];
				if (color == INVISIBLE) continue;
				if (y <= 0) light = top;
				if (y > 0) light = bottom;
				if (x <= 0) light += left;
				if (x > 0) light += right;
				//p *= .5F;
				// if (p < 0) p = 0;
				pixels[xa + ya * width] = makeDarker(color, (lights[0] + light));
			}
		}
	}

	private int makeDarker(int rgb, float darkness) {
		float light = darkness * 0.125F;
		if (light >= 1f) return rgb;
		if (light < 0F) return 0;
		int r = (rgb >> 16) & 255; // red
		int g = (rgb >> 8) & 255; // green
		int b = rgb & 255; // blue
		// now reduce brightness of all channels to 1/3
		r *= light;
		g *= light;
		b *= light;
		// recombine channels and return
		return (r << 16) | (g << 8) | b;
	}

	private int makeDarker(int rgb, int darkness) {
		return makeDarker(rgb, (float) darkness);
	}

	public void setOffset(int x, int y) {
		xOffset = x;
		yOffset = y;
	}
}
