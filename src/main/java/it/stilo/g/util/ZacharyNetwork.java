package it.stilo.g.util;

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

import it.stilo.g.structures.WeightedDirectedGraph;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stilo
 */
public class ZacharyNetwork implements Runnable {
    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ZacharyNetwork.class);


    private WeightedDirectedGraph g;
    private CountDownLatch barrier;
    private int runner;
    private int chunk;
    
    public static int VERTEX=34;

    public ZacharyNetwork(WeightedDirectedGraph g, CountDownLatch barrier,int chunk, int runner) {
        this.g = g;
        this.barrier = barrier;
        this.runner = runner;
        this.chunk= chunk;
    }

    public void run() {
       
        for (int i = chunk; i < input.length; i += runner) {
            g.add(input[i][0]-1, input[i][1]-1, 1.0d);
            g.add(input[i][1]-1, input[i][0]-1, 1.0d);
        }
        barrier.countDown();
    }

    public static void generate(WeightedDirectedGraph g, int runner) {
        long time = System.currentTimeMillis();
        final  CountDownLatch latch = new CountDownLatch(runner);

        
        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new ZacharyNetwork(g,latch,i,runner));
            workers[i].setName("" + i);
            workers[i].start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");

    }
    
    private static int[][] input = {{1, 2}, {1, 3}, {2, 3}, {1, 4}, {2, 4}, {3, 4}, {1, 5}, {1, 6}, {1, 7}, {5, 7},
    {6, 7}, {1, 8}, {2, 8}, {3, 8}, {4, 8}, {1, 9}, {3, 9}, {3, 10}, {1, 11}, {5, 11}, {6, 11}, {1, 12}, {1, 13},
    {4, 13}, {1, 14}, {2, 14}, {3, 14}, {4, 14}, {6, 17}, {7, 17}, {1, 18}, {2, 18}, {1, 20}, {2, 20}, {1, 22}, {2, 22},
    {24, 26}, {25, 26}, {3, 28}, {24, 28}, {25, 28}, {3, 29}, {24, 30}, {27, 30}, {2, 31}, {9, 31}, {1, 32}, {25, 32}, {26, 32},
    {29, 32}, {3, 33}, {9, 33}, {15, 33}, {16, 33}, {19, 33}, {21, 33}, {23, 33}, {24, 33}, {30, 33}, {31, 33}, {32, 33}, {9, 34},
    {10, 34}, {14, 34}, {15, 34}, {16, 34}, {19, 34}, {20, 34}, {21, 34}, {23, 34}, {24, 34}, {27, 34}, {28, 34}, {29, 34}, {30, 34},
    {31, 34}, {32, 34}, {33, 34}};
}
