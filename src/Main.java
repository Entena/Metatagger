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
					Tagger t = new Tagger(fileName);
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
