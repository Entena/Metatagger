package gui;



import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;


public class AudioPlayer {
	private Media currentSong;
	private MediaPlayer player;
	private Mp3Listener listener;
	private boolean atEndOfMedia = false, pending = false;

	public AudioPlayer(Mp3Listener l) {
		player = null;
		currentSong = null;
		new JFXPanel(); //required to use javafx media
		listener = l;
	}

	/**
	 * loads the specified file to the player
	 * @param s location of file
	 */
	public void loadFile(String s) {
		if (player != null) 
			player.dispose();
		currentSong = new Media(s);
		player = new MediaPlayer(currentSong);
		player.currentTimeProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable o) {
				listener.updateSeektime(player.getCurrentTime().toMillis()/player.getTotalDuration().toMillis());
			}
		});
		player.setOnPlaying(new Runnable() {
			public void run() {
				listener.playStarted();
			}
		});
		player.setOnPaused(new Runnable() {
			public void run() {
				pause();
			}
		});
		player.setOnEndOfMedia(new Runnable() {
			public void run() {
				listener.songFinished();
			}
		});
		player.setOnReady(new Runnable() {
			public void run() {
				if (pending) {
					playPause();
					pending = false;
				}
			}
		});
		atEndOfMedia = false;
	}

	/**
	 * if the currently loaded file is available then toggles between playing and pausing
	 * @return true if successfully played or paused
	 */
	public boolean playPause() {
		if (player == null) return false;

		Status status = player.getStatus();

		if (status == Status.UNKNOWN  || status == Status.HALTED) {
			// don't do anything in these states
			pending = true;
			return false;
		}

		if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
			// rewind the song if we're sitting at the end
			if (atEndOfMedia) {
				player.seek(player.getStartTime());
				atEndOfMedia = false;
			}
			player.play();
		} 
		else {
			player.pause();
		}
		return true;
	}

	/**
	 * called when the file begins playing
	 */
	public void play() {

	}

	/**
	 * called when the file is paused
	 */
	public void pause() {
		if (currentSong != null) {
			player.pause();
			listener.paused();
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
	public void setVolume(double d) {
		player.setVolume(d);
	}

	public int getCurrentTime() {
		if (player == null) 
			return 0;
		return (int)player.getCurrentTime().toSeconds();
	}

	public int getTotalTime() {
		if (player == null)
			return 0;
		return (int)player.getTotalDuration().toSeconds();
	}
}
