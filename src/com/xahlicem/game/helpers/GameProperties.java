package com.xahlicem.game.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.xahlicem.game.Game;

public class GameProperties {
	private static final String FILE = "save/pref.ini";
	
	private static final String NAME = "Player";
	private static final String NAME_KEY = "name";
	private static final String LAST_SAVE = "save";
	private static final String LAST_SAVE_KEY = "last_save";
	private static final String LAST_IP = "localhost";
	private static final String LAST_IP_KEY = "last_ip";
	private static final int VOLUME = 10;
	private static final String VOLUME_KEY = "volume";

	private static Properties prop = new Properties();
	private static File prefs = new File("save/pref.ini");

	public static void load() {
		if (prefs.exists()) {
			try (FileInputStream in = new FileInputStream(FILE)) {
				prop.load(in);
				Game.name = prop.getProperty(NAME_KEY);
				Game.lastSave = prop.getProperty(LAST_SAVE_KEY);
				Game.lastIp = prop.getProperty(LAST_IP_KEY);
				Game.vol = Integer.parseInt(prop.getProperty(VOLUME_KEY));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else setDefaults();
	}

	private static void setDefaults() {
		Game.name = NAME;
		Game.lastSave = LAST_SAVE;
		Game.lastIp = LAST_IP;
		Game.vol = VOLUME;
	}

	public static void save() {
		prop.setProperty(NAME_KEY, Game.name);
		prop.setProperty(LAST_SAVE_KEY, Game.lastSave);
		prop.setProperty(LAST_IP_KEY, Game.lastIp);
		prop.setProperty(VOLUME_KEY, String.valueOf(Game.vol));

		try (FileOutputStream out = new FileOutputStream(FILE)) {
			prop.store(out, "---No Comment---");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
