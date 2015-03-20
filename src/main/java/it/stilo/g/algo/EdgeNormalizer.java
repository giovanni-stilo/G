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
import it.stilo.g.structures.WeightedGraph;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class EdgeNormalizer implements Runnable {
    private static final Logger logger = LogManager.getLogger(EdgeNormalizer.class);

    private WeightedGraph g;
    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private EdgeNormalizer(WeightedGraph g, CountDownLatch cb, int chunk, int runner) {
        this.g = g;
        this.chunk = chunk;
        this.runner = runner;
        barrier = cb;
    }

    public void run() {
        for (int i = chunk; i < g.weights.length; i += runner) {
            if (g.weights[i] != null) {
                double sum = 0.0;
                for (int y = 0; y < g.weights[i].length; y++) {
                    sum += g.weights[i][y];
                }
                for (int y = 0; y < g.weights[i].length; y++) {
                    g.weights[i][y] /= sum;
                }
            }
        }

        barrier.countDown();
    }

    public static void normalize(final WeightedGraph g, int runner) {
        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new EdgeNormalizer(g, latch, i, runner));
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
}
