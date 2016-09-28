/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.util;

/*
 * #%L
 * G-github
 * %%
 * Copyright (C) 2013 - 2016 Giovanni Stilo
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

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 *
 * @author stilo
 */
public class NodesMapper<T> {

    private int last = 0;
    private TObjectIntHashMap<T> nodesIds = new TObjectIntHashMap<T>();
    private TIntObjectHashMap<T> idsObjects = new TIntObjectHashMap<T>();

    public synchronized void reset() {
        last = 0;
        nodesIds.clear();
        idsObjects.clear();
    }

    public synchronized T getNode(int i) {
        return idsObjects.get(i);
    }

    public synchronized int getId(T obj) {
        Integer i = null;
        if ((i = nodesIds.get(obj)) != 0) {
            return i;
        } else {
            last++;
            nodesIds.put(obj, last);
            idsObjects.put(last, obj);
        }

        return last;
    }
}
