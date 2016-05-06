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

import it.stilo.g.algo.KppNeg;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.MemInfo;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
*
* @author Di Tommaso, Stilo
*/

public class KppNegExample {

    protected static final Logger logger = LogManager.getLogger(KppNegExample.class);

    public static void main(String[] args) throws InterruptedException {
        
        int worker = (int) (Runtime.getRuntime().availableProcessors());
        
//        int size = 6;
//        logger.info("\t----\t" + size + "@" + 4 + ":" + worker + "\t---");
//        WeightedDirectedGraph g = new WeightedDirectedGraph(size);
//
//        //DirectGraph
//        g.testAndAdd(1, 3, 1);
//        
//        g.testAndAdd(2, 5, 1);
//        
//        g.testAndAdd(3, 2, 1);
//        g.testAndAdd(3, 4, 1);
//        g.testAndAdd(3, 5, 1);
//        
//        g.testAndAdd(4, 5, 1);
        
//      int size = 20;
//     // logger.info("\t----\t" + size + "@" + 4 + ":" + worker + "\t---");
//      WeightedDirectedGraph g = new WeightedDirectedGraph(size);
//      
//      //UndirectGraph
//      g.testAndAdd(1, 3, 1);
//      
//      g.testAndAdd(2, 3, 1);
//      g.testAndAdd(2, 5, 1);
//      
//      g.testAndAdd(3, 1, 1);
//      g.testAndAdd(3, 2, 1);
//      g.testAndAdd(3, 4, 1);
//      g.testAndAdd(3, 5, 1);
//      
//      g.testAndAdd(4, 3, 1);
//      g.testAndAdd(4, 5, 1);
//      
//      g.testAndAdd(5, 2, 1);
//      g.testAndAdd(5, 3, 1);
//      g.testAndAdd(5, 4, 1);
      
        
        
        int size = 20;
        logger.info("\t----\t" + size + "@" + 4 + ":" + worker + "\t---");
        WeightedDirectedGraph g = new WeightedDirectedGraph(size);
        
        //Undirect graph       
        g.testAndAdd(1, 2, 1);
        g.testAndAdd(1, 4, 1);
        g.testAndAdd(1, 6, 1);
        
        g.testAndAdd(2, 1, 1);
        g.testAndAdd(2, 4, 1);
        g.testAndAdd(2, 5, 1);
        g.testAndAdd(2, 7, 1);
        
        g.testAndAdd(3, 4, 1);
        g.testAndAdd(3, 8, 1);
        
        g.testAndAdd(4, 1, 1);
        g.testAndAdd(4, 2, 1);
        g.testAndAdd(4, 3, 1);
        g.testAndAdd(4, 5, 1);
        g.testAndAdd(4, 6, 1);
        g.testAndAdd(4, 7, 1);
        
        g.testAndAdd(5, 2, 1);
        g.testAndAdd(5, 4, 1);
        g.testAndAdd(5, 7, 1);
        
        g.testAndAdd(6, 1, 1);
        g.testAndAdd(6, 4, 1);
        g.testAndAdd(6, 7, 1);
        g.testAndAdd(6, 8, 1);
        
        g.testAndAdd(7, 2, 1);
        g.testAndAdd(7, 4, 1);
        g.testAndAdd(7, 5, 1);
        g.testAndAdd(7, 6, 1);
        g.testAndAdd(7, 8, 1);
        
        g.testAndAdd(8, 3, 1);
        g.testAndAdd(8, 6, 1);
        g.testAndAdd(8, 7, 1);
        g.testAndAdd(8, 9, 1);
        g.testAndAdd(8, 18, 1);
        
        g.testAndAdd(9, 8, 1);
        g.testAndAdd(9, 10, 1);
        g.testAndAdd(9, 14, 1);
        g.testAndAdd(9, 16, 1);
        
        g.testAndAdd(10, 9, 1);
        g.testAndAdd(10, 11, 1);
        g.testAndAdd(10, 12, 1);
        g.testAndAdd(10, 13, 1);
        g.testAndAdd(10, 15, 1);
        g.testAndAdd(10, 16, 1);
        
        g.testAndAdd(11, 10, 1);
        g.testAndAdd(11, 12, 1);
        g.testAndAdd(11, 16, 1);
        
        g.testAndAdd(12, 10, 1);
        g.testAndAdd(12, 11, 1);
        g.testAndAdd(12, 13, 1);
        
        g.testAndAdd(13, 10, 1);
        g.testAndAdd(13, 12, 1);
        g.testAndAdd(13, 17, 1);
        
        g.testAndAdd(14, 9, 1);
        g.testAndAdd(14, 15, 1);
        g.testAndAdd(14, 16, 1);
        
        g.testAndAdd(15, 10, 1);
        g.testAndAdd(15, 14, 1);
        
        g.testAndAdd(16, 9, 1);
        g.testAndAdd(16, 10, 1);
        g.testAndAdd(16, 11, 1);
        g.testAndAdd(16, 14, 1);
        
        g.testAndAdd(17, 13, 1);
        g.testAndAdd(17, 19, 1);
        
        g.testAndAdd(18, 8, 1);
        
        g.testAndAdd(19, 17, 1);
        
        
//        int size = 10;
//        logger.info("\t----\t" + size + "@" + 4 + ":" + worker + "\t---");
//        WeightedDirectedGraph g = new WeightedDirectedGraph(size);
//        
//        //Undirect graph        
//        g.testAndAdd(1, 2, 1);
//        g.testAndAdd(1, 3, 1);
//        
//        g.testAndAdd(2, 1, 1);
//        g.testAndAdd(2, 3, 1);
//        
//        g.testAndAdd(3, 1, 1);
//        g.testAndAdd(3, 2, 1);
//        g.testAndAdd(3, 4, 1);
//        g.testAndAdd(3, 8, 1);
//        
//        g.testAndAdd(4, 3, 1);
//        g.testAndAdd(4, 5, 1);
//        g.testAndAdd(4, 8, 1);
//        
//        g.testAndAdd(5, 4, 1);
//        g.testAndAdd(5, 6, 1);
//        g.testAndAdd(5, 8, 1);
//        
//        g.testAndAdd(6, 5, 1);
//        g.testAndAdd(6, 7, 1);
//        g.testAndAdd(6, 8, 1);
//        
//        g.testAndAdd(7, 6, 1);
//        g.testAndAdd(7, 8, 1);
//        g.testAndAdd(7, 9, 1);
//        
//        g.testAndAdd(8, 3, 1);
//        g.testAndAdd(8, 4, 1);
//        g.testAndAdd(8, 5, 1);
//        g.testAndAdd(8, 6, 1);
//        g.testAndAdd(8, 7, 1);
//        g.testAndAdd(8, 9, 1);
//        
//        g.testAndAdd(9, 7, 1);
//        g.testAndAdd(9, 8, 1);
       
        
        logger.info("out"+Arrays.deepToString(g.out));
        logger.info("in"+Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        List<DoubleValues> brokers = KppNeg.searchBroker(g, g.getVertex(), worker);
        
        for(DoubleValues list : brokers){
        	logger.trace("List broker value: "+ (int)list.value + " id: " + list.index);
        }

        
        MemInfo.info();

    }

}
