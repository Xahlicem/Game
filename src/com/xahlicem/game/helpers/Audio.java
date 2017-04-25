package com.xahlicem.game.helpers;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.xahlicem.game.level.Level;

public class Audio {

	private HashMap<String, Clip> clips;
	private int gap;
	private boolean mute = false;
	private int bgmIndex = 0;

	public Audio() {
		clips = new HashMap<String, Clip>();
		gap = 0;
	}
	
	public void tick(Level level) {
		if (level.bgm.length <= 0 || isPlaying(level.bgm[bgmIndex])) return;
		bgmIndex = (++bgmIndex) % level.bgm.length;
		play(level.bgm[bgmIndex]);
	}

	public void load(String name) {
		if (clips.get(name) != null)
			return;
		Clip clip;
		try {
			AudioInputStream ais = AudioSystem
					.getAudioInputStream(Audio.class.getResourceAsStream("/sfx/" + name + ".mp3"));
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
					baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
			clips.put(name, clip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(String... names) {
		for (int i = 0; i < names.length; i++)
			load(names[i]);
	}

	//public void playLoop(String name) {
	//	clips.get(name).loop(Clip.LOOP_CONTINUOUSLY);
	//}

	public void play(String s) {
		play(s, gap);
	}

	public void play(String s, int i) {
		if (mute)
			return;
		Clip c = clips.get(s);
		if (c == null)
			return;
		if (c.isRunning())
			c.stop();
		c.setFramePosition(i);
		while (!c.isRunning())
			c.start();
	}

	public void stop(String s) {
		if (clips.get(s) == null)
			return;
		if (clips.get(s).isRunning())
			clips.get(s).stop();
	}

	public void resume(String s) {
		if (mute)
			return;
		if (clips.get(s).isRunning())
			return;
		clips.get(s).start();
	}

	public void loop(String s) {
		loop(s, gap, gap, clips.get(s).getFrameLength() - 1);
	}

	public void loop(String s, int frame) {
		loop(s, frame, gap, clips.get(s).getFrameLength() - 1);
	}

	public void loop(String s, int start, int end) {
		loop(s, gap, start, end);
	}

	public void loop(String s, int frame, int start, int end) {
		stop(s);
		if (mute)
			return;
		clips.get(s).setLoopPoints(start, end);
		clips.get(s).setFramePosition(frame);
		clips.get(s).loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void setPosition(String s, int frame) {
		clips.get(s).setFramePosition(frame);
	}

	public int getFrames(String s) {
		return clips.get(s).getFrameLength();
	}

	public int getPosition(String s) {
		return clips.get(s).getFramePosition();
	}

	public void close(String s) {
		stop(s);
		clips.get(s).close();
	}
	
	public void closeAll() {
		for (String name : clips.keySet()) {
			stop(name);
			clips.get(name).close();
		}
	}
	
	private boolean isPlaying(String name) {
		return clips.get(name).isRunning();
	}
}
