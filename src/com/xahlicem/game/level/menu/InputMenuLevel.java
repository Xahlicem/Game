package com.xahlicem.game.level.menu;

import com.xahlicem.game.Game;
import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.level.Level;
import com.xahlicem.game.level.TimeLevel;
import com.xahlicem.game.level.tile.Tile;

public class InputMenuLevel extends MenuLevel {

	protected char[] string;
	protected int pos = 0;

	public InputMenuLevel(Level level, String options) {
		super(level, options, "Back");
	}

	@Override
	protected void init(String[] options) {
		height = 12;
		tiles = new int[width * height];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				if (y == 4 && x > 1 && x < 17) changeTile(x, y, Tile.DIRT);
				else changeTile(x, y, Tile.GRASS, true);
				changeLight(x, y, 1);
			}
		changeTile(3, 4, Tile.DIRT);
		this.name = options[0];
		this.options = new String[options.length - 1];
		System.arraycopy(options, 1, this.options, 0, this.options.length);

		changeTiles(Tile.WATER);
		string = new char[15];
		char[] ch = new char[15];
		switch (name.toUpperCase()) {
			case "NAME":
				ch = Game.name.toCharArray();
				break;
			case "JOIN GAME":
				ch = Game.lastIp.toCharArray();
				break;
		}
		for (int i = 0; i < ch.length; i++)
			string[i] = ch[i];
	}

	@Override
	protected void drawOtherStuff(Screen screen) {
		screen.drawSprite(32 + pos * 16, 80 + y, Sprite.FONT[30], 0);
		screen.drawString(32, 64 + y, new String(string), Sprite.FONT, 0);
		screen.drawString(64, y, name, Sprite.FONT, 0);
	}

	protected void drawOption(int x, int y, Screen screen) {}

	@Override
	protected void move(Input input) {
		int i = 1;
		if (input.isKeyPressed(Input.KEY_SHIFT)) i = 32;
		if (input.isKeyPressed(Input.KEY_UP)) {
			if (up) {
				changeChar(i);
			}
			up = false;
		} else up = true;
		if (input.isKeyPressed(Input.KEY_DOWN)) {
			if (down) {
				changeChar(-i);
			}
			down = false;
		} else down = true;
		if (input.isKeyPressed(Input.KEY_LEFT)) {
			if (left) {
				changePos(-1);
			}
			left = false;
		} else left = true;
		if (input.isKeyPressed(Input.KEY_RIGHT)) {
			if (right) {
				changePos(1);
			}
			right = false;
		} else right = true;
	}

	private void changeChar(int i) {
		char x = (char) (string[pos] + i);
		if (x >= 0 && x < 126) string[pos] = x;
		
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
	}

	private void changePos(int i) {
		int x = pos + i;
		if (x >= 0 && x < string.length) pos = x;
		
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
	}

	@Override
	protected void parseAction(String action) {
		switch (name.toUpperCase()) {
			case "NAME":
				Game.name = new String(string).trim();
				break;
			case "JOIN GAME":
				Game.lastIp = new String(string).trim();
				game.changeLevel(new TimeLevel(1, 1));
				if (game.getClient() == null) game.startClient(Game.lastIp);
				return;
		}
		super.parseAction(action);
	}
}
