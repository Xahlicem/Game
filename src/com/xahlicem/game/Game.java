package com.xahlicem.game;

import java.io.File;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.helpers.GameProperties;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.audio.Volume;
import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;
import com.xahlicem.game.helpers.net.packet.PacketLogin;
import com.xahlicem.game.level.Level;
import com.xahlicem.game.level.menu.MenuLevel;

public class Game implements Runnable {
	private static final double TPS = 60D;
	private static final double NSPT = 1_000_000_000D / TPS;

	public static final String TITLE = "Game";

	private static Volume volume = new Volume();
	public static boolean running;
	public static String name;
	public static String lastSave;
	public static String lastIp;
	public static int vol;

	private Thread thread;
	private Frame frame;
	private Input input;
	private BGMPlayer bgm;
	private SFXPlayer sfx;
	private Screen screen;
	private Level level;
	private File save;

	private Client client;
	private Server server;

	public Game() {

		screen = new Screen();
		input = new Input();
		frame = new Frame(this, screen, input);

		bgm = new BGMPlayer();
		sfx = new SFXPlayer();
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.init();

		game.start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(), now = 0;
		long timer = System.currentTimeMillis();
		double delta = 0;
		int fps = 0, tps = 0;
		boolean draw = false;
		init();

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
				draw = false;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(TITLE + "   |   TPS:" + tps + ", FPS:" + fps);
				tps = 0;
				fps = 0;
			}
		}

	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		close();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		new File("save/").mkdir();
		
		GameProperties.load();
		volume.set(vol);

		changeLevel(MenuLevel.MAIN_MENU);
		save = new File("save/SAVE.PNG");
	}

	private void close() {
		if (server != null) server.close(); // TODO
		if (client != null) client.close(); // TODO
		if (sfx != null) sfx.close();
		if (bgm != null) bgm.close();
		frame.exit();
		GameProperties.save();
		System.out.println("Exit");
		System.exit(0);
	}

	private void tick() {
		input.tick();
		sfx.tick();
		level.tick(input);
	}

	private void draw() {
		screen.clear();
		level.draw(screen);
		screen.draw();
	}

	public void changeLevel(Level level) {
		if (!(this.level instanceof MenuLevel && level instanceof MenuLevel)) bgm.stop();
		this.level = level;
		level.init(this, bgm, sfx);
	}

	public File getSave() {
		return save;
	}

	public Level getLevel() {
		return level;
	}

	public Input getInput() {
		return input;
	}

	public void startServer() {
		System.out.println("Started server!");
		server = new Server(this);
		server.start();
		startClient("localhost");
	}

	public boolean hosting() {
		return server != null;
	}

	public void startClient(String ip) {
		client = new Client(this, ip);
		client.start();
		new PacketLogin(name).writeData(client);
	}

	public Client getClient() {
		return client;
	}
	
	public static void setVolume() {
		volume.set(vol);
	}
}
