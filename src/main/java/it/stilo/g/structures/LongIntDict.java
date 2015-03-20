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

import gnu.trove.TCollections;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TLongIntHashMap;

/**
 *
 * @author stilo
 */
public class LongIntDict {

    private int current = 1;
    private TLongIntMap map = TCollections.synchronizedMap(new TLongIntHashMap());

    public synchronized int testAndSet(long l) {
        int id = map.get(l);
        if (id == 0) {
            id = current++;
            map.put(l, id);
        }
        return id;
    }
    
    public TLongIntIterator getIterator(){
        return this.map.iterator();
    }
    
    public int get(long l){
        return map.get(l);
    }
    
    public synchronized TIntLongMap  getInverted(){
        TIntLongMap inverted=TCollections.synchronizedMap(new TIntLongHashMap());
        
        TLongIntIterator iter = this.getIterator();
        while (iter.hasNext()) {
            iter.advance();
            inverted.put(iter.value(), iter.key());         
        }
               
        return inverted;
    }
}
