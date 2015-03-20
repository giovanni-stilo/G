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

import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class PageRankRW implements Runnable {
    private static final Logger logger = LogManager.getLogger(PageRankRW.class);

    private Random rnd;
    private WeightedGraph g;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private int[] counters;
    private int realSize;

    private int walks;
    private int paths;

    private double beta = 0.85;

    private PageRankRW(WeightedGraph g, CountDownLatch cb, int[] counters, int chunk, int runner, int realSize, double beta) {
        this.g = g;
        this.chunk = chunk;
        this.runner = runner;
        this.barrier = cb;
        this.counters = counters;
        this.walks = (int) ((10 * Math.sqrt(realSize)) / runner) + 1;
        this.paths = (int) (5 * Math.sqrt(realSize)) + 1;
        this.realSize = realSize;
        this.beta = beta;
    }

    public void run() {
        rnd = new Random(System.currentTimeMillis());
        int v = -1;

        for (int iter = 0; iter < walks*paths; ) {
            if (v == -1) {
                v=rnd.nextInt(g.size);
            }else
                iter++;
            v = walk(v);
        }

        barrier.countDown();
    }

    private int walk(int v) {
        if (g.out[v] != null || g.in[v] != null) {
            synchronized (this.counters) {
                this.counters[v]++;
            }
            if (g.out[v] == null || rnd.nextDouble() > beta) {
                return rnd.nextInt(g.size);
            } else {
                return g.out[v][rnd.nextInt(g.out[v].length)];
            }
        } else {
            return -1;
        }
    }

    public static ArrayList<DoubleValues> compute(final WeightedGraph g, double beta, int runner) {
        int[] counters = new int[g.size];
        int realSize = 0;
        for (int i = 0; i < g.size; i++) {
            if (g.in[i] != null || g.out[i] != null) {
                counters[i]++;
                realSize++;
            }
        }

        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new PageRankRW(g, latch, counters, i, runner, realSize, beta));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }

        double sum = 0;

        ArrayList<DoubleValues> list = new ArrayList<DoubleValues>(g.size);
        for (int i = 0; i < counters.length; i++) {
            sum += counters[i];
        }
        for (int i = 0; i < counters.length; i++) {
            list.add(new DoubleValues(i, counters[i] / sum));
        }
        Collections.sort(list);

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return list;
    }
}
