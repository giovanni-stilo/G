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
import it.stilo.g.util.ArraysUtil;
import java.util.Arrays;

import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class SubGraphByEdgesWeight implements Runnable {

    private static final Logger logger = LogManager.getLogger(SubGraphByEdgesWeight.class);

    private WeightedGraph a;
    private double minWeight;
    private WeightedGraph c;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private SubGraphByEdgesWeight(WeightedGraph a, double minWeight, WeightedGraph c, CountDownLatch cb, int chunk, int runner) {
        this.a = a;
        this.minWeight = minWeight;
        this.c = c;
        this.chunk = chunk;
        this.runner = runner;
        barrier = cb;
    }

    public void run() {
        int cand = -1;
        int [] nodes= a.getVertex();
        //System.out.println(Arrays.toString(nodes));
        for (int i = chunk; i < nodes.length; i += runner) {
            cand = nodes[i];

            if (a.out[cand] != null) {                
                for(int e=0;e<a.out[cand].length;e++){
                    int other= a.out[cand][e];
                     //System.out.println(a.weights[cand][e]);
                    if(a.weights[cand][e]>=minWeight){
                        //System.out.println(cand+"-"+other+"-"+a.weights[cand][e]);
                        c.testAndAdd(cand, other, a.weights[cand][e]);
                    }
                }
            }   
            
            if (c.out[cand] != null && c.in[cand] != null) {
                c.V[cand] = cand;
                c.vWeights[cand] = a.vWeights[cand];
            }
        }

        barrier.countDown();
    }

    private static WeightedGraph extract(final WeightedGraph a, final WeightedGraph c, final double minWeight, int runner) {

        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new SubGraphByEdgesWeight(a, minWeight, c, latch, i, runner));
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

    public static WeightedDirectedGraph extract(final WeightedDirectedGraph a, final double minWeight, int runner) {
        WeightedDirectedGraph c = new WeightedDirectedGraph(a.size);
        SubGraphByEdgesWeight.extract(a, c, minWeight, runner);
        return c;
    }

    public static WeightedUndirectedGraph extract(final WeightedUndirectedGraph a, final double minWeight, int runner) {
        WeightedUndirectedGraph c = new WeightedUndirectedGraph(a.size);
        SubGraphByEdgesWeight.extract(a, c, minWeight, runner);
        return c;
    }

    public static WeightedGraph extract(final WeightedGraph a, final double minWeight, int runner) {
        WeightedGraph c;
        if (a instanceof WeightedUndirectedGraph) {
            c = new WeightedUndirectedGraph(a.size);
        } else {
            c = new WeightedUndirectedGraph(a.size);
        }

        SubGraphByEdgesWeight.extract(a, c, minWeight, runner);
        return c;
    }

}
