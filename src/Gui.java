import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Gui extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultTableModel songsModel;
	private DefaultTableModel playlistModel;
	private JSlider volume;
	private JSlider seekbar;
	private JButton playButton, pauseButton, stopButton, ffButton, rwndButton;
	
	
	public Gui() {
		super("Metatagger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//building left side of display
		String[] columnNames = {"Song", "Artist", "Album"};
		
		songsModel = new DefaultTableModel(columnNames, 0);
		playlistModel = new DefaultTableModel(columnNames, 0);
		
		JScrollPane songList = new JScrollPane(new JTable(songsModel));
		JScrollPane playlist = new JScrollPane(new JTable(playlistModel));
		
		
		
		//top will display list of songs, bottom will be current playlist
		JSplitPane leftSide = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, songList, playlist);
		
		//right side will hold playback controls (buttons, sliders, etc)
		JPanel rightSide = new JPanel();
		rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.PAGE_AXIS));
		
		seekbar = new JSlider();
		rightSide.add(seekbar);
		
		//creates buttons
		JPanel ctrlButtons = new JPanel();
		ctrlButtons.setLayout(new BoxLayout(ctrlButtons, BoxLayout.LINE_AXIS));
		rwndButton = new JButton("<<");
		playButton = new JButton("Play");
		pauseButton = new JButton("||");
		stopButton = new JButton("Stop");
		ffButton = new JButton(">>");
		ctrlButtons.add(rwndButton);
		ctrlButtons.add(playButton);
		ctrlButtons.add(pauseButton);
		ctrlButtons.add(stopButton);
		ctrlButtons.add(ffButton);
		
		rightSide.add(ctrlButtons);
		
		volume = new JSlider();
		rightSide.add(volume);
		
		//left side of split pane will display song list, right side will provide controls/current info
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftSide, rightSide);
		getContentPane().add(splitPane);
		pack();
		setVisible(true);
	}
}
