package com.xahlicem.game.level.menu;

import com.xahlicem.game.Game;
import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.level.Level;
import com.xahlicem.game.level.tile.Tile;

public class SliderMenuLevel extends MenuLevel {
	protected int value, maxValue;

	public SliderMenuLevel(Level level, int value, int maxValue) {
		super(level, "Volume", "Back");
		this.value = value;
		this.maxValue = maxValue;
		changeSlider();
	}

	@Override
	protected void init(String[] options) {
		height = 12;
		tiles = new int[width * height];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				changeTile(x, y, Tile.GRASS, true);
				changeLight(x, y, 1);
			}
		changeTile(3, 4, Tile.DIRT);
		this.name = options[0];
		this.options = new String[options.length - 1];
		System.arraycopy(options, 1, this.options, 0, this.options.length);

		changeTiles(Tile.WATER);
	}

	@Override
	protected void drawOtherStuff(Screen screen) {
		screen.drawSprite(48, 64 + y, Sprite.FONT[16], 0);
		screen.drawString(64, y, name, Sprite.FONT, 0);
	}

	@Override
	protected void drawOption(int x, int y, Screen screen) {}

	@Override
	protected void move(Input input) {
		if (input.isKeyPressed(Input.KEY_RIGHT)) {
			if (right) {
				value += 1;
				if (maxValue > 10) value = 10;
				changeSlider();
			}
			right = false;
		} else right = true;
		if (input.isKeyPressed(Input.KEY_LEFT)) {
			if (left) {
				value -= 1;
				if (maxValue < 0) value = 0;
				changeSlider();
			}
			left = false;
		} else left = true;
	}

	protected void changeSlider() {
		for (int i = 0; i < 10; i++) {
			changeTile(4 + i, 4, (i >= value) ? Tile.DIRT : Tile.WATER);
		}
		switch (name.toUpperCase()) {
			case "VOLUME":
				Game.vol = value;
				Game.setVolume();
				break;
		}
	}
}
