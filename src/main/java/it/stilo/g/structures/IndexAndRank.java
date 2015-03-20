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

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.TreeSet;

/**
 *
 * @author stilo
 * @param <T>
 */
public class IndexAndRank<T extends Indexable> {
    private TIntObjectHashMap<T> index= new TIntObjectHashMap<T>();
    private TreeSet<T> rank= new TreeSet<T>();
    
    public synchronized void add(int i,T obj){
        int bef=rank.size();
        rank.add(obj);        
        index.put(i, obj);
        /*if(rank.size()==bef)
            logger.info(obj+"@"+i+"-"+bef);*/
    }
    
    public synchronized T get(int i){
        return index.get(i);
    }
    
    public synchronized int size(){
        return rank.size();
    }
    
    public synchronized boolean contains(int i){
        return index.contains(i);
    }
    
    public synchronized T remove(int i){
        T obj= index.get(i);
        index.remove(i);
        rank.remove(obj);
        return obj;
    }        
    
    public synchronized T poolFirst(){
        T obj= rank.pollFirst();
        if(obj==null)
            return null;
        index.remove(obj.getIndex());
        return obj;
    }
    
    public synchronized T pollLast(){
        T obj= rank.pollLast();
        if(obj==null)
            return null;
        index.remove(obj.getIndex());
        return obj;
    }
}
