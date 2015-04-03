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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import com.google.common.util.concurrent.AtomicDouble;

import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.util.ArraysUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Di Tommaso, Stilo
 */
public class HubnessAuthority implements Runnable {

    private static final Logger logger = LogManager.getLogger(HubnessAuthority.class);

    private WeightedGraph g;
    private int chunk;
    private int runner;
    private CountDownLatch authorityStep;
    private CountDownLatch hubnessStep;
    private CountDownLatch normalizationStep;
    private double[] hub;
    private double[] auth;
    private AtomicDouble SHub;
    private AtomicDouble SAuth;

    private HubnessAuthority(WeightedGraph g,
            CountDownLatch authority, CountDownLatch hubness, CountDownLatch normalizationStep,
            double[] countHub, double[] countAuth,
            AtomicDouble SHub, AtomicDouble SAuth,
            int chunk, int runner) {
        this.g = g;
        this.chunk = chunk;
        this.runner = runner;
        this.authorityStep = authority;
        this.hubnessStep = hubness;
        this.normalizationStep = normalizationStep;
        this.hub = countHub;
        this.auth = countAuth;
        this.SHub = SHub;
        this.SAuth = SAuth;
    }

    public void run() {
        {//Compute Authority score part ( inlink )
            double sumAuth = 0.0d;
            for (int j = chunk; j < g.size; j += runner) {
                auth[j] = 0.0;
                if (g.in[j] != null) {
                    double authScore = 0.0;
                    for (int p = 0; p < g.in[j].length; p++) {
                        int i = g.in[j][p];
                        authScore += g.get(i, j) * hub[i];
                    }
                    auth[j] = authScore;
                    sumAuth += Math.pow(authScore, 2);
                }
            }
            SAuth.addAndGet(sumAuth);

            authorityStep.countDown(); //Signal to other threads
            try {
                authorityStep.await(); //Wait that every thread have finished Authority part.
            } catch (InterruptedException ex) {
                logger.debug(ex);
            }
        }
        

        {//Compute Hubness score part ( inlink )
            double sumHub = 0.0d;

            for (int j = chunk; j < g.size; j += runner) {
                hub[j] = 0.0;
                if (g.out[j] != null) {
                    double hubScore = 0.0;
                    for (int p = 0; p < g.out[j].length; p++) {
                        int i = g.out[j][p];
                        hubScore += g.get(j, i) * auth[i];
                    }
                    hub[j] = hubScore;
                    sumHub += Math.pow(hubScore, 2);
                }
            }
            SHub.addAndGet(sumHub);

            hubnessStep.countDown(); //Signal to other threads
            try {
                hubnessStep.await(); //Wait that every thread have finished Authority part.
            } catch (InterruptedException ex) {
                logger.debug(ex);
            }
        }

        {// Normalization phase.
            double normAuth = Math.sqrt(SAuth.doubleValue());
            double normHub = Math.sqrt(SHub.doubleValue());

            for (int j = chunk; j < g.size; j += runner) {
                if (g.in[j] != null || g.out[j] != null) {
                    auth[j] = auth[j] / normAuth;
                    hub[j] = hub[j] / normHub;
                }
            }

            normalizationStep.countDown();
        }
    }

    public static ArrayList<ArrayList<DoubleValues>> compute(final WeightedGraph g, double sigma, int runner) {
        long time = System.currentTimeMillis();
        double[] hub = new double[g.size];
        double[] auth = new double[g.size];
        double[] oldAuth;

        double iV = 1; //Hubness & Authority scores initialized by dafault to 1.

        for (int i = 0; i < g.size; i++) {
            if (g.in[i] != null || g.out[i] != null) {
                auth[i] = iV;
                hub[i] = iV;
            }
        }

        //Shared accoumulator for normalization phase.
        AtomicDouble SAuth = new AtomicDouble(0.0);
        AtomicDouble SHub = new AtomicDouble(0.0);

        do {
            SAuth.set(0.0);
            SHub.set(0.0);
            CountDownLatch authority = new CountDownLatch(runner);
            CountDownLatch hubness = new CountDownLatch(runner);
            CountDownLatch normalizationStep = new CountDownLatch(runner);

            oldAuth = Arrays.copyOf(auth, auth.length);

            Thread[] workers = new Thread[runner];
            for (int i = 0; i < runner; i++) {
                workers[i] = new Thread(new HubnessAuthority(g, authority, hubness, normalizationStep, hub, auth, SHub, SAuth, i, runner),"" + i);
                workers[i].start();
            }

            try {
                normalizationStep.await();
            } catch (InterruptedException e) {
                logger.debug(e);
            }

        } while (ArraysUtil.L1(ArraysUtil.sub(oldAuth, auth)) > sigma); //Repeat until reach the desiderd precision.

        ArrayList<DoubleValues> listAuth = new ArrayList<DoubleValues>();
        ArrayList<DoubleValues> listHub = new ArrayList<DoubleValues>();

        //Create ranking lists for Hubness & Authority
        for (int i = 0; i < auth.length; i++) {
            if (g.in[i] != null || g.out[i] != null) {
                listAuth.add(new DoubleValues(i, auth[i]));
                listHub.add(new DoubleValues(i, hub[i]));
            }
        }

        Collections.sort(listAuth);
        Collections.sort(listHub);

        ArrayList<ArrayList<DoubleValues>> list = new ArrayList<ArrayList<DoubleValues>>();
        list.add(new ArrayList(listAuth));
        list.add(new ArrayList(listHub));

        logger.trace(((System.currentTimeMillis() - time) / 1000d) + "s");
        return list;
    }
}
