package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.AnimatedSprite;
import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {

	protected Sprite sprite;
	protected boolean solid = false;

	public static final Tile NULL = new Tile(Sprite.NULL);
	public static final Tile DIRT = new Tile(Sprite.DIRT);
	public static final Tile GRASS = new AnimatedTile(new int[] { 150, 30, 45, 15 }, AnimatedSprite.GRASS);
	public static final Tile R_GRASS = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, AnimatedSprite.GRASS);
	public static final Tile WATER = new AnimatedTile(15, AnimatedSprite.WATER);

	public Tile(Sprite sprite) {
		this.sprite = sprite;
	}

	public Tile(Sprite sprite, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
	}

	public void tick() {
	}

	public void draw(int x, int y, Screen screen) {
		screen.drawSprite(x, y, sprite);
	}

	public boolean solid() {
		return solid;
	}
}