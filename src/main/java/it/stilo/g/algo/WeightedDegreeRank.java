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
import it.stilo.g.structures.IndexAndRank;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.util.ArraysUtil;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class WeightedDegreeRank implements Runnable {

    private static final Logger logger = LogManager.getLogger(WeightedDegreeRank.class);

    private WeightedGraph g;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;
    private int[] vertices;
    private IndexAndRank<DoubleValues> index;

    private WeightedDegreeRank(WeightedGraph g, int[] vertices, IndexAndRank<DoubleValues> index, CountDownLatch cb, int chunk, int runner) {
        this.barrier = cb;
        this.chunk = chunk;
        this.runner = runner;
        this.vertices = vertices;
        this.index = index;
        this.g = g;
    }

    public void run() {
        double wDegree = 0.0;
        for (int i = chunk; i < vertices.length; i += runner) {
            int v = vertices[i];
            DoubleValues couple = null;
            if (g.out[v] != null) {

                if (index.contains(v)) {
                    couple = index.remove(v);
                } else {
                    couple = new DoubleValues();
                    couple.index = v;
                }

                couple.value = ArraysUtil.collapse(g.weights[v]);

            } else {
                couple = new DoubleValues();
                couple.index = v;
                couple.value = 0;
            }

            index.add(v, couple);
        }

        barrier.countDown();
    }

    public static IndexAndRank<DoubleValues> buildRank(final WeightedGraph a, int runner) throws InterruptedException {
        long time = System.currentTimeMillis();
        IndexAndRank<DoubleValues> index = new IndexAndRank<DoubleValues>();
        int[] vertices = a.getVertex();

        WeightedDegreeRank.updateRank(a, vertices, index, runner);
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return index;
    }

    public static void updateRank(final WeightedGraph a, int[] vertices, IndexAndRank<DoubleValues> index, int runner) throws InterruptedException {
        //long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);
        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new WeightedDegreeRank(a, vertices, index, latch, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        //logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

}
