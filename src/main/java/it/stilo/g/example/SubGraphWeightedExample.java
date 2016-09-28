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
import it.stilo.g.algo.ConnectedComponents;
import it.stilo.g.algo.SubGraphByEdgesWeight;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import it.stilo.g.util.MemInfo;
import it.stilo.g.util.WeightedRandomGenerator;
import java.util.Arrays;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class SubGraphWeightedExample {

    protected static final Logger logger = LogManager.getLogger(SubGraphWeightedExample.class);

    public static void main(String[] args) throws InterruptedException {
        int size = 15;
        int worker = (int) (Runtime.getRuntime().availableProcessors());

        logger.info("\t----\t" + size + "@" + 4 + ":" + worker + "\t---");
        WeightedDirectedGraph g = new WeightedDirectedGraph(size);

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

        //
        g.testAndAdd(10, 11, 0.1);
        g.testAndAdd(10, 12, 0.2);
        g.testAndAdd(10, 13, 0.3);
        g.testAndAdd(10, 14, 0.4);

        g.testAndAdd(11, 10, 1.0);
        g.testAndAdd(11, 12, 1.2);
        g.testAndAdd(11, 13, 1.3);
        g.testAndAdd(11, 14, 1.4);

        g.testAndAdd(12, 10, 2.0);
        g.testAndAdd(12, 11, 2.1);
        g.testAndAdd(12, 13, 2.3);
        g.testAndAdd(12, 14, 2.4);

        g.testAndAdd(13, 10, 3.0);
        g.testAndAdd(13, 11, 3.1);
        g.testAndAdd(13, 12, 3.2);
        g.testAndAdd(13, 14, 3.4);

        g.testAndAdd(14, 10, 4.0);
        g.testAndAdd(14, 11, 4.1);
        g.testAndAdd(14, 12, 4.2);
        g.testAndAdd(14, 13, 4.3);

        //g.testAndAdd(1, 13, 4.3);      
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g = SubGraphByEdgesWeight.extract(g, 3.0, 1);
        
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));
        
        
        WeightedUndirectedGraph gU = new WeightedUndirectedGraph(10);
        
        gU.testAndAdd(2, 0, 2.0);
        gU.testAndAdd(2, 1, 2.1);
        gU.testAndAdd(2, 3, 2.3);
        gU.testAndAdd(2, 4, 2.4);

        gU.testAndAdd(3, 0, 3.0);
        gU.testAndAdd(3, 1, 3.1);        
        gU.testAndAdd(3, 4, 3.4);
        
        logger.info(Arrays.deepToString(gU.out));
        logger.info(Arrays.deepToString(gU.in));
        logger.info(Arrays.deepToString(gU.weights));

        gU = SubGraphByEdgesWeight.extract(gU, 3.0, 8);
        
        logger.info(Arrays.deepToString(gU.out));
        logger.info(Arrays.deepToString(gU.in));
        logger.info(Arrays.deepToString(gU.weights));
        
        MemInfo.info();

    }

}
