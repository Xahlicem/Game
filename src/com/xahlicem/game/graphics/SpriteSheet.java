package com.xahlicem.game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

	private String path;
	public int width, height;
	public int[] pixels;

	public static final SpriteSheet TILES = new SpriteSheet("/textures/TILES.PNG");
	public static final SpriteSheet FONT = new SpriteSheet("/textures/FONT_TINY.PNG");

	public SpriteSheet(String path) {
		this.path = path;
		load();
	}

	private void load() {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
			for (int i = 0; i < pixels.length; i++) pixels[i] &= 0xFFFFFF;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
