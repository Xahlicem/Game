package com.xahlicem.game;

import java.io.File;

import javax.swing.JOptionPane;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.audio.Volume;
import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;
import com.xahlicem.game.helpers.net.packet.PacketLevelReq;
import com.xahlicem.game.helpers.net.packet.PacketLogin;
import com.xahlicem.game.level.Level;
import com.xahlicem.game.level.MenuLevel;
import com.xahlicem.game.level.tile.RandomAnimatedTile;
import com.xahlicem.game.level.tile.Tile;

public class Game implements Runnable {
	private static final double TPS = 60D;
	private static final double NSPT = 1_000_000_000D / TPS;

	public static final String TITLE = "Game";

	private Thread thread;
	private Frame frame;
	private boolean running;
	private Input input;
	private BGMPlayer bgm;
	private SFXPlayer sfx;
	private Volume volume;
	private Screen screen;
	private Level level;
	private File save;

	private Client client;
	private Server server;

	public Game() {

		screen = new Screen();
		input = new Input();
		frame = new Frame(screen, input);

		bgm = new BGMPlayer();
		sfx = new SFXPlayer();
		volume = new Volume();
	}

	public static void main(String[] args) {
		new File("save/").mkdir();
		Game game = new Game();
		game.frame.init();
		game.save = new File("save/SAVE.PNG");

		game.start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(), now = 0;
		long timer = System.currentTimeMillis();
		double delta = 0;
		int fps = 0, tps = 0;
		boolean draw = false;
		volume.set(0.05);

		// if (save.exists()) changeLevel(new Level(save));
		//changeLevel(new MenuLevel());
		changeLevel(level.TITLE);

		while (running) {
			now = System.nanoTime();
			delta += (double) (now - lastTime) / NSPT;
			lastTime = now;

			if (delta < 1) try {
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			while (delta >= 1) {
				tick();
				tps++;
				draw = true;
				delta--;
			}

			if (draw) {
				draw();
				fps++;
				// draw = false;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(TITLE + "   |   TPS:" + tps + ", FPS:" + fps);
				tps = 0;
				fps = 0;
			}
		}
		sfx.close();
		bgm.close();

	}

	public synchronized void start() {
		running = true;

		String name = JOptionPane.showInputDialog("Please enter name");
		String ip = "10.1.10.2";
		if (JOptionPane.showConfirmDialog(frame, "Do you want to host?") == 0) {
			server = new Server(this);
			server.start();
		} // else ip = JOptionPane.showInputDialog("Please enter server IP");
		client = new Client(this, ip);
		client.start();

		new PacketLogin(name).writeData(client);

		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void tick() {
		input.tick();
		sfx.tick();
		level.tick();
		int i = 2;

		if (input.isKeyPressed(Input.KEY_SHIFT)) i = 4;

		if (input.isKeyPressed(Input.KEY_UP)) y -= i;
		if (input.isKeyPressed(Input.KEY_DOWN)) y += i;
		if (input.isKeyPressed(Input.KEY_LEFT)) x -= i;
		if (input.isKeyPressed(Input.KEY_RIGHT)) x += i;
		edit = input.isKeyPressed(Input.KEY_E);

		int pointX = input.getPoint()[0];
		int pointY = input.getPoint()[1];

		boolean corner = (pointX < 74 && pointY < 74);
		if (corner) volume.set(volume.get() + .05 * input.getWheel());
		else if (input.getWheel() != 0) {
			if (level.lighted()) color = (Tile.getTileIndexLength() * 2 + color + input.getWheel()) % Tile.getTileIndexLength();
			else color = (Level.MAX_BRIGHTNESS * 2 + color + input.getWheel()) % Level.MAX_BRIGHTNESS;
		}

		pointX = (pointX / Screen.SCALE) + x >> 4;
		pointY = (pointY / Screen.SCALE) + y >> 4;
		xPos = level.getX(pointX);
		yPos = level.getY(pointY);

		if (edit && input.isKeyPressed(Input.KEY_PRESS)) {
			if (xPos != lastX || yPos != lastY) {
				if (corner) {
					level.toggleLight();
					color = 0;
				} else {
					if (level.lighted()) {
						level.changeTile(xPos, yPos, Tile.getTileFromIndex(color).getColor(), input.isKeyPressed(Input.KEY_SHIFT));
					} else {
						level.changeLight(xPos, yPos, color);
					}
				}

				level.getPacket().writeData(client);
				lastX = level.getX(pointX);
				lastY = level.getY(pointY);
			}
		}

		if (!click && input.isKeyPressed(Input.KEY_PRESS)) {
			click = true;
		}
		if (click && !input.isKeyPressed(Input.KEY_PRESS)) {
			click = false;
			lastX = -1;
			lastY = -1;
		}

		if (input.isKeyPressed(Input.KEY_ESC)) {
			level.save("save");
		}
	}

	boolean click = false;
	public static boolean edit = true;
	int color = 0;
	int x = 0, y = 0;
	int xPos, yPos;
	int lastX = -1, lastY = -1;

	private void draw() {
		level.draw(x, y, screen);
		if (edit) {
			screen.drawSprite(x, y, Sprite.CONTAINER, 8);
			String s = String.valueOf(color);
			if (level.lighted()) {
				Tile.getTileFromIndex(color).draw(x + 2, y + 2, screen);
				if (Tile.getTileFromIndex(color).getClass().equals(RandomAnimatedTile.class)) s += "A";
			} else {
				level.getTile(xPos, yPos).draw(x + 2, y + 2, screen, color);
			}
			screen.drawString(x + 3, y + 12, s, Sprite.FONT_TINY, (level.lighted()) ? 0xFF000000 : 0xFFFFFFFF);

		}
		screen.draw();
	}

	private void changeLevel(Level level) {
		this.level = level;
		level.init(bgm, sfx);
		if (server == null) new PacketLevelReq().writeData(client);
	}

	public Level getLevel() {
		return level;
	}
}
