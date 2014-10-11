package tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class PrinterGrabber {
	private String cmd;
	
	public PrinterGrabber(){
		if(System.getProperty("os.name").contains("Windows")){
			//TODO add in windows binary in repo
			cmd = "cmds"+System.getProperty("file.separator")+"echoprint-codegen-windows";
		} else {
			cmd = "cmds"+System.getProperty("file.separator")+"echoprint-codegen-linux";
		}
	}
	
	/**
	 * This method returns a 
	 */
	public JSONObject fingerprint(File mp3){
		String s = null;
		try {
			System.out.println(cmd+" "+mp3.getAbsolutePath()+" 10 30");			
			Process p = Runtime.getRuntime().exec(cmd+" "+mp3.getAbsolutePath());	             
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));	 
            //BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));	 
            // read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
            s = stdInput.readLine();
            String output = "";
            if(!s.equals("[") && !s.equals("]")){
            	output += s;
            }
            while (s != null) {
            	s = stdInput.readLine();
            	if(s != null && !s.equals("[") && !s.equals("]")){
            		output += s;
            	}
            	//System.out.println(s);
            }
            //System.out.print(output);
			try {
				return new JSONObject(output);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
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
