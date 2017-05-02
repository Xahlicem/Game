package com.xahlicem.game.thing;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.level.Level;

public class MobileThing extends Thing{
	
	protected Sprite sprite;

	public MobileThing(Level level, Sprite sprite, int x, int y) {
		super(level);
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		//level.addMob(this);
	}

	@Override
	public void tick() {
		move(1, 1);
	}
	
	public int getPos() {
		return level.getPos(x, y);
	}
	
	public int getTilePos() {
		return level.getPos(x>>4, y>>4);
	}
	
	public void move(int x, int y) {
		this.x = level.getX(this.x + x);
		this.y = level.getY(this.y + y);
	}

	@Override
	public void draw(Screen screen) {
		screen.drawSprite(x, y, sprite, 8);
	}

	public void draw(Screen screen, int x2, int y2, int[] lights) {
		screen.drawSprite(x2, y2, sprite, 8);
	}

}
