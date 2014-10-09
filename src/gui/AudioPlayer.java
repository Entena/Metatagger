package gui;

import maryb.player.*;


public class AudioPlayer implements PlayerEventListener {
	private String currentFile;
	private Player player;

	public AudioPlayer() {
		player = new Player();
		currentFile = null;
	}

	/**
	 * loads the specified file to the player
	 * @param s location of file
	 */
	public void loadFile(String s) {
		currentFile = s;
		player.setSourceLocation(currentFile);
	}

	/**
	 * if there is a currently selected file, play it
	 */
	public void play() {
		if (currentFile != null) {
			player.play();
		}
	}

	/**
	 * if there is a currently selected file, pause it
	 */
	public void pause() {
		if (currentFile != null) {
			player.pause();
		}
	}

	/**
	 * if there is a currently selected file, stop it
	 */
	public void stop() {
		if (currentFile != null) {
			player.stop();
		}
	}

	/**
	 * seeks to a position in the current file
	 * @param i time in microseconds
	 */
	public void seek(int i) {
		if (currentFile != null) {
			player.seek(i);
		}
	}

	/**
	 * sets the volume of the player
	 */
	public void setVolume(int i) {
		player.setCurrentVolume(((float) i)/100f);
		System.out.println(player.getCurrentVolume());
	}

	/**
	 * not used
	 */
	public void buffer() {}

	/**
	 * called when the current song has finished playing
	 * not called on stop or pause
	 */
	public void endOfMedia() {
		// TODO Auto-generated method stub

	}

	/**
	 * called when state of song has changed
	 */
	public void stateChanged() {
		// TODO Auto-generated method stub

	}
}
