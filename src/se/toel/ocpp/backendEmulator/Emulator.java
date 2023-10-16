/*
 * WS Emulator for the OCPP bridge
 */

package se.toel.ocpp.backendEmulator;

import java.net.InetSocketAddress;
import java.util.Iterator;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.toel.util.Dev;
import se.toel.util.FileUtils;

/**
 *
 * @author toel
 */
public class Emulator {

    /***************************************************************************
     * Constants and variables
     **************************************************************************/
    private final Logger log = LoggerFactory.getLogger(Emulator.class);
    private final String deviceId;
    private final WebSocket ws;
    private final String scenario;
    private final int conId = Counter.geInstance().getNext();
    private final Clock clock = new Clock();
    JSONArray script = null;
    private long timer = 0, transactionId = 0;

     /***************************************************************************
     * Constructor
     **************************************************************************/
    public Emulator(String deviceId, ClientHandshake handshake, WebSocket webSocket, String scenario) {
        
        this.deviceId = deviceId;
        this.ws = webSocket;
        this.scenario = scenario;
        clock.start();
        
        InetSocketAddress isa = webSocket.getRemoteSocketAddress();
        System.out.println("Incoming connection from "+isa.getHostName()+":"+isa.getPort());
        
        Iterator<String> it = handshake.iterateHttpFields();
        while (it.hasNext()) {
            String key = it.next();
            String value = handshake.getFieldValue(key);
            System.out.println("   header "+key+": "+value);
        }
        
    }
    
    

     /***************************************************************************
     * Public methods
     **************************************************************************/
    
    
    public void shutdown() {
        clock.shutdown();
    }
    
    
    public void incoming(String message) {
        
        try {
           
            JSONArray arr = new JSONArray(message);
            int type = arr.getInt(0);
            if (type==2) {
                String msgId = arr.getString(1);
                String op = arr.getString(2);
                JSONObject payload = arr.getJSONObject(3);
                String conf = getConf(op, msgId);
                send(conf);
            }
            
        } catch (Exception e) {
            Dev.error("while handling incoming '"+message+"': "+e.getClass().getName()+": "+e.getMessage());
        }
        
    }
    

    /***************************************************************************
     * Private methods
     **************************************************************************/
    
    private String getConf(String op, String msgId) {
     
        switch (op) {
            case "StartTransaction": transactionId = System.currentTimeMillis()/1000l; break;
        }
        
        String json = populate(loadConf(op), msgId);
        
        return json;
        
    }
    
    private String loadConf(String op) {
     
        String json = FileUtils.getFileContent("data/conf/"+op+".json");
        if (json==null) {
            json = "[3, \"${msgId}\", {}]";
            FileUtils.setFileContent("data/conf/"+op+".json", json);
        }
        return json;
        
    }
    
    private String loadReq(String op) {
     
        String json = FileUtils.getFileContent("data/req/"+op+".json");
        if (json==null) {
            json = "[2, \"${msgId}\", \""+op+"\", {}]";
            FileUtils.setFileContent("data/req/"+op+".json", json);
        }
        return json;
        
    }
    
    
    private JSONArray loadScript(String scenario) {
     
        Dev.info("Loading scenario: "+scenario);
        
        try {
            String json = FileUtils.getFileContent("data/scenario/"+scenario+".json");
            JSONArray array = new JSONArray(json);
            return array;
        } catch (Exception e) {
            Dev.error("while loading scenario '"+scenario+"': "+e.getClass().getName()+": "+e.getMessage());
        }
        return null;
        
    }
    
    private String populate(String json, String msgId) {
        
        json = json.replace("${msgId}", msgId);
        json = json.replace("${now}", DateTimeUtil.toIso8601(System.currentTimeMillis()));
        json = json.replace("${transactionId}", String.valueOf(transactionId));
        
        return json;
        
    }
    
    
    private void send(String msg) {
        
        Dev.info(deviceId+" S "+msg);
        ws.send(msg);
    
    }
    
    
    private void tick() {
        
        timer++;
        
        if (script==null) script = loadScript(scenario);        
        
        if (script!=null) {
         
            if (script.length()==0) shutdown();
            
            JSONObject action = script.getJSONObject(0);
            
            int sec = action.optInt("second", -1);
            if (sec>=0 && timer>=sec) {
                
                script.remove(0);
                
                String actionType = action.optString("type", "-");
                String op = action.optString("op", "-");
                switch (actionType) {
                    case "script": doActionScript(op); break;
                    case "ocpp": doActionOcpp(op); break;
                    default: Dev.error("unsupported scenario action type '"+actionType+"'");
                }
            }
        }
    }
    
    
    private void doActionScript(String op) {
        
        System.out.println();
        Dev.info("Executing script action "+op);
        
        switch (op) {
            case "exit": shutdown(); System.exit(0); break;
            case "restart": timer=0; script=null; break;
            default: Dev.error("unsupported scenario req '"+op+"'");
        }
        
    }
    
    
    private void doActionOcpp(String op) {
        
        System.out.println();
        Dev.info("Executing ocpp action "+op);
        
        if (op.equals("UpdateFirmware")) Global.getInstance().getServer().setScenario("run");
        
        String json = loadReq(op);
        json = populate(json, String.valueOf(System.currentTimeMillis()));
        send(json);
        
    }
    
    
    private class Clock extends Thread {
    
        private boolean running = false;
        
        @Override
        public void run() {

            long lastRun = System.currentTimeMillis();
            long periode = 1000;
            running = true;
            
            while (running) {
                Dev.sleep(50);
                long now = System.currentTimeMillis();
                if (((now-lastRun))>periode) {
                    lastRun = now;
                    tick();
                }
            }

        }

        public void shutdown() {
            running = false;
        }
        
        
    }
    

}
