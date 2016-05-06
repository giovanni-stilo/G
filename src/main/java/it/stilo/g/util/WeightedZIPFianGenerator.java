package it.stilo.g.util;

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
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stilo
 */
public class WeightedZIPFianGenerator implements Runnable {
    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(WeightedZIPFianGenerator.class);

    private WeightedDirectedGraph g;
    private CountDownLatch barrier;
    private int degree;
    private int runner;
    private int chunk;
    private ZipfDistribution dist;

    private WeightedZIPFianGenerator(WeightedDirectedGraph g, CountDownLatch barrier,ZipfDistribution dist, int degree, int chunk, int runner) {
        this.g = g;
        this.barrier = barrier;
        this.degree = degree;
        this.runner = runner;
        this.chunk = chunk;
        this.dist = dist;
    }

    public void run() {

        Random rnd = new Random(System.currentTimeMillis());

        for (int i = chunk; i < g.out.length; i += runner) {
            for (int j = 0; j < degree; j++) {
               
                while (!g.testAndAdd(i, dist.sample()-1, rnd.nextDouble()));
            }
        }
        barrier.countDown();
    }

    public static void generate(WeightedDirectedGraph g,double exponent, int degree, int runner) {

        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);

        ZipfDistribution dist=new ZipfDistribution(g.size, exponent);
        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new WeightedZIPFianGenerator(g, latch,dist, degree, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }

        logger.info(WeightedZIPFianGenerator.class.getName() + "\t" + ((System.currentTimeMillis() - time) / 1000d) + "s");
    }
}
