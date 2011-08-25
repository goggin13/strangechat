package models.eliza;

import play.Play;

/**
 *  Eliza Application.
 */
public class Eliza {
    public static long user_id = -2;    
    
    private static String devScript = "conf/script.txt";
    private static String prodScript = "data/script.txt"; 
    private static ElizaMain eliza = null;
    private static Eliza instance = null;
    
    private Eliza () {
    }

    public String respondTo (String s) {
		return eliza.processInput(s);
    }

    public static Eliza get () {
        if (eliza == null) {
            eliza = new ElizaMain();
            boolean isDev = Play.mode == Play.Mode.DEV;
            eliza.readScript(isDev ? devScript : prodScript);
        }
        if (instance == null) {
            instance = new Eliza();
        }
        return instance;
    }
}
