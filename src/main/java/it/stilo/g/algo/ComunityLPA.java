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

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class ComunityLPA implements Runnable {

    private static final Logger logger = LogManager.getLogger(ComunityLPA.class);

    private static Random rnd;
    private WeightedGraph g;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private int[] labels;
    private int[] list = null;

    private ComunityLPA(WeightedGraph g, CountDownLatch cb, int[] labels, int chunk, int runner) {
        this.g = g;
        this.runner = runner;
        this.barrier = cb;
        this.labels = labels;
        this.chunk = chunk;
    }

    private boolean initList() {
        if (list == null) {
            // Partitioning over worker
            list = new int[(g.in.length / runner) + runner];

            int j = 0;

            for (int i = chunk; i < g.in.length; i += runner) {

                if (g.in[i] != null) {
                    labels[i] = i;
                    list[j] = i;
                    j++;
                } else {
                    if (g.out[i] != null) {
                        labels[i] = i;
                    } else {
                        labels[i] = -1;
                    }
                }
            }
            list = Arrays.copyOf(list, j);

            //Shuffle
            for (int i = 0; i < list.length; i++) {
                for (int z = 0; z < 10; z++) {
                    int randomPosition = rnd.nextInt(list.length);
                    int temp = list[i];
                    list[i] = list[randomPosition];
                    list[randomPosition] = temp;
                }
            }

            return true;
        }
        return false;
    }

    public void run() {
        if (!initList()) {
            for (int i = 0; i < list.length; i++) {
                int[] near = g.in[list[i]];
                int[] nearLabs = new int[near.length];
                for (int x = 0; x < near.length; x++) {
                    nearLabs[x] = labels[near[x]];
                }
                labels[list[i]] = bestLabel(nearLabs);
            }
        }
        barrier.countDown();
    }

    public static int bestLabel(int[] neighborhood) {
        Arrays.sort(neighborhood);
        int best = -1;
        int maxCount = -1;
        int counter = 0;
        int last = -1;
        for (int i = 0; i < neighborhood.length; i++) {
            if (maxCount > (neighborhood.length - i)) {
                break;
            }

            if (neighborhood[i] == last) {
                counter++;
                if (counter > maxCount) {
                    maxCount = counter;
                    best = last;
                }
            } else {
                counter = 0;
                last = neighborhood[i];
            }
        }

        if (maxCount <= 1) {
            return neighborhood[rnd.nextInt(neighborhood.length)];
        }
        return best;
    }

    public static int[] compute(final WeightedGraph g, double threshold, int runner) {

        ComunityLPA.rnd = new Random(System.currentTimeMillis());

        int[] labels = new int[g.size];
        int[] newLabels = labels;
        int iter = 0;

        long time = System.nanoTime();
        CountDownLatch latch = null;

        ComunityLPA[] runners = new ComunityLPA[runner];

        for (int i = 0; i < runner; i++) {
            runners[i] = new ComunityLPA(g, latch, labels, i, runner);
        }

        ExecutorService ex = Executors.newFixedThreadPool(runner);

        do {
            iter++;
            labels = newLabels;
            newLabels = Arrays.copyOf(labels, labels.length);
            latch = new CountDownLatch(runner);

            //Label Propagation
            for (int i = 0; i < runner; i++) {
                runners[i].barrier = latch;
                runners[i].labels = newLabels;
                ex.submit(runners[i]);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.debug(e);
            }

        } while (smoothEnd(labels, newLabels, iter, threshold));

        ex.shutdown();

        logger.info(((System.nanoTime() - time) / 1000000000d) + "\ts");
        return labels;
    }

    private static boolean smoothEnd(int[] labels, int[] newLabels, int iter, double threshold) {
        if (iter < 2) {
            return true;
        }

        int k = 3;

        if (iter > k) {
            int equality = 0;

            for (int i = 0; i < labels.length; i++) {
                if (labels[i] == newLabels[i]) {
                    equality++;
                }
            }
            double currentT = (equality / ((double) labels.length));

            return !(currentT >= threshold);
        }
        return !Arrays.equals(labels, newLabels);
    }
}
