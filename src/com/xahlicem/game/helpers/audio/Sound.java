package com.xahlicem.game.helpers.audio;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class Sound {
	public Sequence sound;
	private String path;

	public static final Sound BGM_TITLE = new Sound("BGM_TITLE");
	public static final Sound SFX = new Sound("SFX", 1);

	public Sound(String name) {
		path = "/sfx/" + name + ".mid";
		try {
			sound = MidiSystem.getSequence(AudioPlayer.class.getResourceAsStream(path));
		} catch (InvalidMidiDataException | IOException e) {
			sound = null;
			e.printStackTrace();
		}
	}

	public Sound(String name, int track) {
		path = "/sfx/" + name + ".mid";
		track += 1;
		try {
			sound = MidiSystem.getSequence(AudioPlayer.class.getResourceAsStream(path));
			Track[] tracks = sound.getTracks();
			for (int i = 1; i < tracks.length; i++)
				if (i != track) sound.deleteTrack(tracks[i]);
		} catch (InvalidMidiDataException | IOException e) {
			sound = null;
			e.printStackTrace();
		}
	}
}
