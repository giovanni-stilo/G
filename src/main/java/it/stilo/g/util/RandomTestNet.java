package it.stilo.g.util;

/*
 * #%L
 * WeNet
 * %%
 * Copyright (C) 2013 - 2015 Dipartimento Informatica - Sapienza Univerit???? di Roma
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

import java.util.concurrent.CountDownLatch;


import it.stilo.g.structures.WeightedDirectedGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Di Tommaso, Stilo
 */
public class RandomTestNet implements Runnable {
    private static final Logger logger = LogManager.getLogger(RandomTestNet.class);

    private WeightedDirectedGraph g;
    private CountDownLatch barrier;
    private int runner;
    private int chunk;
    private  double [][]  input;
    

    public RandomTestNet(WeightedDirectedGraph g, double [][] input, CountDownLatch barrier,int chunk, int runner) {
        this.g = g;
        this.input = input;
        this.barrier = barrier;
        this.runner = runner;
        this.chunk= chunk;
    }

    public void run() {
       
        for (int i = chunk; i < input.length; i += runner) {
            g.add((int)input[i][0], (int)input[i][1], input[i][2]);
        }
        barrier.countDown();
    }

    public static void generate(WeightedDirectedGraph g,double [][] input, int runner) {
        long time = System.currentTimeMillis();
        final  CountDownLatch latch = new CountDownLatch(runner);

        
        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new RandomTestNet(g,input,latch,i,runner));
            workers[i].setName("" + i);
            workers[i].start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        
        logger.trace(((System.currentTimeMillis() - time) / 1000d) + "s");

    }
  
    //Random network not-weighted
    public static final double [][] RANDOM_UNWEIGHTED = { {2, 28, 1.0d}, {2, 29, 1.0d}, {3, 5, 1.0d}, 
    	  {3, 24, 1.0d}, {3, 25, 1.0d}, {3,30, 1.0d}, {4, 3, 1.0d}, {4, 16, 1.0d},
    	  {4, 17, 1.0d}, {6, 2, 1.0d}, {6, 4, 1.0d}, {6, 27, 1.0d}, {7, 16, 1.0d}, 
    	  {8, 6, 1.0d}, {8, 25, 1.0d}, {10, 21, 1.0d}, {11, 4,  1.0d}, {11, 14, 1.0d},
    	  {11, 29, 1.0d}, {12, 3, 1.0d}, {12, 4, 1.0d}, {13, 17, 1.0d}, {13, 19, 1.0d},
    	  {14, 1, 1.0d}, {14, 8, 1.0d}, {14, 9, 1.0d}, {14, 25, 1.0d}, {14, 26, 1.0d},
    	  {15, 29, 1.0d}, {17, 10, 1.0d}, {18, 5, 1.0d}, {18, 17, 1.0d}, {18, 19, 1.0d},
    	  {18, 21, 1.0d}, {18, 23, 1.0d}, {18, 24, 1.0d}, {19, 9,  1.0d}, {19, 30, 1.0d}, 
    	  {20, 1, 1.0d}, {20, 17,  1.0d}, {20, 22, 1.0d}, {20, 25, 1.0d}, {22, 1, 1.0d},
    	  {22, 5, 1.0d}, {23, 9, 1.0d}, {24, 5, 1.0d}, {24, 9, 1.0d}, {25, 6, 1.0d}, 
    	  {26, 25, 1.0d}, {26, 29,  1.0d}, {27, 2, 1.0d}, {27, 16, 1.0d}, {27, 24, 1.0d},
    	  {28, 4, 1.0d}, {28, 6, 1.0d}, {28, 7, 1.0d},  {28, 8,  1.0d}, {28, 10, 1.0d}, 
    	  {29, 13, 1.0d}, {29, 17, 1.0d}, {29, 23, 1.0d}, {30, 24, 1.0d}}; 
    
      //Random network weighted
      public static double [][] RANDOM_WEIGHTED = { {2, 28, 0.08666582}, {2, 29, 0.50531118}, {3, 5, 0.64478008}, 
    	  {3, 24, 0.80950699}, {3, 25, 0.93522339}, {3,30, 0.35309659}, {4, 3, 0.60826943}, {4, 16, 0.16427433},
    	  {4, 17, 0.93032858}, {6, 2, 0.64361618}, {6, 4, 0.98338696}, {6, 27, 0.70752913}, {7, 16, 0.01167409}, 
    	  {8, 6, 0.27271794}, {8, 25, 0.11243930}, {10, 21, 0.48066853}, {11, 4,  0.91758324}, {11, 14, 0.09473025},
    	  {11, 29, 0.05438931}, {12, 3, 0.01867017}, {12, 4, 0.73747133}, {13, 17, 0.61326110}, {13, 19, 0.24528475},
    	  {14, 1, 0.12361086}, {14, 8, 0.76826079}, {14, 9, 0.49276153}, {14, 25, 0.46964070}, {14, 26, 0.90490903},
    	  {15, 29, 0.60388358}, {17, 10, 0.13577555}, {18, 5, 0.33369651}, {18, 17, 0.72101485}, {18, 19, 0.18025353},
    	  {18, 21, 0.48021990}, {18, 23, 0.15670840}, {18, 24, 0.37792664}, {19, 9,  0.54652785}, {19, 30, 0.28404355}, 
    	  {20, 1, 0.73775734}, {20, 17,  0.59292669}, {20, 22, 0.02767050}, {20, 25, 0.45307287}, {22, 1, 0.28333919},
    	  {22, 5, 0.36074459}, {23, 9, 0.94478951}, {24, 5, 0.65462773}, {24, 9, 0.36255214}, {25, 6, 0.62638332}, 
    	  {26, 25, 0.09048140}, {26, 29,  0.51803761}, {27, 2, 0.30807095}, {27, 16, 0.54454067}, {27, 24, 0.94662356},
    	  {28, 4, 0.08871457}, {28, 6, 0.83977137}, {28, 7, 0.58058719},  {28, 8,  0.64532430}, {28, 10, 0.46726933}, 
    	  {29, 13, 0.91709221}, {29, 17, 0.14142187}, {29, 23, 0.54632198}, {30, 24, 0.08285320}}; 
}
