/*
 * Main application
 */

package se.toel.ocpp.backendEmulator;

import se.toel.util.Dev;

/**
 *
 * @author toel
 */
public class Application {

    /***************************************************************************
     * Constants and variables
     **************************************************************************/
    private static boolean running = true;

     /***************************************************************************
     * Constructor
     **************************************************************************/

     /***************************************************************************
     * Public methods
     **************************************************************************/
    
    public void execute(int port, String scenario) {
        
        // Shutdown hook to close the db connection
        Runtime.getRuntime().addShutdownHook( new Thread (  )  {  
            @Override
            public void run() {
                shutdown();
            }
        }); 
        
        Dev.debugEnabled(true);
        Dev.setWriteToFile(true);
        
        try {
        
            System.out.println("starting server on port: " + port);
            Server server = new Server(port, scenario);
            Global.getInstance().setServer(server);
            server.start();
            System.out.println(" done");

            while (running) {
                Dev.sleep(100);
            }
            
            server.shutdown();
            Dev.sleep(1000);
            System.exit(0);
            
        
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        
    }
    
    
    public static void shutdown() {
        
        Dev.info("Shuting down...");
        running = false;
        
    }
    
        
    /***************************************************************************
     * Private methods
     **************************************************************************/

}
