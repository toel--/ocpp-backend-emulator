/*
 * Using https://github.com/TooTallNate/Java-WebSocket
 */
package se.toel.ocpp.backendEmulator;

import java.net.URISyntaxException;
import se.toel.util.StringUtil;

/**
 *
 * @author toel
 */
public class Main {

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws URISyntaxException {
        
        if (args.length<1) showSyntaxAndExit();
        
        int port = StringUtil.str2int(args[0]);
        String scenario = "default";
        if (args.length>1) scenario = args[1];
        
        Application app = new Application();
        app.execute(port, scenario);
        
    }
    
    
    private static void showSyntaxAndExit() {
        
        System.out.println("OCPP Backend emulator");
        System.out.println("Toel Hartmann 2023");
        System.out.println();
        System.out.println("  Syntax:");
        System.out.println("     java -jar OcppBackendEmulator.jar [port] [scenario]");
        System.out.println("  where");
        System.out.println("     [port] is the local port to listen to");
        System.out.println("     [scenario] is scenario to execute");
        System.exit(1);
        
    }
    
    
}