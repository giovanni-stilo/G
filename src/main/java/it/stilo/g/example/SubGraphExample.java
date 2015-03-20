package it.stilo.g.example;

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

import it.stilo.g.algo.SubGraph;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.MemInfo;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class SubGraphExample {
    protected static final Logger logger = LogManager.getLogger(SubGraphExample.class);

    public static void main(String [] args){
        int size = 5;
        int worker = (int) (Runtime.getRuntime().availableProcessors());
       
        logger.info("\t----\t"+size+"@"+(size-1)+":"+worker+"\t---");
        WeightedDirectedGraph g= new WeightedDirectedGraph(size);
        
        g.testAndAdd(0, 1, 0.1);
        g.testAndAdd(0, 2, 0.2);
        g.testAndAdd(0, 3, 0.3);
        g.testAndAdd(0, 4, 0.4);
        
        g.testAndAdd(1, 0, 1.0);
        g.testAndAdd(1, 2, 1.2);
        g.testAndAdd(1, 3, 1.3);
        g.testAndAdd(1, 4, 1.4);
        
        g.testAndAdd(2, 0, 2.0);
        g.testAndAdd(2, 1, 2.1);
        g.testAndAdd(2, 3, 2.3);
        g.testAndAdd(2, 4, 2.4);
        
        g.testAndAdd(3, 0, 3.0);
        g.testAndAdd(3, 1, 3.1);
        g.testAndAdd(3, 2, 3.2);
        g.testAndAdd(3, 4, 3.4);
        
        g.testAndAdd(4, 0, 4.0);
        g.testAndAdd(4, 1, 4.1);
        g.testAndAdd(4, 2, 4.2);
        g.testAndAdd(4, 3, 4.3);
        
        MemInfo.info();
       
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));
        WeightedDirectedGraph s=SubGraph.extract(g, new int[]{1,2,3,4}, worker);
        logger.info(Arrays.deepToString(s.out));
        logger.info(Arrays.deepToString(s.in));
        logger.info(Arrays.deepToString(s.weights));
        s=SubGraph.extract(g, new int[]{0,2,3,4}, worker);
        logger.info(Arrays.deepToString(s.out));
        logger.info(Arrays.deepToString(s.in));
        logger.info(Arrays.deepToString(s.weights));
        s=SubGraph.extract(g, new int[]{0,1,2,4}, worker);
        logger.info(Arrays.deepToString(s.out));
        logger.info(Arrays.deepToString(s.in));
        logger.info(Arrays.deepToString(s.weights));
        s=SubGraph.extract(g, new int[]{0,1,3}, worker);
        logger.info(Arrays.deepToString(s.out));
        logger.info(Arrays.deepToString(s.in));
        logger.info(Arrays.deepToString(s.weights));
        g.remove(2);
        g.remove(4);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));
        
        MemInfo.info();
    }
    
}
