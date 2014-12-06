package gui;
import db.DBSong;

public interface SongEditListener {
	
	void savePressed(DBSong song);
	
	void deletePressed(DBSong song);
}
