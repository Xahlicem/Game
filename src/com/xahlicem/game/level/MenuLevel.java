package com.xahlicem.game.level;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.thing.MobileThing;

public class MenuLevel extends Level{

	public MenuLevel() {
		super(16, 9);
		addThing(new MobileThing(this, new Sprite(16, 0xFF0000), 0, 0));
		addThing(new MobileThing(this, new Sprite(16, 0xFF0000), 0, 64));
		addThing(new MobileThing(this, new Sprite(16, 0xFF0000), 64, 0));
		addThing(new MobileThing(this, new Sprite(16, 0xFF0000), 64, 64));
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		super.draw(xScroll, yScroll, screen);
	}

}
