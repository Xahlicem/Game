package com.xahlicem.game;

import java.io.File;

import javax.swing.JOptionPane;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.audio.Volume;
import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;
import com.xahlicem.game.helpers.net.packet.PacketLevelReq;
import com.xahlicem.game.helpers.net.packet.PacketLogin;
import com.xahlicem.game.level.Level;

public class Game implements Runnable {
	private static final double TPS = 60D;
	private static final double NSPT = 1_000_000_000D / TPS;

	public static final String TITLE = "Game";
	public static final Volume volume = new Volume();

	public static boolean running;
	
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
		frame = new Frame(screen, input);

		bgm = new BGMPlayer();
		sfx = new SFXPlayer();
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

		//if (save.exists()) changeLevel(new EditableLevel(save));
		//else changeLevel(Level.TITLE);
		changeLevel(Level.MAIN_MENU);

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
		close();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void close() {
		if (server != null) server.close();
		if (client != null) client.close();
		if (sfx != null) sfx.close();
		if (bgm != null) bgm.close();
		frame.dispose();
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
		bgm.stop();
		this.level = level;
		level.init(this, bgm, sfx);
		if (server == null) new PacketLevelReq().writeData(client);
	}
	
	public File getSave() {
		return save;
	}

	public Level getLevel() {
		return level;
	}
	
	public Client getClient() {
		return client;
	}
	
	public Input getInput() {
		return input;
	}
}
