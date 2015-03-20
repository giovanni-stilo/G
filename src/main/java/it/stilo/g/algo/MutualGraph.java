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
public class MutualGraph implements Runnable {
    private static final Logger logger = LogManager.getLogger(MutualGraph.class);

    private WeightedGraph a;
    private int[] nodes;
    private WeightedGraph c;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private MutualGraph(WeightedGraph a, int[] nodes, WeightedGraph c, CountDownLatch cb, int chunk, int runner) {
        this.a = a;
        this.nodes = nodes;
        this.c = c;
        this.chunk = chunk;
        this.runner = runner;
        barrier = cb;
    }

    public void run() {
        int cand = -1;
        for (int i = chunk; i < nodes.length; i += runner) {
            cand = nodes[i];

            if (a.out[cand] != null && a.in[cand] != null) {
                int[] mutuals = ArraysUtil.intersection(a.out[cand], a.in[cand]);
                if (mutuals != null && mutuals.length > 0) {
                    c.V[cand] = cand;
                    c.vWeights[cand] = a.vWeights[cand];
                    c.out[cand] = mutuals;
                    c.in[cand] = Arrays.copyOf(mutuals, mutuals.length);

                    c.weights[cand] = new double[c.out[cand].length];

                    for (int j = 0; j < c.out[cand].length; j++) {
                        double oW = a.get(cand, c.out[cand][j]);
                        double iW = a.get(c.out[cand][j], cand);
                        c.weights[cand][j] = oW < iW ? oW : iW;
                    }
                }
            }
        }

        barrier.countDown();
    }

    private static WeightedGraph extract(final WeightedGraph a, final WeightedGraph c, int runner) {

        long time = System.currentTimeMillis();
        int[] nodes = a.getVertex();
        Arrays.sort(nodes);
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new MutualGraph(a, nodes, c, latch, i, runner));
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

    public static WeightedUndirectedGraph extract(final WeightedUndirectedGraph a, int runner) {
        WeightedUndirectedGraph c = new WeightedUndirectedGraph(a.size);
        MutualGraph.extract(a, c, runner);
        return c;
    }
    
    public static WeightedDirectedGraph extract(final WeightedDirectedGraph a, int runner) {
        WeightedDirectedGraph c = new WeightedDirectedGraph(a.size);
        MutualGraph.extract(a, c, runner);
        return c;
    }

    public static void main(String[] args) {
        int worker = (int) (Runtime.getRuntime().availableProcessors());

        /*WeightedDirectedGraph g = new WeightedDirectedGraph(ZacharyNetwork.VERTEX);
         ZacharyNetwork.generate(g, worker);*/
        WeightedDirectedGraph g = new WeightedDirectedGraph(5);

        g.testAndAdd(0, 2, 0.2);
        g.testAndAdd(0, 3, 0.3);

        g.testAndAdd(1, 0, 1.0);

        g.testAndAdd(1, 4, 1.4);

        g.testAndAdd(2, 0, 2.0);
        g.testAndAdd(2, 1, 2.1);
        g.testAndAdd(2, 3, 2.3);
        g.testAndAdd(2, 4, 2.4);

        g.testAndAdd(3, 2, 3.2);
        g.testAndAdd(3, 4, 3.4);

        g.testAndAdd(4, 0, 4.0);
        g.testAndAdd(4, 1, 4.1);

        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        WeightedDirectedGraph g1 = MutualGraph.extract(g, worker);
        logger.info(Arrays.deepToString(g1.out));
        logger.info(Arrays.deepToString(g1.in));
        logger.info(Arrays.deepToString(g1.weights));

    }

}
