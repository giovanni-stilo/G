/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.util;

import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedGraph;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author stilo
 */
public class SmartSerializer {

    public static void serialize(WeightedGraph g, String prefix) throws IOException {
        DataOutputStream v = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(prefix + ".V.gz")));

        marshalling(g.V, v);
        marshalling(g.vWeights, v);
        v.flush();
        v.close();

        DataOutputStream in = null;
        if (g instanceof WeightedDirectedGraph) {
            in = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(prefix + ".I.gz")));
        }

        DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(prefix + ".O.gz")));
        DataOutputStream w = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(prefix + ".W.gz")));

        for (int i = 0; i < g.out.length; i++) {
            marshalling(g.out[i], out);
            if (in != null) {
                marshalling(g.in[i], in);
            }
            marshalling(g.weights[i], w);
        }

        out.flush();
        out.close();

        in.flush();
        in.close();

        w.flush();
        w.close();
    }

    private static void marshalling(int[] array, DataOutputStream bw) throws IOException {
        if (array == null) {
            bw.writeInt(0);
        } else {
            bw.writeInt(array.length);
            for (int i = 0; i < array.length; i++) {
                bw.writeInt(array[i]);
            }
        }
    }

    private static void marshalling(double[] array, DataOutputStream bw) throws IOException {
        if (array == null) {
            bw.writeInt(0);
        } else {
            bw.writeInt(array.length);
            for (int i = 0; i < array.length; i++) {
                bw.writeDouble(array[i]);
            }
        }
    }

    public static WeightedGraph unserialize(String prefix) throws IOException {
        DataInputStream v = new DataInputStream(new GZIPInputStream(new FileInputStream(prefix + ".V.gz")));

        int[] V = unmarshallingInteger(v);
        double[] vWeights = unmarshallingDouble(v);
        WeightedDirectedGraph g = new WeightedDirectedGraph(V.length);

        v.close();
        
        g.V=V;
        g.vWeights=vWeights;

        DataInputStream in = null;
        if (true) {
            in = new DataInputStream(new GZIPInputStream(new FileInputStream(prefix + ".I.gz")));
        }
        DataInputStream out = new DataInputStream(new GZIPInputStream(new FileInputStream(prefix + ".O.gz")));
        DataInputStream w = new DataInputStream(new GZIPInputStream(new FileInputStream(prefix + ".W.gz")));

        for (int i = 0; i < g.out.length; i++) {
            g.out[i] = unmarshallingInteger(out);
            if (in != null) {
                g.in[i] = unmarshallingInteger(in);
            }
            g.weights[i] = unmarshallingDouble(w);
        }

        out.close();
        in.close();
        w.close();

        return g;
    }

    private static int[] unmarshallingInteger(DataInputStream br) throws IOException {
        int size = br.readInt();
        if (size == 0) {
            return null;
        }

        int[] array = new int[size];

        for (int i = 0; i < array.length; i++) {
            array[i] = br.readInt();
        }
        return array;
    }

    private static double[] unmarshallingDouble(DataInputStream br) throws IOException {
        int size = br.readInt();
        if (size == 0) {
            return null;
        }

        double[] array = new double[size];

        for (int i = 0; i < array.length; i++) {
            array[i] = br.readDouble();
        }
        return array;
    }
}
