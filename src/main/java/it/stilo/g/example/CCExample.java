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
public class CCExample {

    protected static final Logger logger = LogManager.getLogger(CCExample.class);

    public static void main(String[] args) throws InterruptedException {
        int size = 15;
        int worker = (int) (Runtime.getRuntime().availableProcessors());

        logger.info("\t----\t" + size + "@" + 4 + ":" + worker + "\t---");
        WeightedUndirectedGraph g = new WeightedUndirectedGraph(size);

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

        Set<Set<Integer>> comps = ConnectedComponents.rootedConnectedComponents(g, new int[]{11, 10}, worker);
        logger.info(comps);

        MemInfo.info();

        size = 50;

        int degree = 4;
        logger.info("\t----\t" + size + "@" + degree + ":" + worker + "\t----");
        WeightedUndirectedGraph big = new WeightedUndirectedGraph(size);
        /*WeightedRandomGenerator.generate(big, degree, worker);
         big.remove(25);
         big.remove(26);
         big.remove(27);
         big.remove(28);
         big.remove(29);
         big.remove(30);*/

        big.testAndAdd(0, 4, 1.0);
        big.testAndAdd(1, 47, 1.0);
                //null, 2
        //[],  3
        big.testAndAdd(4, 0, 1.0);
        big.testAndAdd(4, 16, 1.0);
        big.testAndAdd(5, 42, 1.0);
        big.testAndAdd(6, 33, 1.0);
        big.testAndAdd(7, 16, 1.0);
        //null, 8
        big.testAndAdd(9, 31, 1.0);
        big.testAndAdd(9, 47, 1.0);
        big.testAndAdd(10, 22, 1.0);
        big.testAndAdd(10, 34, 1.0);
        big.testAndAdd(11, 35, 1.0);
        big.testAndAdd(12, 15, 1.0);
        big.testAndAdd(12, 21, 1.0);
        big.testAndAdd(12, 42, 1.0);
        big.testAndAdd(13, 20, 1.0);
        big.testAndAdd(14, 21, 1.0);
        big.testAndAdd(14, 47, 1.0);
        big.testAndAdd(15, 12, 1.0);
        big.testAndAdd(15, 38, 1.0);
        big.testAndAdd(15, 39, 1.0);
        big.testAndAdd(15, 41, 1.0);
        big.testAndAdd(15, 44, 1.0);
        big.testAndAdd(15, 49, 1.0);
        big.testAndAdd(16, 4, 1.0);
        big.testAndAdd(16, 7, 1.0);
        big.testAndAdd(16, 38, 1.0);
        //null, 17
        big.testAndAdd(18, 33, 1.0);
        big.testAndAdd(18, 44, 1.0);
        //null, 19
        big.testAndAdd(20, 13, 1.0);
        big.testAndAdd(20, 41, 1.0);
        /* --- */ big.testAndAdd(21, 12, 1.0);
        big.testAndAdd(21, 14, 1.0);
        big.testAndAdd(21, 32, 1.0);
        big.testAndAdd(22, 10, 1.0);
        big.testAndAdd(23, 37, 1.0);
                //null, 24
        //null, 25
        //null, 26
        //null, 27
        //null, 28
        //null, 29
        //null, 30
        big.testAndAdd(31, 9, 1.0);
        big.testAndAdd(31, 43, 1.0);
        big.testAndAdd(32, 21, 1.0);
        big.testAndAdd(32, 49, 1.0);
        big.testAndAdd(33, 6, 1.0);
        big.testAndAdd(33, 18, 1.0);
        big.testAndAdd(33, 39, 1.0);
        big.testAndAdd(33, 45, 1.0);
        big.testAndAdd(34, 10, 1.0);
        big.testAndAdd(35, 11, 1.0);
        //null, 36
        big.testAndAdd(37, 23, 1.0);
        big.testAndAdd(37, 43, 1.0);
        big.testAndAdd(37, 48, 1.0);
        big.testAndAdd(38, 15, 1.0);
        big.testAndAdd(38, 16, 1.0);
        big.testAndAdd(39, 15, 1.0);
        big.testAndAdd(39, 33, 1.0);
        big.testAndAdd(39, 41, 1.0);
        big.testAndAdd(40, 41, 1.0);
        big.testAndAdd(41, 15, 1.0);
        big.testAndAdd(41, 20, 1.0);
        big.testAndAdd(41, 39, 1.0);
        big.testAndAdd(41, 40, 1.0);
        big.testAndAdd(41, 48, 1.0);
        big.testAndAdd(42, 5, 1.0);
        big.testAndAdd(42, 12, 1.0);
        big.testAndAdd(43, 31, 1.0);
        big.testAndAdd(43, 37, 1.0);
        big.testAndAdd(44, 15, 1.0);
        big.testAndAdd(44, 18, 1.0);
        big.testAndAdd(45, 33, 1.0);
        //null, 46
        big.testAndAdd(47, 1, 1.0);
        big.testAndAdd(47, 9, 1.0);
        big.testAndAdd(47, 14, 1.0);
        big.testAndAdd(48, 37, 1.0);
        big.testAndAdd(48, 41, 1.0);
        big.testAndAdd(49, 15, 1.0);
        big.testAndAdd(49, 32, 1.0);

        big.remove(41);
        big.remove(42);
        big.remove(43);
        big.remove(44);
        big.remove(45);

        big.remove(15);
        big.remove(16);

        logger.info(Arrays.deepToString(big.out));

        int[] all = new int[big.size];
        for (int i = 0; i < big.size; i++) {
            all[i] = i;
        }
        comps = ConnectedComponents.rootedConnectedComponents(big, all, worker);
        logger.info(comps);

        size = 1000000;
        degree = 10;
        WeightedUndirectedGraph mega = new WeightedUndirectedGraph(size);
        WeightedRandomGenerator.generate(mega, degree, worker);
        for (int i = degree; i < size; i += degree) {
            mega.remove(i);
        }

        comps = ConnectedComponents.rootedConnectedComponents(mega, new int[]{5, 94995}, worker);
        logger.info(comps.iterator().next().size());
        MemInfo.info();
    }

}
