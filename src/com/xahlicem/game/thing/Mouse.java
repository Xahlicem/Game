package com.xahlicem.game.thing;

import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.level.Level;

public class Mouse extends Critter {

	public Mouse(Level level, int x, int y) {
		super(level, SpriteSheet.THING_MOUSE, x, y);
	}
	
	public void init() {
		widthOffset = -6;
		heightOffset = -6;
		yOffset = 5;
		xOffset = 3;
		super.init();
	}

}
