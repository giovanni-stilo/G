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

import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.util.Arrays;

import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class UnionDisjoint implements Runnable {
    private static final Logger logger = LogManager.getLogger(UnionDisjoint.class);
   
    private WeightedGraph a;
    private WeightedGraph b;
    private WeightedGraph c;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;
    
    private UnionDisjoint(WeightedGraph a,WeightedGraph b,WeightedGraph c, CountDownLatch cb, int chunk, int runner) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.chunk=chunk;
        this.runner=runner;
        barrier = cb;
    }
    
    public void run() {
        if(b!=null){
            for (int i = chunk; i < c.size; i+=runner) {
                    if(a.V[i]>-1){c.V[i]=a.V[i];}
                    if(b.V[i]>-1){c.V[i]=b.V[i];}
                    if(a.weights[i]!=null){c.weights[i]=a.weights[i];}
                    if(b.weights[i]!=null){c.weights[i]=b.weights[i];} 
                    if(a.out[i]!=null){c.out[i]=a.out[i];}
                    if(b.out[i]!=null){c.out[i]=b.out[i];}
                    if(a.in[i]!=null){c.in[i]=a.in[i];}
                    if(b.in[i]!=null){c.in[i]=b.in[i];}
            }
        }        
        else{
            for (int i = chunk; i < c.size; i+=runner) {
                if(a.V[i]>-1){c.V[i]=a.V[i];}
                if(a.weights[i]!=null){c.weights[i]=a.weights[i];}                
                if(a.out[i]!=null){c.out[i]=a.out[i];}                
                if(a.in[i]!=null){c.in[i]=a.in[i];}                
            }
        }
        
        barrier.countDown();
    }

    public static WeightedDirectedGraph _merge(final WeightedGraph a,final WeightedGraph b, int runner) {
        WeightedDirectedGraph c = new WeightedDirectedGraph(a.size);        
        
        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new UnionDisjoint(a,b,c, latch, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return c;
    }
    
    public static WeightedDirectedGraph copy(final WeightedDirectedGraph a, int runner) {
        WeightedDirectedGraph c = new WeightedDirectedGraph(a.size);
        copy(a,c,runner);
        return c;
    }
    
    public static WeightedUndirectedGraph copy(final WeightedUndirectedGraph a, int runner) {
        WeightedUndirectedGraph c = new WeightedUndirectedGraph(a.size);
        copy(a,c,runner);
        return c;
    }
    
    private static WeightedGraph copy(final WeightedGraph a,final WeightedGraph c, int runner) {               
        
        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new Clone(a,c, latch, i, runner));
            //workers[i] = new Thread(new UnionDisjoint(a,null,c, latch, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return c;
    }
    
    
    public static void main(String [] args){
        WeightedUndirectedGraph g = new WeightedUndirectedGraph(15);
        WeightedUndirectedGraph g1;
        
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
        
        g1=UnionDisjoint.copy(g, 4);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));
        
        logger.info(Arrays.deepToString(g1.out));
        logger.info(Arrays.deepToString(g1.in));
        logger.info(Arrays.deepToString(g1.weights));
        
        g1.testAndAdd(10, 11, 0.1);
        g1.testAndAdd(10, 12, 0.2);
        g1.testAndAdd(10, 13, 0.3);
        g1.testAndAdd(10, 14, 0.4);

        g1.testAndAdd(11, 10, 1.0);
        g1.testAndAdd(11, 12, 1.2);
        g1.testAndAdd(11, 13, 1.3);
        g1.testAndAdd(11, 14, 1.4);

        g1.testAndAdd(12, 10, 2.0);
        g1.testAndAdd(12, 11, 2.1);
        g1.testAndAdd(12, 13, 2.3);
        g1.testAndAdd(12, 14, 2.4);

        g1.testAndAdd(13, 10, 3.0);
        g1.testAndAdd(13, 11, 3.1);
        g1.testAndAdd(13, 12, 3.2);
        g1.testAndAdd(13, 14, 3.4);

        g1.testAndAdd(14, 10, 4.0);
        g1.testAndAdd(14, 11, 4.1);
        g1.testAndAdd(14, 12, 4.2);
        g1.testAndAdd(14, 13, 4.3);
        
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));
        
        logger.info(Arrays.deepToString(g1.out));
        logger.info(Arrays.deepToString(g1.in));
        logger.info(Arrays.deepToString(g1.weights));
    }
}
