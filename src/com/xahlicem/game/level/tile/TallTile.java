package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class TallTile extends Tile {
	
	protected Sprite[] sprites;

	public TallTile(int color, Sprite...sprites) {
		super(sprites[0], color);
		this.sprites = sprites;
	}
	
	public void draw(int x, int y, Screen screen, int darkness) {
		screen.drawSprite(x, y, sprite, darkness);
		for (int i = 1; i < sprites.length; i++) screen.drawSprite(x, y - sprite.height * i, sprites[i], darkness);
	}

}
