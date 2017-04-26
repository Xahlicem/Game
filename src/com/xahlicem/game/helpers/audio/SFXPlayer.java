package com.xahlicem.game.helpers.audio;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

public class SFXPlayer extends AudioPlayer {

	private static final int MAX_CHANNELS = 8;
	private static final int MAX_CHANNELS_MASK = MAX_CHANNELS - 1;
	private Sequencer[] channels;
	private long[] positions;
	private int channelIndex = 0;

	public SFXPlayer() {
		try {
			channels = new Sequencer[MAX_CHANNELS];
			positions = new long[MAX_CHANNELS];
			for (int i = 0; i < channels.length; i++) {
				channels[i] = MidiSystem.getSequencer();
				channels[i].open();
			}
			midiPlayer = channels[channelIndex];
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		System.out.println("Closing");
		for (int i = 0; i < channels.length; i++) {
			channels[i].stop();
			channels[i].close();
		}
		System.out.println("Closed");
	}

	public void play(Sound sound, int loops, long start, long end, long pos) {
		channelIndex = ++channelIndex & MAX_CHANNELS_MASK;
		midiPlayer = channels[channelIndex];
		super.play(sound, loops, start, end, pos);
	}

	public boolean isPlaying() {
		for (int i = 0; i < channels.length; i++) {
			if (channels[i].isRunning()) return true;
		}
		return false;
	}

	public void stop() {
		for (int i = 0; i < channels.length; i++)
			channels[i].stop();
	}

	public void mute() {
		if (mute) return;
		mute = true;
		for (int i = 0; i < channels.length; i++) {
			positions[i] = channels[i].getTickPosition();
			channels[i].stop();
		}
	}

	public void unMute() {
		if (!mute) return;
		mute = false;
		for (int i = 0; i < channels.length; i++) {
			channels[i].start();
			channels[i].setTickPosition(positions[i]);
		}
	}
}
