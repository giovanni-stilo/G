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

import it.stilo.g.util.WeightedRandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author stilo
 */
public class Clone implements Runnable {
    private static final Logger logger = LogManager.getLogger(Clone.class);

    private WeightedGraph a;
    private WeightedGraph b;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    Clone(WeightedGraph a, WeightedGraph b, CountDownLatch cb, int chunk, int runner) {
        this.a = a;
        this.b = b;
        this.chunk = chunk;
        this.runner = runner;
        barrier = cb;
    }

    public void run() {
        for (int i = chunk; i < b.size; i += runner) {
            if (a.V[i] > -1) {
                b.V[i] = a.V[i];
                b.vWeights[i] = a.vWeights[i];
                if (a.weights[i] != null)
                    b.weights[i] = Arrays.copyOf(a.weights[i], a.weights[i].length);
                if (a.in[i] != null)
                    b.in[i] = Arrays.copyOf(a.in[i], a.in[i].length);
                if (a.out[i] != null)
                    b.out[i] = Arrays.copyOf(a.out[i], a.out[i].length);
            }
        }

        barrier.countDown();
    }


    public static WeightedDirectedGraph clone(final WeightedDirectedGraph a, int runner) {
        WeightedDirectedGraph c = new WeightedDirectedGraph(a.size);
        clone(a, c, runner);
        return c;
    }

    public static WeightedUndirectedGraph clone(final WeightedUndirectedGraph a, int runner) {
        WeightedUndirectedGraph c = new WeightedUndirectedGraph(a.size);
        clone(a, c, runner);
        return c;
    }

    private static WeightedGraph clone(final WeightedGraph a, final WeightedGraph b, int runner) {

        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new Clone(a, b, latch, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        //logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return b;
    }


    public static void main(String[] args) {
        WeightedDirectedGraph g = new WeightedDirectedGraph(15);
        WeightedDirectedGraph g1, g2, g3;

        int size = 1000000;
        int degree = 2000;
        int worker = 12;
        logger.info("\t----\t" + size + "@" + degree + ":" + worker + "\t----");
        g = new WeightedDirectedGraph(size);
        WeightedRandomGenerator.generate(g, degree, worker);

        for (int i = 0; i < 100; i++) {
            g2 = Clone.clone(g, 16);
            g1 = UnionDisjoint.copy(g, 16);
        }

    }

}
