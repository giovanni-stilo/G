package it.stilo.g.structures;

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

import it.stilo.g.algo.EdgeNormalizer;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class WeightedDirectedGraph extends WeightedGraph {
    protected static final Logger logger = LogManager.getLogger(WeightedDirectedGraph.class);

    public WeightedDirectedGraph(int size) {
        V = new int[size];
        vWeights = new double[size];
        Arrays.fill(V, -1);
        out = new int[size][];
        in = new int[size][];
        weights = new double[size][];
        rows = new Object[size];
        cols = new Object[size];
        this.size = size;
        for (int i = 0; i < size; i++) {
            rows[i] = new Object();
            cols[i] = new Object();
        }
    }

    public void reset() {
        V = new int[size];
        vWeights = new double[size];
        Arrays.fill(V, -1);
        out = new int[size][];
        in = new int[size][];
        weights = new double[size][];
    }

    @Override
    public boolean testAndAdd(int a, int b, double w) {
        synchronized (rows[a]) {
            if (this.get(a, b) > -1.0) {
                return false;
            } else {
                V[a]=a;
                V[b]=b;
                add(a, b, w);
                return true;
            }
        }
    }

    @Override
    public void add(int a, int b, double w) {
        V[a]=a;
        V[b]=b;
        synchronized (rows[a]) {
            if (out[a] == null) {
                out[a] = new int[]{b};
                weights[a] = new double[]{w};
            } else {
                int[] tmpI = new int[out[a].length + 1];
                double[] tmpW = new double[weights[a].length + 1];
                int delta = 0;
                int i = 0;
                for (; i < out[a].length; i++) {
                    if (delta == 0 && b < out[a][i]) {
                        tmpI[i] = b;
                        tmpW[i] = w;
                        delta++;
                    }
                    tmpI[i + delta] = out[a][i];
                    tmpW[i + delta] = weights[a][i];
                }
                if (delta == 0) {
                    tmpI[i] = b;
                    tmpW[i] = w;
                }

                weights[a] = tmpW;
                out[a] = tmpI;
            }
        }

        synchronized (cols[b]) {
            if (in[b] == null) {
                in[b] = new int[]{a};
            } else {
                int[] tmpO = Arrays.copyOf(in[b], in[b].length + 1);
                tmpO[in[b].length] = a;
                Arrays.sort(tmpO);
                in[b] = tmpO;
            }
        }
    }

    @Override
    public double get(int a, int b) {
        synchronized (rows[a]) {
            if (out[a] == null) {
                return -1.0d;
            }
            int i = Arrays.binarySearch(out[a], b);
            return (i < 0) ? (-1.0d) : weights[a][i];

        }
    }

    @Override
    public boolean update(int a, int b, double w) {
        synchronized (rows[a]) {
            if (out[a] == null) {
                return false;
            }

            int i = Arrays.binarySearch(out[a], b);
            if(i<0)
                return false;
            weights[a][i] = w;
        }

        return true;
    }

    public void remove(int a) {
        V[a]=-1;
        synchronized (rows[a]) {
            if (out[a] != null) {
                for (int i = 0; i < out[a].length; i++) {
                    int k = out[a][i];
                    if (in[k] != null) {
                        synchronized (cols[k]) {
                            int[] tmp = new int[in[k].length];
                            for (int j = 0; j < in[k].length; j++) {
                                if (a <= in[k][j] && ((j + 1) < in[k].length)) {
                                    tmp[j] = in[k][j + 1];
                                } else {
                                    tmp[j] = in[k][j];
                                }
                            }
                            in[k] = Arrays.copyOf(tmp, tmp.length - 1);
                        }
                    }
                }
                out[a] = null;
                weights[a] = null;
            }
        }
        synchronized (cols[a]) {
            if (in[a] != null) {
                for (int i = 0; i < in[a].length; i++) {
                    int k = in[a][i];
                    if (out[k] != null) {
                        synchronized (rows[k]) {
                            int[] tmp = new int[out[k].length];
                            double[] wTmp = new double[weights[k].length];

                            for (int j = 0; j < out[k].length; j++) {
                                if (a <= out[k][j] && ((j + 1) < out[k].length)) {
                                    tmp[j] = out[k][j + 1];
                                    wTmp[j] = weights[k][j + 1];
                                } else {
                                    tmp[j] = out[k][j];
                                    wTmp[j] = weights[k][j];
                                }
                            }

                            out[k] = Arrays.copyOf(tmp, tmp.length - 1);
                            weights[k] = Arrays.copyOf(wTmp, wTmp.length - 1);
                        }
                    }
                }
                in[a] = null;
            }
        }
    }

    public static void main(String[] args) {
        WeightedDirectedGraph g = new WeightedDirectedGraph(5);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.add(0, 4, 4.674839);
        //g.add(4, 0, 4.674839);
        g.add(4, 3, 4.674839);
        //g.add(3, 4, 4.674839);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.add(0, 3, 3.5473);
        //g.add(3, 0, 3.5473);
        g.add(3, 2, 3.5473);
        //g.add(2, 3, 3.5473);

        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.add(0, 2, 2.5637829);
        //g.add(2, 0, 2.5637829);
        g.add(2, 1, 2.5637829);
        //g.add(1, 2, 2.5637829);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.add(0, 1, 1.54637892);
        //g.add(1, 0, 1.54637892);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        logger.info(g.get(0, 1));
        logger.info(g.get(0, 2));
        logger.info(g.get(0, 3));
        logger.info(g.get(0, 4));
        EdgeNormalizer.normalize(g, Runtime.getRuntime().availableProcessors());
        logger.info(g.get(0, 1));
        logger.info(g.get(0, 2));
        logger.info(g.get(0, 3));
        logger.info(g.get(0, 4));
    }
}
