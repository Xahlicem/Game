package com.xahlicem.game.thing;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.level.Level;

public abstract class Thing {
	public int x, y;
	protected Level level;

	public Thing(Level level) {
		this.level = level;
	}

	public abstract void tick();

	public abstract void draw(Screen screen);
}
