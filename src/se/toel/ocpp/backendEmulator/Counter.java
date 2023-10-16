/*
 * Simple counter class
 */

package se.toel.ocpp.backendEmulator;

/**
 *
 * @author toel
 */
public class Counter {

    /***************************************************************************
     * Constants and variables
     **************************************************************************/
    private static final Counter counter = new Counter();
    private int count = 0;

     /***************************************************************************
     * Constructor
     **************************************************************************/
    private Counter() {}

     /***************************************************************************
     * Public methods
     **************************************************************************/
    
    public static Counter geInstance() {
        return counter;
    }
    
    public int getNext() {
        count++;
        return count;
    }

    /***************************************************************************
     * Private methods
     **************************************************************************/

}
