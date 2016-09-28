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

import it.stilo.g.structures.WeightedDirectedGraph;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author ditommaso
 */
public class AssortativityNetwork implements Runnable {
    
    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AssortativityNetwork.class);


    private WeightedDirectedGraph g;
    private CountDownLatch barrier;
    private int runner;
    private int chunk;
    
    public static int VERTEX=6;

    public AssortativityNetwork(WeightedDirectedGraph g, CountDownLatch barrier,int chunk, int runner) {
        this.g = g;
        this.barrier = barrier;
        this.runner = runner;
        this.chunk= chunk;
    }

    public void run() {
       
        for (int i = chunk; i < input.length; i += runner) {
            g.add(input[i][0], input[i][1], 1.0d);
            //g.add(input[i][1], input[i][0], 1.0d);
        }
        barrier.countDown();
    }

    public static void generate(WeightedDirectedGraph g, int runner) {
        long time = System.currentTimeMillis();
        final  CountDownLatch latch = new CountDownLatch(runner);

        
        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new AssortativityNetwork(g,latch,i,runner));
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
    
    //private static int[][] input = {{1, 2}, {1, 3}, {2, 1}, {2, 3}, {3, 1}, {3, 2}, {4, 5}, {4, 6}, {5, 4}, {5, 6}, {6, 4},{6, 5}};  //assortativity network
    
    //private static int[][] input = {{1, 2}, {1, 3}, {2, 3}, {4, 5}, {4, 6}, {5, 6}};  //assortativity network
    
//    private static int[][] input = {{1, 2},  {4, 3}};
     
//   private static int[][] input = {{2, 4}, {1, 3}};
  //   private static int[][] input = {{1, 2}};
    
    private static int[][] input = {{1,2},{2,5},{5,1},{3,4},{4,3},{2,1}};
}
