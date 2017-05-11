package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class TallTile extends Tile {
	
	protected Sprite[] sprites;

	public TallTile(TileType type, boolean solid, Sprite...sprites) {
		super(type, sprites[0], solid);
		this.sprites = sprites;
	}
	
	public void draw(int x, int y, Screen screen, int darkness) {
		screen.drawSprite(x, y, sprite, darkness);
		for (int i = 1; i < sprites.length; i++) screen.drawSprite(x, y - sprite.height * i, sprites[i], darkness);
	}
	
	public void draw(int x, int y, Screen screen, int... lights) {
		screen.drawSprite(x, y, sprite, lights[0], lights[0], lights[2], lights[3], lights[4]);
		for (int i = 1; i < sprites.length; i++) screen.drawFrontSprite(x, y - sprite.height * i, sprites[i], lights[0], lights[0], lights[0], lights[3], lights[4]);
	}

}
