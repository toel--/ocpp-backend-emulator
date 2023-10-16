/*
 * Server part of the OCPP relay
 */

package se.toel.ocpp.backendEmulator;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import se.toel.util.Dev;

/**
 *
 * @author toel
 */
public class Server extends WebSocketServer {

    /***************************************************************************
     * Constants and variables
     **************************************************************************/
    Map<WebSocket, Emulator> connections = new HashMap<>();
    private String scenario;
    
     /***************************************************************************
     * Constructor
     **************************************************************************/
    public Server(int port, String scenario) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.scenario = scenario;
    }

     /***************************************************************************
     * Public methods
     **************************************************************************/
    
    public void shutdown() {
        
        for (Map.Entry<WebSocket, Emulator> connection : connections.entrySet()) {
            connection.getKey().close();
            connection.getValue().shutdown();
        }
        
        try {
            this.stop();
        } catch (Exception e) {}
        
        
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
                
        String deviceId = getDeviceId(conn.getResourceDescriptor());
        System.err.println("");
        Dev.info(deviceId+" connect from "+conn.getRemoteSocketAddress().getHostString()+":"+conn.getRemoteSocketAddress().getPort());
        
        try {
            Emulator emulator = new Emulator(deviceId, handshake, conn, getScenario());
            connections.put(conn, emulator);
        } catch (Exception e) {
            Dev.error("While opening connection", e);
        }
        
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                
        String deviceId = getDeviceId(conn.getResourceDescriptor());
        Dev.info(deviceId+" disconnected "+(remote ? "by charge point" : "by central system"));
        Emulator emulator = connections.get(conn);
        if (emulator!=null) {
            emulator.shutdown();
            connections.remove(conn);
        }
        
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        
        String deviceId = getDeviceId(conn.getResourceDescriptor());
        Dev.info(deviceId+" R "+message);
        Emulator emulator = connections.get(conn);
        emulator.incoming(message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        Dev.info("Unsupported ByteBuffer received...");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Dev.error("                 error on connection to charge point", ex);
        ex.printStackTrace(System.err);
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
        System.exit(1);
    }

    @Override
    public void onStart() {
        Dev.info("Server started!");
        setConnectionLostTimeout(100);
    }

    /**
     * @return the scenario
     */
    public String getScenario() {
        return scenario;
    }

    /**
     * @param scenario the scenario to set
     */
    public void setScenario(String scenario) {
        this.scenario = scenario;
    }
    
    
    /***************************************************************************
     * Private methods
     **************************************************************************/
    private String getDeviceId(String url) {
     
        int p = url.lastIndexOf('/');
        return url.substring(p+1);
        
    }

}
