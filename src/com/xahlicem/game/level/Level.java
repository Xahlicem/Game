package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.helpers.audio.AudioPlayer;
import com.xahlicem.game.helpers.audio.Sound;
import com.xahlicem.game.level.tile.Tile;

public class Level {

	private static final int DAY_LIGHT = 0xFFFFFF;
	private static final int MORNING_LIGHT = 0xC0C0C0;
	private static final int EVENING_LIGHT = 0x80A0A0;
	private static final int TWI_LIGHT = 0x306080;
	private static final int NIGHT_LIGHT = 0x203040;
	private static final Random R = new Random();

	public int width, height, wMask, hMask;
	private int[] tiles, darkness;
	private List<Tile> tileList = new ArrayList<Tile>();
	private int time, light;
	private Sound[] bgm = new Sound[]{};
	private int bgmIndex = 0;
	private AudioPlayer midi, sfx;

	public static final Level TITLE = new Level("/level/TITLE", Sound.BGM_TITLE);

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		wMask = width - 1;
		hMask = height - 1;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(int width, int height, Sound... bgm) {
		this.width = width;
		this.height = height;
		wMask = width - 1;
		hMask = height - 1;
		this.bgm = bgm;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(String path) {
		loadLevel(path);
	}
	
	public Level(String path, Sound... bgm) {
		loadLevel(path);
		this.bgm = bgm;
	}

	private void loadLevel(String path) {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path + ".PNG"));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			tiles = new int[width*height];
			image.getRGB(0, 0, width, height, tiles, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path + "_L.PNG"));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			darkness = new int[width*height];
			image.getRGB(0, 0, width, height, darkness, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
			darkness = new int[width*height];
			for (int i = 0; i < darkness.length; i++) darkness[i] = 0xFFFFFF;
		}
		
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = tiles[i] & 0xFFFFFF;
			darkness[i] = darkness[i] & 0xFFFFFF;
			tiles[i] = Tile.getRandomColor(tiles[i]);
			if (!tileList.contains(Tile.getTile(tiles[i]))) tileList.add(Tile.getTile(tiles[i]));
		}
	}

	private void generateLevel() {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				tiles[x + y * width] = R.nextInt(10);
			}
	}
	
	public void init(AudioPlayer midi, AudioPlayer sfx) {
		this.midi = midi;
		this.sfx = sfx;
	}

	public void tick() {
		if (!midi.isPlaying()) {
			midi.setSound(bgm[bgmIndex++ % bgm.length]);
			midi.play();
		}
		time();
		for (Tile tile : tileList)
			tile.tick();
	}
	
	private void time() {
		time++;
		if (time > 10000) time = 0;
		
		if (time < 1500) light = NIGHT_LIGHT;
		else if (time < 2500) light = TWI_LIGHT;
		else if (time < 3000) light = MORNING_LIGHT;
		else if (time < 6000) light = DAY_LIGHT;
		else if (time < 7000) light = EVENING_LIGHT;
		else if (time < 7500) light = TWI_LIGHT;
		else light = NIGHT_LIGHT;
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		for (int y = yScroll >> 4; y <= (yScroll + screen.height + 32) >> 4; y++)
			for (int x = xScroll >> 4; x <= (xScroll + screen.width) >> 4; x++) {
				int l = darkness[(x&wMask) + (y&hMask) * width];
				if (l < light) l = light;
				getTile(x & wMask, y & hMask).draw(x << 4, y << 4, screen, l);
			}
	}

	public Tile getTile(int x, int y) {
		return getTile(x + y * width);
	}

	public Tile getTile(int i) {
		return Tile.getTile(tiles[i]);
	}
	
	public void changeTile(int x, int y, int tile) {
		tiles[x + y * width] = tile;
	}
}
