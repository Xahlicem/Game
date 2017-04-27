package com.xahlicem.game.helpers.audio;

import java.util.HashMap;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class SFXPlayer {

	private static final int CHANNELS_START = 10;
	private static final int MAX_CHANNELS = 16;
	private HashMap<MidiChannel, Integer> ticks = new HashMap<MidiChannel, Integer>();
	private MidiChannel[] channels;
	private int channelIndex = CHANNELS_START - 1;
	private Synthesizer synth;

	public SFXPlayer() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
		    channels = synth.getChannels();
		    synth.loadAllInstruments(synth.getDefaultSoundbank());
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void tick() {
		for (MidiChannel channel : ticks.keySet()) {
			ticks.put(channel, ticks.get(channel)-1);
			if (ticks.get(channel) == 0) channel.allNotesOff();
		}
	}
	
	public void sound(int sound, int time) {
	    // Check for null; maybe not all 16 channels exist.
		channelIndex++;
		if (channelIndex >= MAX_CHANNELS) channelIndex = CHANNELS_START;
	    if (channels[channelIndex] != null) {
	    	ticks.put(channels[channelIndex], time);
	    	channels[channelIndex].programChange(sound);
	        channels[channelIndex].noteOn(60, 93); 
	    }
	}

	public void mute() {
	}

	public void unMute() {
	}
	
	public void close() {
		synth.close();
	}
}
