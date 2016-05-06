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
import it.stilo.g.structures.LongIntDict;
import it.stilo.g.structures.WeightedGraph;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stilo
 */
public class GraphReader extends Thread {

    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(GraphReader.class);

    private static String SEP = "\t";
    private WeightedGraph g;
    private String file;
    private LongIntDict map = null;
    private CountDownLatch barrier;
    private boolean check = false;

    public GraphReader(WeightedGraph g, String file, LongIntDict map, CountDownLatch barrier) {
        this.g = g;
        this.file = file;
        this.map = map;
        this.barrier = barrier;
    }

    public GraphReader(WeightedGraph g, String file, CountDownLatch barrier) {
        this.g = g;
        this.file = file;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            if (map != null) {
                GraphReader.readGraphLong2IntRemap(g, file, map, check);
            } else {
                GraphReader.readGraph(g, file, check);
            }

            barrier.countDown();
        } catch (IOException ex) {
            Logger.getLogger(GraphReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public static void setSeparator(String separator) {
        SEP = separator;
    }

    public static void readGraphLong2IntRemap(WeightedGraph g, String filename, LongIntDict direct, boolean test) throws IOException {
        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));

        String line;
        int id0, id1;

        double w;
        String[] parts;
        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);

            id0 = direct.testAndSet(Double.valueOf(parts[0]).longValue());
            id1 = direct.testAndSet(Double.valueOf(parts[1]).longValue());

            w = 1.0;//Double.parseDouble(parts[2]);

            if (test) {
                g.testAndAdd(id0, id1, w);
            } else {
                g.add(id0, id1, w);
            }
        }

        br.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

    public static void readAndAddGraph(WeightedGraph g, String filename) throws IOException {
        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));

        String line;
        int id0, id1;

        double w, nW;
        String[] parts;
        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);

            id0 = Double.valueOf(parts[0]).intValue();//Integer.parseInt(parts[0]);
            id1 = Double.valueOf(parts[1]).intValue();//Integer.parseInt(parts[1]);

            nW = Double.parseDouble(parts[2]);

            if ((w = g.get(id0, id1)) > 0.0) {
                g.update(id0, id1, w + nW);
            } else {
                g.add(id0, id1, nW);
            }
        }

        br.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");

    }

    public static void readGraph(WeightedGraph g, String filename, boolean test) throws IOException {
        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));

        String line;
        int id0, id1;

        double w;
        String[] parts;
        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);

            id0 = Double.valueOf(parts[0]).intValue();//Integer.parseInt(parts[0]);
            id1 = Double.valueOf(parts[1]).intValue();//Integer.parseInt(parts[1]);

            if (parts.length > 2) {
                w = Double.parseDouble(parts[2]);
            } else {
                w = 1.0;
            }

            if (test) {
                g.testAndAdd(id0, id1, w);
            } else {
                g.add(id0, id1, w);
            }
        }

        br.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");

    }

    public static void readGraphReverse(WeightedGraph g, String filename, boolean test) throws IOException {
        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));

        String line;
        int id0, id1;

        double w;
        String[] parts;
        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);

            id0 = Double.valueOf(parts[0]).intValue();//Integer.parseInt(parts[0]);
            id1 = Double.valueOf(parts[1]).intValue();//Integer.parseInt(parts[1]);

            if (parts.length > 2) {
                w = Double.parseDouble(parts[2]);
            } else {
                w = 1.0;
            }

            if (test) {
                g.testAndAdd(id1, id0, w);
            } else {
                g.add(id1, id0, w);
            }
        }

        br.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
}
