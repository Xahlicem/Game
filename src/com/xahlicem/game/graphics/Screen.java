package com.xahlicem.game.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Screen  extends Canvas {
	private static final long serialVersionUID = 2489505347406779762L;
	
	public static final int WIDTH = 300;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 4;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	private static final int INVISIBLE = 0xFF88FF;
	private int xOffset, yOffset;

	public Screen() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		
		setPreferredSize(size);
		setFocusable(true);
		requestFocus();
	}

	public void clear() {
		Arrays.fill(pixels, 0);
	}
	
	public void draw() {
		BufferStrategy strategy = getBufferStrategy();
		if (strategy == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g.dispose();

		strategy.show();
	}

	public void drawSprite(int xPos, int yPos, Sprite sprite, int darkness) {
		xPos -= xOffset;
		yPos -= yOffset;
		for (int y = 0; y < sprite.height; y++) {
			int ya = y + yPos;
			if (ya < 0 || ya >= HEIGHT) continue;
			for (int x = 0; x < sprite.width; x++) {
				int xa = x + xPos;
				if (xa < 0 || xa >= WIDTH) continue;
				int color = sprite.pixels[x + y * sprite.width];
				if (color == INVISIBLE) continue;
				pixels[xa + ya * WIDTH] = makeDarker(color, darkness);
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
			if (ya < 0 || ya >= HEIGHT) continue;
			for (int x = -w + 1; x < w + 1; x++) {
				int xa = x + h - 1 + xPos;
				if (xa < 0 || xa >= WIDTH) continue;
				int color = sprite.pixels[x + w - 1 + (y + h - 1) * sprite.width];
				if (color == INVISIBLE) continue;
				if (y <= 0) light = top;
				if (y > 0) light = bottom;
				if (x <= 0) light += left;
				if (x > 0) light += right;
				pixels[xa + ya * WIDTH] = makeDarker(color, (lights[0] + light));
			}
		}
	}
	
	public void drawFont(int xPos, int yPos, Sprite sprite, int color) {
		xPos -= xOffset;
		yPos -= yOffset;
		for (int y = 0; y < sprite.height; y++) {
			int ya = y + yPos;
			if (ya < 0 || ya >= HEIGHT) continue;
			for (int x = 0; x < sprite.width; x++) {
				int xa = x + xPos;
				if (xa < 0 || xa >= WIDTH) continue;
				if (sprite.pixels[x + y * sprite.width] == INVISIBLE) continue;
				pixels[xa + ya * WIDTH] = color;
			}
		}
	}

	public void drawString(int x, int y, String string, Sprite[] font, int color) {
		for (int i = 0; i < string.length(); i++)
			drawFont(x + font[0].width * i, y, font[string.charAt(i)], color);
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
