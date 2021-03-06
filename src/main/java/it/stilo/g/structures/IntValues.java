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
public class IntValues implements Indexable {
    private static final Logger logger = LogManager.getLogger(IntValues.class);

    public int index;
    public int value;
    public int size;

    public IntValues() {

    }

    public IntValues(int index, int value, int size) {
        this.index = index;
        this.value = value;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        return (value == ((IntValues) o).value) && (index == ((IntValues) o).index);
    }

    public int compareTo(Object o) {
        int comp = Integer.compare(value, ((IntValues) o).value);
        switch (comp) {
            case 1:
                return 1;
            case -1:
                return -1;
            default:
                return Integer.compare(index, ((IntValues) o).index);
        }
    }

    public String toString() {
        return "[" + index + "," + value + "," + size + "]";
    }

    public static void main(String[] args) {
        ArrayList<IntValues> list = new ArrayList<IntValues>();

        list.add(new IntValues(5, 2, 4));

        list.add(new IntValues(0, 5, 4));

        list.add(new IntValues(1, 2, 4));

        Collections.sort(list);
        logger.info(list);
    }

    public int getIndex() {
        return this.index;
    }
}
