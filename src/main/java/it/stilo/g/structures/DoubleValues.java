package it.stilo.g.structures;

/*
 * #%L
 * G
 * %%
 * Copyright (C) 2014 Giovanni Stilo
 * %%
 * G is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <https://www.gnu.org/licenses/lgpl-3.0.txt>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class DoubleValues implements Indexable {
    private static final Logger logger = LogManager.getLogger(DoubleValues.class);

    public int index;
    public double value;

    public DoubleValues() {

    }

    public DoubleValues(int index, double value) {
        this.index = index;
        this.value = value;
    }
    
    @Override
    public int hashCode(){
        return index;
    }
    
    @Override
    public boolean equals(Object o){
        return (value==((DoubleValues) o).value)&&(index==((DoubleValues) o).index);
    }
    
    public int compareTo(Object o) {
        int comp = Double.compare(value, ((DoubleValues) o).value);
        switch (comp) {
            case 1:
                return -1;
            case -1:
                return 1;
            default:
                return Integer.compare(index, ((DoubleValues) o).index);
        }
    }

    public String toString() {
        return index + "," + value;
    }
    
    public int getIndex() {
        return this.index;
    }

    public static void main(String[] args) {
        ArrayList<DoubleValues> list = new ArrayList<DoubleValues>();

        list.add(new DoubleValues(5, 2.5));

        list.add(new DoubleValues(0, 4.5));
        list.add(new DoubleValues(3, 4.5));
        list.add(new DoubleValues(2, 4.5));
        list.add(new DoubleValues(4, 4.5));
        list.add(new DoubleValues(6, 4.5));

        list.add(new DoubleValues(1, 4.4));

        Collections.sort(list);
        
        logger.info(list);
    }
}
