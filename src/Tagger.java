import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import beatit.BPM;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


public class Tagger {
	private ArrayList<File> music;
	
	public Tagger(String musicFolder){
		try {
			//Path dir = FileSystems.getDefault().getPath(musicFolder);
			music = new ArrayList<File>();
			File dir = new File(musicFolder);
			File[] list = dir.listFiles();
			buildList(list);
			//System.out.println(music.size());
			//DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.mp3");
		    
		} catch (DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
		beginTagging();
	}
	
	public void buildList(File[] files) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	            //System.out.println("Directory: " + file.getName());
	            buildList(file.listFiles()); // Calls same method again.
	        } else {
	            //System.out.println("File: " + file.getName());
	            if(file.getName().contains(".mp3") == true){
	            	//System.out.println(file.getName());
	            	music.add(file);
	            }
	        }
	    }
	}
	
	public void beginTagging(){
		try {
			Mp3File mp3file;
			for(int i=0; i < music.size(); i++){
				System.out.println(music.get(i).getAbsoluteFile().toString());
				mp3file = new Mp3File(music.get(i).getAbsoluteFile().toString());
				System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
				System.out.println("Bitrate: " + mp3file.getLengthInSeconds() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
				System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
				System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
				System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
				System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
				System.out.println("BPM "+BPM.getBPM(music.get(i).getAbsoluteFile().toString()));
			}
		} catch (UnsupportedTagException | InvalidDataException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
