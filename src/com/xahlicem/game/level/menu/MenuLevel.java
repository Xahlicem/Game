package com.xahlicem.game.level.menu;

import com.xahlicem.game.Game;
import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.level.EditableLevel;
import com.xahlicem.game.level.Level;
import com.xahlicem.game.level.TimeLevel;
import com.xahlicem.game.level.tile.Tile;
import com.xahlicem.game.thing.Thing;

public class MenuLevel extends TimeLevel {

	protected static final String[] MAIN = { Game.TITLE, "Start Game", "Load", "Join Game", "Options", "Quit" };
	protected static final String[] OPTIONS = { "Options", "Back", "Profile", "Volume", "Etc" };
	protected static final String[] OPTIONS_PROFILE = { "Profile", "Back", "Name", "Hair", "Clothes" };
	protected static final String[] PAUSED = { "Paused", "Resume", "Host Server", "Options", "Save", "Load", "Exit" };

	public static final Level MAIN_MENU = new MenuLevel(MAIN);

	protected Level prevLevel;
	protected String name;
	protected String[] options;
	protected int index = 0;

	public MenuLevel(String... options) {
		super(20, 3 * (options.length - 1), BGM.BGM_TITLE);
		init(options);
	}

	public MenuLevel(Level level, String... options) {
		super(20, 3 * (options.length - 1), BGM.BGM_TITLE);
		prevLevel = level;
		init(options);
	}

	protected void init(String[] options) {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				if (y % 3 == 0 && x > 1 && x < 17) changeTile(x, y, Tile.DIRT.getColor(), true);
				else changeTile(x, y, Tile.GRASS.getColor(), true);
				changeLight(x, y, 1);
			}
		this.name = options[0];
		this.options = new String[options.length - 1];
		System.arraycopy(options, 1, this.options, 0, this.options.length);
		y = -32;
		changeTiles(Tile.WATER);
	}

	@Override
	public void tick(Input input) {
		super.tick(input);

		if (input.isKeyPressed(Input.KEY_E)) {
			if (enter) parseAction(options[index]);
			enter = false;
		} else enter = true;
	}

	@Override
	protected void move(Input input) {
		int speed = 48;

		if (input.isKeyPressed(Input.KEY_UP)) {
			if (up) {
				changeTiles(Tile.GRASS);
				y = getY(y - speed);
				changeTiles(Tile.WATER);
			}
			up = false;
		} else up = true;
		if (input.isKeyPressed(Input.KEY_DOWN)) {
			if (down) {
				changeTiles(Tile.GRASS);
				y = getY(y + speed);
				changeTiles(Tile.WATER);
			}
			down = false;
		} else down = true;
	}

	protected void changeTiles(Tile tile) {
		for (int i = 0; i < name.length(); i++)
			changeTile(4 + i, y >> 4, tile.getBaseColor(), true);
	}

	public void draw(Screen screen) {
		screen.setOffset(x, y);
		for (int yPos = y >> 4; yPos <= (y + Screen.HEIGHT + 32) >> 4; yPos++)
			for (int xPos = x >> 4; xPos <= (x + Screen.WIDTH) >> 4; xPos++) {
				int i = getTilePos(xPos, yPos);
				int x2 = xPos << 4;
				int y2 = yPos << 4;

				int[] lights = getLights(xPos, yPos);

				getTile(i).draw(x2, y2, screen, lights);
				drawEdges(xPos, yPos, x2, y2, screen, lights);
				if (getTileX(xPos) == 15 && getTileY(yPos) % 3 == 0) screen.drawString(getX(x2 - 192), y2, options[index = getTileY(yPos) / 3], Sprite.FONT, 0);
			}
		setIndex();
		for (Thing t : things)
			t.draw(screen);

		screen.drawSprite(32, 32 + y, Sprite.FONT[16], 0);
		screen.drawString(64, y, name, Sprite.FONT, 0);
	}

	private void setIndex() {
		index -= 3;
		if (index < options.length && index > 0) return;
		index %= options.length;
		if (index < 0) index += options.length;
	}

	protected void parseAction(String action) {
		switch (action.toUpperCase()) {
			case "START GAME":
				game.changeLevel(TITLE);
				break;
			case "OPTIONS":
				game.changeLevel(new MenuLevel(this, OPTIONS));
				break;
			case "PROFILE":
				game.changeLevel(new MenuLevel(this, OPTIONS_PROFILE));
				break;
			case "VOLUME":
				game.changeLevel(new SliderMenuLevel(this, Game.vol, 10));
				break;
			case "BACK":
			case "RESUME":
				game.changeLevel(prevLevel);
				break;
			case "SAVE":
				prevLevel.save("save");
				break;
			case "LOAD":
			case "LOAD LEVEL":
				game.changeLevel(new EditableLevel(game.getSave()));
				break;
			case "NAME":
			case "JOIN GAME":
				game.changeLevel(new InputMenuLevel(this, action));
				break;
			case "HOST SERVER":
				game.changeLevel(prevLevel);
				if (!game.hosting()) game.startServer();
				break;
			case "EXIT":
				game.changeLevel(MAIN_MENU);
				break;
			case "QUIT":
				game.stop();
				break;
			default:
				break;
		}

		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
		if (sfx != null) sfx.sound(127, 1);
	}

	@Override
	protected void menu() {
		boolean back = false;
		for (String s : options) {
			if (s.equalsIgnoreCase("BACK")) back = true;
			if (s.equalsIgnoreCase("RESUME")) back = true;
		}
		if (back) parseAction("BACK");
		else parseAction("QUIT");
	}

	public static MenuLevel getPauseMenu(Level level) {
		return new MenuLevel(level, PAUSED);
	}

}
