package gui;



import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class AudioPlayer {
	private Media currentSong, bufferedSong;
	private MediaPlayer player, bufferPlayer;

	public AudioPlayer() {
		player = null;
		currentSong = null;
		new JFXPanel(); //required to use javafx media
	}

	/**
	 * loads the specified file to the player
	 * @param s location of file
	 */
	public void loadFile(String s) {
		System.out.println(s);
		currentSong = new Media(s);
		player = new MediaPlayer(currentSong);
	}

	/**
	 * if there is a currently selected file, play it
	 */
	public void play() {
		if (currentSong != null) {
			player.play();
		}
	}

	/**
	 * if there is a currently selected file, pause it
	 */
	public void pause() {
		if (currentSong != null) {
			player.pause();
		}
	}

	/**
	 * if there is a currently selected file, stop it
	 */
	public void stop() {
		if (currentSong != null) {
			player.stop();
		}
	}

	/**
	 * seeks to a position in the current file
	 * @param t part of song to skip to expressed as a decimal between 0 and 1
	 */
	public void seek(double t) {
		if (currentSong != null) {
			player.seek(currentSong.getDuration().multiply(t));
		}
	}

	/**
	 * sets the volume of the player
	 */
	public void setVolume(int i) {
		player.setVolume(((double) i)/100.);
	}

	/**
	 * Creates a MediaPlayer object for a buffered song 
	 */
	public void bufferSong(String s) {
		bufferedSong = new Media(s);
		bufferPlayer = new MediaPlayer(bufferedSong);
	}

	public boolean loadBuffer() {
		if (bufferedSong == null || bufferedSong == null) return false;
		
		currentSong = bufferedSong;
		player = bufferPlayer;
		bufferedSong = null;
		bufferPlayer = null;
		return true;
	}
}
