package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import db.DatabaseBuilder;
import db.DatabaseConnector;
import db.DatabaseModel;
import db.SQLiteDatabaseConnector;
import tagger.FileHandler;
import tagger.Tagger;


public class Gui extends JFrame implements Mp3PositionListener {
	private static final String DBNAME = "metatag.db";
	private static final long MICRO = 1000000;
	private DefaultTableModel songsModel;
	private DefaultTableModel playlistModel;
	private JSlider volume;
	private JSlider seekbar;
	private JButton playButton, pauseButton, stopButton, ffButton, rwndButton;
	private AudioPlayer player;
	private JFileChooser chooser;
	private FileHandler handler;
	private int currentSong;
	private DatabaseConnector dbconn;
	private DatabaseModel dbmodel;
	JTable songsTable;


	public Gui() {
		super("Metatagger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		player = new AudioPlayer();
		setUpDatabase();
		handler = new FileHandler(dbconn);

		chooser = new JFileChooser("C:\\Users\\Shayan\\Music\\iTunes\\iTunes Media\\Music\\");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"MP3 Files", "mp3");
		chooser.setFileFilter(filter);

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		//building left side of display
		String[] columnNames = {"Song", "Artist", "Album"};

		songsModel = new DefaultTableModel(columnNames, 0);
		playlistModel = new DefaultTableModel(columnNames, 0);

		songsTable = new JTable(songsModel);
		songsModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				//DO STUFF
			}
		}); //used to update changes in the table in the db

		JScrollPane songList = new JScrollPane(songsTable);//new JTable(songsModel));

		JScrollPane playlist = new JScrollPane(new JTable(playlistModel));



		//top will display list of songs, bottom will be current playlist
		JSplitPane leftSide = new JSplitPane(JSplitPane.VERTICAL_SPLIT, songList, playlist);

		leftSide.setDividerLocation(400);

		//right side will hold playback controls (buttons, sliders, etc)
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.PAGE_AXIS));

		seekbar = new JSlider();
		seekbar.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {seek(seekbar.getValue());}
		});
		rightSide.add(seekbar);

		//creates buttons
		JPanel ctrlButtons = new JPanel();
		ctrlButtons.setLayout(new BoxLayout(ctrlButtons, BoxLayout.LINE_AXIS));
		rwndButton = new JButton("<<");
		rwndButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {rwdPressed();}
		});
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {playPressed();}
		});
		pauseButton = new JButton("||");
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {pausePressed();}
		});
		stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {stopPressed();}
		});
		ffButton = new JButton(">>");
		ffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {ffPressed();}
		});
		ctrlButtons.add(rwndButton);
		ctrlButtons.add(playButton);
		ctrlButtons.add(pauseButton);
		ctrlButtons.add(stopButton);
		ctrlButtons.add(ffButton);
		rightSide.add(ctrlButtons);

		volume = new JSlider();
		volume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {volChange(volume.getValue());}
		});
		rightSide.add(volume);

		//left side of split pane will display song list, right side will provide controls/current info
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
		splitPane.setDividerLocation(700);

		getContentPane().add(splitPane);

		//creating the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		JMenuItem menuItem = new JMenuItem("Add Folder");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chooser.showDialog(null, "Select Folder") == JFileChooser.APPROVE_OPTION) {
					System.out.println(chooser.getCurrentDirectory());
					System.out.println(chooser.getSelectedFile());
					addFiles(chooser.getCurrentDirectory());
				}
			}
		});
		menu.add(menuItem);

		setJMenuBar(menuBar);
		setPreferredSize(new Dimension(1000, 800));
		pack();
		setVisible(true);
	}

	private void playPressed() {
		if (songsTable.getSelectedRow() == -1) return;

		player.loadFile((String) songsModel.getValueAt(songsTable.getSelectedRow(), 0));
		player.play();
	}

	private void ffPressed() {
		System.out.println("fastforward");
	}

	private void rwdPressed() {
		System.out.println("rewind");
	}

	private void pausePressed() {
		player.pause();
	}

	private void stopPressed() {
		player.stop();
	}

	private void seek(int i) {
		System.out.println(i);
	}

	private void volChange(int i) {
		player.setVolume(i);
	}

	private String getNextSong() {
		return null;
	}

	private boolean isMp3(File f) {
		String filename = f.getName();
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		return extension.toLowerCase().equals("mp3");
	}

	@Override
	public void updateSeektime(long pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void songFinished() {
		player.loadFile(getNextSong());
	}

	/**
	 * currently very general code, will need to figure out exactly what needs to be done
	 * it will first use the algorithm to retrieve tag info about the song and then it
	 * will add the song to the database and to the table and probably use some id to
	 * associate the two
	 * 
	 * @param f the song, must be an mp3
	 */
	public void addSong(File f) {
		Object[] row = buildRow(f);
		songsModel.addRow(row);
	}

	private Object[] buildRow(File f) {
		Object[] row = {f.toURI().toString(), "", ""};
		return row;
	}

	/**
	 * adds all the folders and subfolders et al of the given directory recursively
	 * 
	 * @param directory the directory to be iterated through
	 */
	public void addFiles(File directory) {
		ArrayList<File> songs = handler.getMP3s(directory);
		ArrayList<File> missing = handler.getIncomplete(songs);
		/*Thread t = new Thread(new Runnable(ArrayList<File>) {
			public void run() {
				// TODO Auto-generated method stub
				
			}
		});*/
		handler.enterToDatabase(songs);
		handler.identifyAndUpdateSongs(missing);
		handler.enterToDatabase(missing);
	}

	/**
	 * recursively goes through directory tree adding all mp3 files
	 * 
	 * @param f the directory currently being iterated through
	 */
	private void addFilesToTable(ArrayList<DBSong> songs) {
		for (File f: files) {
			if (f.isDirectory()) {
				addFiles(f.listFiles()); //iterates through directories
			}
			else {
				//check if it is an mp3
				if (isMp3(f)) {
					addSong(f);
				}
			}
		}
	}

	private void setUpDatabase() {
		dbconn = new SQLiteDatabaseConnector(DBNAME);
		try {
			dbconn.openDBConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not open Connection");
			System.exit(0);
		}
		
		File f = new File(DBNAME);
		if (!f.exists()) {
			DatabaseBuilder dbbuild = new DatabaseBuilder(dbconn);
			if (!dbbuild.buildDatabase()) {
				System.out.println("Could not bulid db");
				System.exit(0);
			}
		}
		dbmodel = new DatabaseModel(dbconn);
	}
}