package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import db.DBSong;
import db.DatabaseModel;

public class SongEditPanel extends JPanel {
	private DBSong song;
	private DatabaseModel model;
	private JLabel idLabel, titleLabel, artistLabel, albumLabel, bpmLabel;
	private JTextField id;
	private JTextField title;
	private JTextField artist;
	private JTextField album;
	private JTextField bpm;
	private JButton save, delete;
	private SongEditListener listener;
	
	private final int COLS = 15; //the number of columns in the textfield
	
	public SongEditPanel(SongEditListener l) {
		super();
		listener = l;
		
		id = new JTextField(COLS);
		title = new JTextField(COLS);
		artist = new JTextField(COLS);
		album = new JTextField(COLS);
		bpm = new JTextField(COLS);

		id.setEditable(false);
		bpm.setEditable(false);
		idLabel = new JLabel(    "Song ID:     ");
		titleLabel = new JLabel( "Track Name:  ");
		artistLabel = new JLabel("Artist:      ");
		albumLabel = new JLabel( "Album:       ");
		bpmLabel = new JLabel(   "BPM:         ");
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(createRowPanel(idLabel, id));
		this.add(createRowPanel(titleLabel, title));
		this.add(createRowPanel(artistLabel, artist));
		this.add(createRowPanel(albumLabel, album));
		this.add(createRowPanel(bpmLabel, bpm));

		save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		
		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete();
			}
		});
		
		JPanel saveDeletePanel = new JPanel();
		saveDeletePanel.setLayout(new BoxLayout(saveDeletePanel, BoxLayout.LINE_AXIS));
		saveDeletePanel.add(save);
		saveDeletePanel.add(delete);
		
		this.add(saveDeletePanel);
		//setSong(song);
	}
	
	private void save() {
		if (song == null) return;
		
		if (title.getText() != song.getName())
			song.setName(title.getText());
		if (artist.getText() != song.getArtist())
			song.setArtist(artist.getText());
		if (album.getText() != song.getAlbum())
			song.setAlbum(album.getText());
		
		listener.savePressed(song);
	}
	
	private void delete() {
		listener.deletePressed(song);
	}

	private JPanel createRowPanel(JComponent l, JComponent t) {
		JPanel p = new JPanel();
		p.add(l);
		p.add(t);
		return p;
	}
	
	private void setTextField(String s, boolean flagged, JTextField t) {
		t.setText(s);
		if (!flagged) {
			t.setBackground(Color.red);
		}
		else {
			t.setBackground(Color.white);
		}
	}
	
	public void setSong(DBSong s) {
		song = s;
		
		if (song == null) {
			setTextField("No song", false, id);
			setTextField("", false, title);
			setTextField("", false, artist);
			setTextField("", false, album);
			setTextField("N/A", false, bpm);
		}
		else {
			setTextField(Integer.toString(song.getSongId()), true, id);
			setTextField(song.getName(), song.isNameValid(), title);
			setTextField(song.getArtist(), song.isArtistValid(), artist);
			setTextField(song.getAlbum(), song.isAlbumValid(), album);
			setTextField(Integer.toString(song.getBPM()), song.isBPMValid(), bpm);
		}
	}
}
