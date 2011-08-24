package models.eliza;

import java.awt.*;
import play.*;

/**
 *  Eliza Application.
 */
public class Eliza {
    public static long user_id = -2;    
    
    private static String devScript = "conf/script.txt";
    private static String prodScript = "data/script.txt"; 
    private static ElizaMain instance = null;
    
    public static void init () {
        if (instance == null) {
            instance = new ElizaMain();
            boolean isDev = Play.mode == Play.Mode.DEV;
            instance.readScript(isDev ? devScript : prodScript);
        }
    }

    public static String respondTo (String s) {
		return instance.processInput(s);
    }

}
