package gui;

import gui.plugin.LearningPlugin;
import gui.plugin.PluginLoader;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import db.DBSong;
import db.DatabaseBuilder;
import db.DatabaseConnector;
import db.DatabaseModel;
import db.SQLiteDatabaseConnector;
import tagger.FileHandler;


public class Gui extends JFrame implements Mp3Listener {
	private static final String DBNAME = "metatag.db";
	private DefaultTableModel songsModel;
	private DefaultTableModel playlistModel;
	private JSlider volume;
	private JSlider seekbar;
	private JLabel songPosition, currentSongTitle, currentSongArtist;
	private JButton playButton, ffButton, rwndButton;
	private AudioPlayer player;
	private JFileChooser chooser;
	private FileHandler handler;
	private DBSong currentSong;
	private DatabaseConnector dbconn;
	private DatabaseModel dbmodel;
	private JTable songsTable;
	private JProgressBar progressBar;
	private boolean allowSeeking;
	private JLabel progressBarLabel;

	ArrayList<LearningPlugin> loadedPlugins;
	private LearningPlugin currentPlugin;

	public Gui() {
		super("Metatagger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		player = new AudioPlayer(this);
		setUpDatabase();
		handler = new FileHandler(dbconn);

		chooser = new JFileChooser("C:\\Users\\Shayan\\Music\\iTunes\\iTunes Media\\Music\\");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"MP3 Files", "mp3");
		chooser.setFileFilter(filter);

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		//building left side of display
		String[] columnNames = {"ID", "Song", "Artist", "Album"};

		songsModel = new DefaultTableModel(columnNames, 0);
		playlistModel = new DefaultTableModel(columnNames, 0);

		songsTable = new JTable(songsModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		}; //makes the ID column uneditable
		songsModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				//DO STUFF
			}
		}); //used to update changes in the table in the db


		songsTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int id = getSelectedId();
					if (id != -1) {
						//new Thread
						playSong(dbmodel.getSong(id));
					}
				}
			}
		}); //plays a song when double clicked

		JScrollPane songList = new JScrollPane(songsTable);//new JTable(songsModel));

		JScrollPane playlist = new JScrollPane(new JTable(playlistModel));



		//top will display list of songs, bottom will be current playlist
		JSplitPane leftSide = new JSplitPane(JSplitPane.VERTICAL_SPLIT, songList, playlist);

		leftSide.setDividerLocation(400);

		//right side will hold playback controls (buttons, sliders, etc)
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.PAGE_AXIS));

		seekbar = new JSlider(0, 10000, 0);
		seekbar.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				seek(seekbar.getValue());
				int curTime = player.getCurrentTime();
				int totalTime = player.getTotalTime();
				songPosition.setText(String.format("%02d:%02d/%02d:%02d", curTime/60, curTime%60, totalTime/60, totalTime%60));
			}
		});
		rightSide.add(seekbar);

		songPosition = new JLabel("0:00/0:00");
		rightSide.add(songPosition);

		//creates buttons
		JPanel ctrlButtons = new JPanel();
		ctrlButtons.setLayout(new BoxLayout(ctrlButtons, BoxLayout.LINE_AXIS));
		rwndButton = new JButton("<<");
		rwndButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {rwdPressed();}
		});
		playButton = new JButton(">");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {playPressed();}
		});
		ffButton = new JButton(">>");
		ffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {ffPressed();}
		});
		ctrlButtons.add(rwndButton);
		ctrlButtons.add(playButton);
		ctrlButtons.add(ffButton);
		rightSide.add(ctrlButtons);

		volume = new JSlider();
		volume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {updateVolume();}
		});
		rightSide.add(volume);

		JPanel songInfoPanel = new JPanel();
		songInfoPanel.setLayout(new BoxLayout(songInfoPanel, BoxLayout.PAGE_AXIS));
		JLabel curSongLabel = new JLabel("Currently Playing:");
		currentSongTitle = new JLabel("\0");
		currentSongArtist = new JLabel("\0");
		songInfoPanel.add(curSongLabel);
		songInfoPanel.add(currentSongTitle);
		songInfoPanel.add(currentSongArtist);
		rightSide.add(songInfoPanel);

		rightSide.add(new JSeparator());

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		progressBarLabel = new JLabel();
		rightSide.add(progressBarLabel);
		rightSide.add(progressBar);

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
					addFiles(chooser.getSelectedFile());
				}
			}
		});
		menu.add(menuItem);
		
		//creatubg the pluging menu
		JMenu pluginMenu = new JMenu("Plugins");
		menuBar.add(pluginMenu);
		
		JMenuItem loadedPluginsMenu = new JMenuItem("Loaded Plugins");
		loadedPlugins = new ArrayList<LearningPlugin>();
		loadedPluginsMenu.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                LoadedPluginDialog dialog = new LoadedPluginDialog(
                                                  loadedPlugins, currentPlugin);
                dialog.setVisible(true);
                
                if(dialog.getSelected() != currentPlugin){
                    currentPlugin = dialog.getSelected();
                }
            }
        });
		
		pluginMenu.add(loadedPluginsMenu);
		
		JMenuItem loadPlugins = new JMenuItem("Load Plugins");
		loadPlugins.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser jarChooser = new JFileChooser(".");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                 "Jar Plugins", "jar");
                jarChooser.setFileFilter(filter);

                jarChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                
                if (jarChooser.showDialog(null, "Select Plugin") == JFileChooser.APPROVE_OPTION) {
                    PluginLoader loader = new PluginLoader();
                    try {
                        loadedPlugins.addAll(
                                loader.loadPlugin(
                                        jarChooser.getSelectedFile()
                                                           .getAbsolutePath()));
                    } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
		pluginMenu.add(loadPlugins);

		setJMenuBar(menuBar);
		setPreferredSize(new Dimension(1000, 800));
		pack();
		setVisible(true);
		allowSeeking = true;
		loadInitial();
	}

	/**
	 * returns the id of the selected song in the table
	 * @return the id of the selected song, -1 if no song is selected
	 */
	public int getSelectedId() {
		if (songsTable.getSelectedRow() == -1) return -1;
		return Integer.parseInt((String) songsModel.getValueAt(songsTable.getSelectedRow(), 0));
	}

	/**
	 * called when the play/pause button is pressed
	 */
	private void playPressed() {
		player.playPause();
	}

	/**
	 * 
	 */
	private void playSong(DBSong song) {
		if (song == null) return;

		File f = new File(song.getFilepath());
		player.loadFile(f.toURI().toString());
		currentSong = song;
		player.playPause();
		updateVolume();
		setSongInfoLabel(song);
	}

	private void setSongInfoLabel(DBSong song ) {
		currentSongTitle.setText(song.getName());
		currentSongArtist.setText(song.getArtist() + " - " + song.getAlbum());
	}

	private void ffPressed() {
		if (currentSong == null) return;

		int nextId = (currentSong.getSongId() % songsModel.getRowCount()) + 1;
		playSong(dbmodel.getSong(nextId));
	}

	private void rwdPressed() {
		if (currentSong == null) return;

		if (player.getCurrentTime() >= 5) //rewinds song if more than 5 seconds played
			player.seek(0);
		else { //otherwise plays previous song
			int prevId = (currentSong.getSongId() + songsModel.getRowCount() - 2) % songsModel.getRowCount() + 1;
			playSong(dbmodel.getSong(prevId));
		}
	}

	private void seek(int i) {
		if (allowSeeking)
			player.seek(seekbar.getValue()/(double)seekbar.getMaximum());
	}

	public void updateVolume() {
		double val = volume.getValue()/(double)volume.getMaximum();
		if (currentSong != null)
			player.setVolume(val);
	}

	/**
	 * adds the given song to the songsTable
	 * 
	 * @param song DBSong object returned by database
	 */
	public void addSong(DBSong song) {
		Object[] row = buildRow(song);
		songsModel.addRow(row);
	}

	/**
	 * builds the row out of the given song
	 * 
	 * @param song DBSong object returned by database
	 * @return returns row to be inserted into table
	 */
	private Object[] buildRow(DBSong song) {
		Object[] row = {Integer.toString(song.getSongId()), song.getName(), song.getArtist(), song.getAlbum()};
		return row;
	}

	/**
	 * Sends the given directory to the tagger and adds them to the DB and 
	 * 
	 * @param directory the directory to be iterated through
	 */
	public void addFiles(final File directory) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				progressBar.setVisible(true);
				progressBarLabel.setText("Analyzing Songs...");
				ArrayList<File> songs = handler.getMP3s(directory);
				ArrayList<File> missing = handler.getIncomplete(songs);
				progressBarLabel.setText("Adding Tagged Songs...");
				addFilesToTable(handler.enterAndReturnIDs(songs));
				progressBarLabel.setText("Getting Missing Info...");
				handler.identifyAndUpdateSongs(missing);
				progressBarLabel.setText("Adding Songs with Missing Info...");
				addFilesToTable(handler.enterAndReturnIDs(missing));
				progressBar.setVisible(false);
				progressBarLabel.setText("");
			}
		});
		t.start();
	}

	/**
	 * recursively goes through directory tree adding all mp3 files
	 * 
	 * @param f the directory currently being iterated through
	 */
	private void addFilesToTable(ArrayList<DBSong> songs) {
		for (DBSong song: songs) {
			addSong(song);
		}
	}

	private void setUpDatabase() {
		File f = new File(DBNAME);
		boolean firstRun = !f.exists();

		dbconn = new SQLiteDatabaseConnector(DBNAME);
		try {
			dbconn.openDBConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not open Connection");
			System.exit(0);
		}


		if (firstRun) {
			DatabaseBuilder dbbuild = new DatabaseBuilder(dbconn);
			if (!dbbuild.buildDatabase()) {
				System.out.println("Could not bulid db");
				System.exit(0);
			}
		}
		dbmodel = new DatabaseModel(dbconn);
	}

	private void loadInitial() {
		ArrayList<Integer> ids = dbmodel.getAllSongIds();

		for (Integer id: ids) {
			DBSong song = dbmodel.getSong(id);
			addSong(song);
		}
	}

	public void updateSeektime(double pos) {
		allowSeeking = false;
		seekbar.setValue((int) (pos*seekbar.getMaximum()));
		allowSeeking = true;
	}

	public void songFinished() {
		ffPressed();
	}

	public void playStarted() {
		playButton.setText("||");
	}

	public void paused() {
		playButton.setText(">");
	}
}
