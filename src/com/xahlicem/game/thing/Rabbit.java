package com.xahlicem.game.thing;

import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.level.Level;

public class Rabbit extends Critter {

	public Rabbit(Level level, int x, int y) {
		super(level, SpriteSheet.THING_RABBIT, x, y);
	}

}
