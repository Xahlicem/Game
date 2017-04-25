package com.xahlicem.game.level.tile;

import java.util.ArrayList;
import java.util.List;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {

	protected Sprite sprite;
	protected boolean solid = false;

	public static List<Tile> animated = new ArrayList<Tile>();
	public static List<Tile> tiles = new ArrayList<Tile>();

	public static final Tile NULL = new Tile(Sprite.NULL);
	public static final Tile WATER = new AnimatedTile(new int[] { 120, 10, 5, 15 }, Sprite.WATER);
	public static final Tile R_WATER = new RandomAnimatedTile(10, new int[] { 5, 10, 5, 15 }, Sprite.WATER);
	public static final Tile DIRT = new Tile(Sprite.DIRT);
	public static final Tile GRASS = new Tile(Sprite.GRASS);
	public static final Tile GRASS_GROWN = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_GROWN);
	public static final Tile R_GRASS_GROWN = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_GROWN);
	public static final Tile GRASS_TALL = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_TALL);
	public static final Tile R_GRASS_TALL = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_TALL);
	public static final Tile FLOWERS = new AnimatedTile(60, Sprite.FLOWERS);
	public static final Tile R_FLOWERS = new RandomAnimatedTile(100, 5, Sprite.FLOWERS);

	public Tile(Sprite sprite) {
		this.sprite = sprite;
		tiles.add(this);
	}

	public Tile(Sprite sprite, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
		tiles.add(this);
	}

	public void tick() {}

	public void draw(int x, int y, Screen screen) {
		screen.drawSprite(x, y, sprite);
	}

	public boolean solid() {
		return solid;
	}
}