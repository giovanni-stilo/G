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
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TIntLongMap;
import it.stilo.g.structures.LongIntDict;
import it.stilo.g.structures.WeightedGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stilo
 */
public class GraphWriter {

    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(GraphWriter.class);

    private static String SEP = "\t";

    private WeightedGraph g;
    private PrintWriter writer;
    private String output;

    public GraphWriter(WeightedGraph g, String out) {
        this.g = g;
        this.output = out;
    }

    private void prepare() throws IOException {
        File file = new File(output);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        writer = new PrintWriter(bw);
    }

    private void close() {
        writer.flush();
        writer.close();
    }

    public void save() throws IOException {
        prepare();
        for (int i = 0; i < g.out.length; i++) {
            if (g.out[i] != null) {
                for (int y = 0; y < g.out[i].length; y++) {
                    writer.println((i) + "," + (g.out[i][y]));
                }
            }
            writer.flush();
        }

        close();
    }

    public void saveAdiacence() throws IOException {
        prepare();
        for (int i = 0; i < g.out.length; i++) {
            if (g.out[i] != null) {
                writer.print(i);
                for (int y = 0; y < g.out[i].length; y++) {
                    writer.print(" " + g.out[i][y]);
                }
                writer.print("\n");
                writer.flush();
            }

        }

        close();
    }

    public void saveComunity(int label, int[] labels) throws IOException {
        prepare();
        for (int i = 0; i < g.out.length; i++) {
            if (labels[i] == label && g.out[i] != null) {
                for (int y = 0; y < g.out[i].length; y++) {
                    if (labels[g.out[i][y]] == label) {
                        writer.println(i + "," + g.out[i][y]);
                    }
                }
            }
        }

        close();
    }

    public static void saveDirectGraph(WeightedGraph g, String outputGraph, TIntLongMap decoder) throws IOException {

        long time = System.currentTimeMillis();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputGraph))));

        String newLine = "";

        int[] vertices = g.getVertex();

        for (int i = 0; i < vertices.length; i++) {
            int v = vertices[i];
            if (g.out[v] != null) {
                for (int j = 0; j < g.out[v].length; j++) {
                    int d = g.out[v][j];
                    double w = g.weights[v][j];
                    if (decoder != null) {
                        newLine = "" + decoder.get(v) + SEP + decoder.get(d) + SEP + w;
                    } else {
                        newLine = "" + v + SEP + d + SEP + w;
                    }

                    bw.write(newLine);
                    bw.newLine();
                }
            }
        }

        bw.flush();
        bw.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

    public static void saveLongIntDictionary(LongIntDict dict, String fileName) throws IOException {
        long time = System.currentTimeMillis();
        TLongIntIterator iter = dict.getIterator();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(fileName))));
        String newLine = "";
        while (iter.hasNext()) {
            iter.advance();
            newLine = "" + iter.key() + "\t" + iter.value();
            bw.write(newLine);
            bw.newLine();
        }
        bw.flush();
        bw.close();
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

}
