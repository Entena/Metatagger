import java.io.File;
import java.util.ArrayList;

import db.DatabaseBuilder;
import db.DatabaseConnector;
import db.SQLiteDatabaseConnector;
import tagger.FileHandler;
import tagger.Tagger;
import gui.Gui;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count = 0;
		if(args.length < 1){
			System.exit(-1);
		}
		while(count < args.length){
			switch(args[count]){
				case "-h":
					System.out.println("Metatagger");
					System.out.println("-g runs gui mode");
					System.out.println("-t initates tagger mode");
					count++;
				break;				
				case "-t":
					String fileName = args[count+1];
					DatabaseConnector dbconnect = new SQLiteDatabaseConnector("dbtest.db");
					try{
						dbconnect.openDBConnection();
						DatabaseBuilder dbBuilder = new DatabaseBuilder(dbconnect);
						dbBuilder.buildDatabase();
						FileHandler handler = new FileHandler(dbconnect);
						File dir = new File(fileName);
						System.out.println(dir.toString());
						ArrayList<File> mp3s = handler.getMP3s(dir);
						System.out.println("Got "+mp3s.size()+" mp3s");
						ArrayList<File> missing = handler.getIncomplete(mp3s);
						System.out.println("Songs missing info: "+missing.size()+" Songs not missing "+mp3s.size());
						handler.identifyAndUpdateSongs(missing);
					}catch(Exception e){
						e.printStackTrace();
					}
					count = count+2;
				break;
				case "-g":
					new Gui();
				break;
				default://If none of your cases match then this is an unrecognized parameter and we will exit.
					System.out.println("Unrecognized parameter "+args[count]+"\nExiting.");
					System.exit(-1);					
				break;
			}
		}
	}

}
