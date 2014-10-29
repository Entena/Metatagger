package tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class PrinterGrabber {
	private String cmd;
	
	public PrinterGrabber(){
		if(System.getProperty("os.name").contains("Windows")){
			//TODO add in windows binary in repo
			cmd = "cmds"+System.getProperty("file.separator")+"echoprint.exe";
		} else {
			if(System.getProperty("os.arch").equals("amd64")){
				cmd = "cmds"+System.getProperty("file.separator")+"echoprint-codegen-linux-64";
			} else {
				cmd = "cmds"+System.getProperty("file.separator")+"echoprint-codegen-linux";
			}
		}
	}
	
	/**
	 * This method returns a JSONObject containing information from the echoprint command
	 * The JSONObject will have the following keys: tag, code_count, code, metadata
	 * code is the fingerprint
	 * metadata is the info found in the file (i.e. artist, sample_rate)
	 * @return a JSONObject
	 */
	public JSONObject fingerprint(File mp3){
		String s = null;
		try {
			//System.out.println(cmd+" "+mp3.getAbsolutePath()+" 10 30");
			ProcessBuilder pb = new ProcessBuilder(cmd, mp3.getAbsolutePath(), "10", "30");
			Process p = pb.start();//Runtime.getRuntime().exec(cmd+" "+mp3.getAbsolutePath()+" 10 30");
			//Process p = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));	 
            //BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));	 
            // read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
            s = stdInput.readLine();
            String output = "";
            if(!s.equals("[") && !s.equals("]") && s != null){
            	output += s;
            }
            while (s != null) {
            	s = stdInput.readLine();
            	if(s != null && !s.equals("[") && !s.equals("]")){
            		output += s;
            	}
            	//System.out.println(s);
            }

            //output = output.replace("{\"metadata\":", "");
            //output = output.replaceFirst("}", ""); 
            return new JSONObject(output);            
            //System.out.print(output);

            // read any errors from the attempted command
            /*System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }*/
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
