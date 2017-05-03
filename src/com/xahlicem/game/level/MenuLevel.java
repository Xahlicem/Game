package com.xahlicem.game.level;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.level.tile.Tile;
import com.xahlicem.game.thing.Thing;

public class MenuLevel extends TimeLevel {
	
	protected static final String[] OPTIONS ={"Back", "Profile", "Host Server", "Join Game", "Volume"};
	protected static final String[] OPTIONS_PROFILE ={"Back", "Name", "Hair", "Clothes"};

	protected Level prevLevel;
	protected String name;
	protected String[] options;
	protected int index = 0;

	public MenuLevel(String name, String... options) {
		super(16, 3 * options.length, BGM.BGM_TITLE);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				if (y % 3 == 0) changeTile(x, y, Tile.DIRT.getColor(), true);
				else changeTile(x, y, Tile.GRASS.getColor(), true);
				changeLight(x, y, 1);
			}
		this.name = name;
		this.options = options;
		y = -32;
	}

	public MenuLevel(Level level, String name, String... options) {
		super(16, 3 * options.length, BGM.BGM_TITLE);
		prevLevel = level;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				if (y % 3 == 0) changeTile(x, y, Tile.DIRT.getColor(), true);
				else changeTile(x, y, Tile.GRASS.getColor(), true);
				changeLight(x, y, 1);
			}
		this.name = name;
		this.options = options;
		y = -32;
	}

	@Override
	public void tick(Input input) {
		super.tick(input);

		if (input.isKeyPressed(Input.KEY_E)) {
			if (enter)parseAction(options[index]);
			enter = false;
		} else enter = true;
	}

	@Override
	protected void move(Input input) {
		int speed = 48;

		if (input.isKeyPressed(Input.KEY_UP)) {
			if (up) y = getY(y - speed);
			up = false;
		} else up = true;
		if (input.isKeyPressed(Input.KEY_DOWN)) {
			if (down) y = getY(y + speed);
			down = false;
		} else down = true;
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
		System.out.println(action);
		switch (action.toUpperCase()) {
			case "START GAME":
				game.changeLevel(TITLE);
				break;
			case "OPTIONS":
				game.changeLevel(new MenuLevel(this, "Options", OPTIONS));
				break;
			case "PROFILE":
				game.changeLevel(new MenuLevel(this, "Profile", OPTIONS_PROFILE));
				break;
			case "LOAD":
			case "LOAD LEVEL":
				game.changeLevel(new EditableLevel(game.getSave()));
				break;
			case "BACK":
			case "RESUME":
				game.changeLevel(prevLevel);
				break;
			case "SAVE":
				prevLevel.save("save");
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

}
