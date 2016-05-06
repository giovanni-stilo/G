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
import gnu.trove.set.hash.TIntHashSet;
import it.stilo.g.structures.WeightedGraph;
import java.util.LinkedList;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Stilo
 */
public class ReachabilityScore implements Runnable {

    private static final Logger logger = LogManager.getLogger(ReachabilityScore.class);

    private WeightedGraph g;

    private AtomicInteger cg;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private TIntHashSet visited = new TIntHashSet();
    private LinkedList<Integer> queue = new LinkedList<Integer>();

    private ReachabilityScore(WeightedGraph a, AtomicInteger cg, CountDownLatch cb, int chunk, int runner) {
        this.g = a;
        this.cg = cg;
        this.chunk = chunk;
        this.runner = runner;
        this.barrier = cb;
    }

    //compute reachability for each node
    public void run() {
        int localCounter=0;
        for (int i = chunk; i < g.out.length; i += runner) {
            if (g.out[i] != null) {
                queue.add(i);
                visited.add(i);
                localCounter+=this.explore();
                visited.clear();
            }
        }
        cg.getAndAdd(localCounter);
        barrier.countDown();
    }

    private int explore() {
        Integer node = null;
        int count = 0;
        while ((node = queue.poll()) != null) {           
            if (g.out[node] != null) {
                for (int i = 0; i < g.out[node].length; i++) {
                    int v = g.out[node][i];
                    if (!visited.contains(v)) {
                        count++;
                        queue.add(v);
                        visited.add(v);
                    }
                }
            }
        }
        return count;
    }

    public static int compute(final WeightedGraph a, int runner) throws InterruptedException {
        long time = System.currentTimeMillis();

        AtomicInteger cg = new AtomicInteger(0);

        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new ReachabilityScore(a, cg, latch, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");

        return cg.get();
    }

}
