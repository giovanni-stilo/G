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

import it.stilo.g.structures.WeightedGraph;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stilo
 */
public class WeightedRandomGenerator implements Runnable {
    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(WeightedRandomGenerator.class);

    private WeightedGraph g;
    private CountDownLatch barrier;
    private int degree;
    private int runner;

    private WeightedRandomGenerator(WeightedGraph g, CountDownLatch barrier,int degree,int runner) {
        this.g = g;
        this.barrier = barrier;
        this.degree=degree;
        this.runner=runner;
    }

    public void run() {
        int fact=1;
        int size = g.size / (runner*degree);

        Random rnd = new Random(System.currentTimeMillis());
        int a = 0;
        int b = 0;
        for (int x = 0; x < degree * size; x++) {
            while (((a = rnd.nextInt(g.size/fact))!=(b = rnd.nextInt(g.size/fact)))&&(!g.testAndAdd(a, b,rnd.nextDouble())));
        }
        barrier.countDown();
    }
         
    public static void generate(WeightedGraph g,int degree, int runner) {
        
        long time = System.currentTimeMillis();
        final  CountDownLatch latch = new CountDownLatch(runner);

        
        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new WeightedRandomGenerator(g,latch,degree,runner));
            workers[i].setName("" + i);
             workers[i].start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }
        
        logger.info(WeightedRandomGenerator.class.getName()+"\t"+((System.currentTimeMillis() - time) / 1000d) + "s");
    }
}
