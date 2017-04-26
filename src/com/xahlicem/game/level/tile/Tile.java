package com.xahlicem.game.level.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {
	protected static final Random R = new Random();

	protected Sprite sprite;
	protected boolean solid = false;
	protected int color;

	public static final HashMap<Integer, Tile> tileColor = new HashMap<Integer, Tile>();
	public static List<Tile> list = new ArrayList<Tile>();

	public static final int WATER_COLOR = 0x0000FF;
	public static int waterIndex = 0;
	public static final int DIRT_COLOR = 0x964b00;
	public static int dirtIndex = 0;
	public static final int GRASS_COLOR = 0x00FF00;
	public static int grassIndex = 0;

	public static final Tile NULL = new Tile(Sprite.NULL, 0xFF66FF);
	public static final Tile WATER = new AnimatedTile(new int[] { 120, 10, 5, 15 }, Sprite.WATER, WATER_COLOR + waterIndex++);
	public static final Tile R_WATER = new RandomAnimatedTile(10, new int[] { 5, 10, 5, 15 }, Sprite.WATER, WATER_COLOR + waterIndex++);
	public static final Tile DIRT = new Tile(Sprite.DIRT, DIRT_COLOR + dirtIndex++);
	public static final Tile GRASS = new Tile(Sprite.GRASS, GRASS_COLOR + grassIndex++);
	public static final Tile SMALL_FLOWERS = new Tile(Sprite.SMALL_FLOWERS, GRASS_COLOR + grassIndex++);
	public static final Tile GRASS_GROWN = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_GROWN, GRASS_COLOR + grassIndex++);
	public static final Tile R_GRASS_GROWN = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_GROWN, GRASS_COLOR + grassIndex++);
	public static final Tile GRASS_TALL = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_TALL, GRASS_COLOR + grassIndex++);
	public static final Tile R_GRASS_TALL = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_TALL, GRASS_COLOR + grassIndex++);
	public static final Tile FLOWERS = new AnimatedTile(60, Sprite.FLOWERS, GRASS_COLOR + grassIndex++);
	public static final Tile R_FLOWERS = new RandomAnimatedTile(100, 5, Sprite.FLOWERS, GRASS_COLOR + grassIndex++);

	public Tile(Sprite sprite, int color) {
		this.sprite = sprite;
		this.color = color;
		tileColor.put(color, this);
		list.add(this);
	}

	public Tile(Sprite sprite, int color, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
		this.color = color;
		tileColor.put(color, this);
		list.add(this);
	}

	public static Tile getTile(int color) {
		if (!tileColor.containsKey(color)) return NULL;
		return tileColor.get(color);
	}

	public void tick() {}

	public void draw(int x, int y, Screen screen, int darkness) {
		screen.drawSprite(x, y, sprite, darkness);
	}

	public void draw(int x, int y, Screen screen) {
		screen.drawSprite(x, y, sprite, 0xFFFFFF);
	}

	public boolean solid() {
		return solid;
	}

	public int getColor() {
		return color;
	}

	public int getRandomColor(int color) {
		switch (color & 0xFFFFFF) {
		case Tile.WATER_COLOR:
			return Tile.WATER_COLOR + R.nextInt(Tile.waterIndex);
		case Tile.DIRT_COLOR:
			return Tile.DIRT_COLOR + R.nextInt(Tile.dirtIndex);
		case Tile.GRASS_COLOR:
			return Tile.GRASS_COLOR + R.nextInt(Tile.grassIndex);
		}
		return NULL.getColor();
	}
}