package it.stilo.g.algo;

/*
 * #%L
 * G
 * %%
 * Copyright (C) 2013 - 2015 Giovanni Stilo
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

import com.google.common.util.concurrent.AtomicDouble;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.util.ArraysUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class PageRankPI implements Runnable {
    private static final Logger logger = LogManager.getLogger(PageRankPI.class);

    private WeightedGraph g;

    private int chunk;
    private int runner;
    private CountDownLatch firstStep;
    private CountDownLatch secondStep;
    private CountDownLatch thirdStep;

    private double[] ranks;

    private double beta = 0.85d;
    private int realSize = 0;

    private AtomicDouble S;

    private PageRankPI(WeightedGraph g, CountDownLatch first, CountDownLatch second,CountDownLatch thirdStep, double[] counters, double beta, int realSize, AtomicDouble S, int chunk, int runner) {
        this.g = g;
        this.chunk = chunk;
        this.runner = runner;
        this.firstStep = first;
        this.secondStep = second;
        this.thirdStep = thirdStep;
        this.ranks = counters;
        this.beta = beta;
        this.realSize = realSize;
        this.S = S;
    }

    @Override
    public void run() {
        for (int j = chunk; j < g.size; j += runner) {
            if (g.in[j] == null) {
                ranks[j] = 0.0;
            } else {
                double tRank = 0.0;
                for (int p = 0; p < g.in[j].length; p++) {
                    int i = g.in[j][p];
                    tRank += (beta * (ranks[i] /g.out[i].length));
                }
                ranks[j] = tRank;
            }
        }
        
        firstStep.countDown();
        try {
            firstStep.await();
        } catch (InterruptedException ex) {
            logger.debug(ex);
        }

        for (int j = chunk; j < g.size; j += runner) {
            if (g.in[j] != null || g.out[j] != null) {
                S.addAndGet(ranks[j]);
            }
        }
       
         
        secondStep.countDown();
        try {
            secondStep.await();
        } catch (InterruptedException ex) {
            logger.debug(ex);
        }
       
        for (int j = chunk; j < g.size; j += runner) {
            if (g.in[j] != null || g.out[j] != null) {
                ranks[j] = ranks[j] + ((1 - S.doubleValue()) / realSize);
            }
        }
        thirdStep.countDown();
    }

    public static ArrayList<DoubleValues> compute(final WeightedGraph g, double beta,double sigma, int runner) {
        long time = System.currentTimeMillis();
        double[] ranks = new double[g.size];
        double[] oldRanks ;
        int realSize = 0;
        for (int i = 0; i < g.size; i++) {
            if (g.in[i] != null || g.out[i] != null) {
                realSize++;
            }
        }

        double iV = 1d / (double) realSize;

        for (int i = 0; i < g.size; i++) {
            if (g.in[i] != null || g.out[i] != null) {
                ranks[i] = iV;
            }
        }
        
        double diff=0;
        
        do  {
            AtomicDouble S = new AtomicDouble(0.0);
            CountDownLatch first = new CountDownLatch(runner);
            CountDownLatch second = new CountDownLatch(runner);
            CountDownLatch third = new CountDownLatch(runner);
            
            oldRanks=Arrays.copyOf(ranks, ranks.length);

            Thread[] workers = new Thread[runner];
            for (int i = 0; i < runner; i++) {
                workers[i] = new Thread(new PageRankPI(g, first, second,third, ranks, beta, realSize, S, i, runner));
                workers[i].setName("" + i);
                workers[i].start();
            }
            try {
                third.await();
            } catch (InterruptedException e) {
                logger.debug(e);
            }
            
        }while(ArraysUtil.L1(ArraysUtil.sub(oldRanks, ranks))>sigma);

        ArrayList<DoubleValues> list = new ArrayList<DoubleValues>(realSize);
        for (int i = 0; i < ranks.length; i++) {
            if (g.in[i] != null || g.out[i] != null) {
                list.add(new DoubleValues(i, ranks[i]));
            }
        }
        Collections.sort(list);

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return list;
    }
}
