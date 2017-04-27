package com.xahlicem.game.helpers.audio;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

public class BGMPlayer {
	protected boolean mute = false;
	protected long position;
	private BGM currentSound;
	protected Sequencer midiPlayer;

	public BGMPlayer() {
		try {
			midiPlayer = MidiSystem.getSequencer();
			midiPlayer.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		midiPlayer.stop();
		midiPlayer.close();
	}

	public void tick() {
	}

	public void setSound(BGM sound) {
		currentSound = sound;
	}

	public void play(BGM sound, int loops, long start, long end, long pos) {
		if (mute) return;
		if (sound != null) try {
			long length = sound.sound.getTickLength();
			if (end > length) end = length;
			if (start > length) start = length;
			if (pos > length) pos = length;
			
			if (end < length) end = 0;
			if (start < length) start = 0;
			if (pos < length) pos = 0;
			
			midiPlayer.setSequence(sound.sound);
			midiPlayer.setLoopCount(loops);
			midiPlayer.setLoopStartPoint(start);
			midiPlayer.setLoopEndPoint(end);
			midiPlayer.setTickPosition(pos);
			midiPlayer.start();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		play(currentSound, 0, 0, currentSound.sound.getTickLength(), 0);
	}
	
	public void play(BGM sound) {
		play(sound, 0, 0, sound.sound.getTickLength(), 0);
	}

	public boolean isPlaying() {
		return midiPlayer.isRunning();
	}

	public void stop() {
		midiPlayer.stop();
	}

	public void mute() {
		if (mute) return;
		mute = true;
		position = midiPlayer.getTickPosition();
		midiPlayer.stop();
	}

	public void unMute() {
		if (!mute) return;
		mute = false;
		midiPlayer.start();
		midiPlayer.setTickPosition(position);
	}
}
