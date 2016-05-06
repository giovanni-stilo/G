/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.structures;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class ObjectValues implements Comparable {

    private static final Logger logger = LogManager.getLogger(ObjectValues.class);

    public Object term;
    public int value;
    
    public ObjectValues(Object term, int  value) {
        this.term = term;
        this.value = value;       
    }

    @Override
    public boolean equals(Object o) {
        return (value == ((ObjectValues) o).value);
    }

    public int compareTo(Object o) {
        return Integer.compare(((ObjectValues) o).value,value);        
    }

}
