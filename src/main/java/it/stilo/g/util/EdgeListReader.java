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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.stilo.g.algo.ComunityLPA;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeListReader {
    protected static final Logger logger = LogManager.getLogger(EdgeListReader.class);

    private static Pattern VERTEX_ID = Pattern.compile("\\d+");

    public static WeightedGraph readGraph(String filename, boolean isDirected,
            boolean startFromZero) throws IOException {
        WeightedGraph g;

        if (isDirected) {
            g = new WeightedDirectedGraph(getVerticesNumberFromFile(filename, startFromZero));
        } else {
            g = new WeightedUndirectedGraph(getVerticesNumberFromFile(filename, startFromZero));
        }

        String line = null;
        Matcher m;
        int i, j, lineIndex = 0;
        int[] arcVector = new int[2];

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));

        logger.info("getting edges...");
        while ((line = br.readLine()) != null) {
            m = VERTEX_ID.matcher(line);
            i = 0;
            j = 0;
            while (m.find(i) || j < 2) {
                arcVector[j] = Integer.valueOf(m.group())
                        - (startFromZero ? 0 : 1);

                i = m.end();
                j++;
            }
            EdgeListReader.add(g, arcVector[0], arcVector[1], 1.0d);
            lineIndex++;
        }

        logger.info("COMPLETE: " + lineIndex + " edges found");
        br.close();

        return g;
    }

    public static void readGZBinaryGraph(WeightedGraph g, String filename) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;
        double w = -1.0d;

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                w = dis.readDouble();
                g.testAndAdd(a, b, w);
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    
    public static void readGZNormalizeNeg2PosOneBinaryGraph(WeightedGraph g, String filename) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;
        double w = -1.0d;

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                w = dis.readDouble();
                g.testAndAdd(a, b, 2*(w-0.5d));
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    
    
    public static void readGZLogBinaryGraph(WeightedGraph g, String filename) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;
        double w = -1.0d;

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                w = dis.readDouble();
                g.testAndAdd(a, b, Math.log(w));
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    
    public static void readGZBoundedBinaryGraph(WeightedGraph g, String filename,double min, double max) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;
        double w = -1.0d;

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                w = dis.readDouble();
                if(w>=min && w<=max)
                g.testAndAdd(a, b, w);
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    
    public static void readGZBinaryResetGraph(WeightedGraph g, String filename,double weight) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;        

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                dis.readDouble();
                g.testAndAdd(a, b, weight);
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    
    public static void readGZBinarySubGraph(WeightedGraph g, String filename, int [] vertexs) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;
        double w = -1.0d;

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                w = dis.readDouble();
                if(Arrays.binarySearch(vertexs, a)>=0 && Arrays.binarySearch(vertexs, b)>=0)
                    g.testAndAdd(a, b, w);
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    
    public static void readGZBinaryResetSubGraph(WeightedGraph g, String filename, int [] vertexs, double weight) throws IOException {
        long time = System.currentTimeMillis();
        DataInputStream dis;
        int count = 0;
        int av = 0;

        int a = -1;
        int b = -1;
        //double w = 1.0d;

        dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(filename))));
        while ((av = dis.available()) != 0) {
            count++;
            try {
                a = dis.readInt();
                b = dis.readInt();
                dis.readDouble();
                if(Arrays.binarySearch(vertexs, a)>=0 && Arrays.binarySearch(vertexs, b)>=0)
                    g.testAndAdd(a, b, weight);
            } catch (java.io.EOFException eof) {
                break;
            }
        }

        dis.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }
    

    public static void writePosGZBinaryGraph(WeightedGraph g, String filename) throws IOException {
        DataOutputStream writers = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))));

        for (int i = 0; i < g.out.length; i++) {
            for (int j = 0; j < g.out[i].length; j++) {
                if (g instanceof WeightedDirectedGraph || (g instanceof WeightedUndirectedGraph && i < g.out[i][j])) {
                    if (g.weights[i][j] > 0.0) {
                        writers.writeInt(i);
                        writers.writeInt(g.out[i][j]);
                        writers.writeDouble(g.weights[i][j]);
                    }
                }
            }
        }

        writers.flush();
        writers.close();
    }

    public static void writeAllGZBinaryGraph(WeightedGraph g, String filename) throws IOException {
        DataOutputStream writers = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))));

        for (int i = 0; i < g.out.length; i++) {
            for (int j = 0; j < g.out[i].length; j++) {
                if (g instanceof WeightedDirectedGraph || (g instanceof WeightedUndirectedGraph && i < g.out[i][j])) {
                    writers.writeInt(i);
                    writers.writeInt(g.out[i][j]);
                    writers.writeDouble(g.weights[i][j]);
                }
            }
        }

        writers.flush();
        writers.close();
    }

    public static void writeResetGZBinaryGraph(WeightedGraph g, String filename, double def) throws IOException {
        DataOutputStream writers = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))));

        for (int i = 0; i < g.out.length; i++) {
            for (int j = 0; j < g.out[i].length; j++) {
                if (g instanceof WeightedDirectedGraph || (g instanceof WeightedUndirectedGraph && i < g.out[i][j])) {
                    writers.writeInt(i);
                    writers.writeInt(g.out[i][j]);
                    writers.writeDouble(def);
                }
            }
        }

        writers.flush();
        writers.close();
    }

    private static int getVerticesNumberFromFile(String filename, boolean startFromZero)
            throws IOException {
        logger.info("getting vertices number...");
        int i, j, n = 0, current;
        String line = null;
        Matcher m;

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));

        while ((line = br.readLine()) != null) {
            m = VERTEX_ID.matcher(line);
            i = 0;
            j = 0;
            while (m.find(i) || j < 2) {
                current = Integer.valueOf(m.group());
                if (current > n) {
                    n = current;
                }
                i = m.end();
                j++;
            }
        }

        br.close();
        logger.info("COMPLETE: " + (n + (startFromZero ? 1 : 0))
                + " vertices found");
        return n + (startFromZero ? 1 : 0);
    }

    private static void add(WeightedGraph g, int a, int b, double w) {
        g.testAndAdd(a, b, w);
    }

    public static void main(String[] args) throws IOException {

        // "/media/clarauso/DATA MINT/karate.txt"
        WeightedGraph g = new EdgeListReader()
                .readGraph(
                        "rauso-g.csv.gz",
                        false, true);

        int[] labels = ComunityLPA.compute(g, 0.975d, Runtime.getRuntime()
                .availableProcessors());
        int best = ComunityLPA.bestLabel(Arrays.copyOf(labels, labels.length));
        int count = 0;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == best) {
                count++;
            }
        }
        /*logger.info(Arrays.toString(g.out[0]));
        
         logger.info(Arrays.toString(g.in[0]));
        
         logger.info(Arrays.toString(labels));*/
        logger.info("Size of Biggest: " + count);

        GraphWriter gw = new GraphWriter(g, "comunity.csv");
        gw.saveComunity(best, labels);
        MemInfo.info();

    }
}
