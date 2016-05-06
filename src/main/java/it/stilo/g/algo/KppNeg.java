package it.stilo.g.algo;

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
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Di Tommaso, Stilo
 */
public class KppNeg {

    private static final Logger logger = LogManager.getLogger(KppNeg.class);



    public static  ArrayList<DoubleValues> searchBroker(final WeightedDirectedGraph gAllVertex, final int[] nodes, int runner) throws InterruptedException {
        long time = System.currentTimeMillis();
        
        ArrayList<DoubleValues> listCg = new ArrayList<DoubleValues>(); 
        ArrayList<Integer> brokers = new ArrayList<Integer>();
        int cg = 0;
        
        //compute reachability of entire graph
        int cgCompleteGraph = ReachabilityScore.compute(gAllVertex, runner);
        
        //for each node v remove v from the graph and compute reachability for the graph
        for (int i = 0; i < nodes.length; i++) {
        	WeightedDirectedGraph gv =  UnionDisjoint.copy(gAllVertex, runner); 
        	gv.remove(nodes[i]);
        	cg = ReachabilityScore.compute(gv, runner);
        	listCg.add(new DoubleValues(nodes[i], cgCompleteGraph-cg ));
        }
        
        Collections.sort(listCg);

        
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");

        return listCg;
    }

}
