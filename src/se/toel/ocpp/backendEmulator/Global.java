/*
 * Just a singleton holder for global stuff
 */

package se.toel.ocpp.backendEmulator;

/**
 *
 * @author toel
 */
public class Global {

    
    /***************************************************************************
     * Constants and variables
     **************************************************************************/
    private static final Global instance = new Global();
    private Server server = null;

     /***************************************************************************
     * Constructor
     **************************************************************************/
    private Global() {
    }

     /***************************************************************************
     * Public methods
     **************************************************************************/
    public static Global getInstance() {
        return instance;
    }
    
    /**
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    /**
     * @param server the server to set (only one time)
     */
    public void setServer(Server server) {
        if (this.server==null) this.server = server;
    }
    
    
    /***************************************************************************
     * Private methods
     **************************************************************************/

}
