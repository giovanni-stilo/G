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
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class WeightedUndirectedGraph extends WeightedGraph {

    protected static final Logger logger = LogManager.getLogger(WeightedUndirectedGraph.class);

    public WeightedUndirectedGraph(int size) {
        vWeights = new double[size];
        V = new int[size];
        Arrays.fill(V, -1);
        out = new int[size][];
        in = out;
        weights = new double[size][];
        rows = new Object[size];
        cols = rows;

        this.size = size;
        for (int i = 0; i < size; i++) {
            rows[i] = new Object();
        }
    }

    public void reset() {
        vWeights = new double[size];
        V = new int[size];
        Arrays.fill(V, -1);
        out = new int[size][];
        in = out;
        weights = new double[size][];
    }

    @Override
    public boolean testAndAdd(int a, int b, double w) {
        synchronized (rows[a]) {
            if (this.get(a, b) > -1.0) {
                return false;
            } else {
                V[a] = a;
                V[b] = b;
                atomicAdd(a, b, w);
                atomicAdd(b, a, w);
                return true;
            }
        }
    }

    @Override
    public void add(int a, int b, double w) {
        V[a] = a;
        V[b] = b;
        atomicAdd(a, b, w);
        atomicAdd(b, a, w);
    }

    private void atomicAdd(int a, int b, double w) {

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

    /**
     * The update operation is not totally thread safe. Should be used
     * concurrently with add operations but should be dangerous if used
     * concurrently with other update calls. Is need to be careffull to do not
     * perform two update of the same edge at the same time.
     *
     * @param a source vertex
     * @param b destiantion vertex
     * @param w new weight
     * @return true if the edge exist false otherwise.
     */
    @Override
    public boolean update(int a, int b, double w) {
        synchronized (rows[a]) {
            if (out[a] == null) {
                return false;
            }
            int i = Arrays.binarySearch(out[a], b);
            weights[a][i] = w;
        }
        synchronized (rows[b]) {
            if (out[b] == null) {
                return false;
            }
            int i = Arrays.binarySearch(out[b], a);
            weights[b][i] = w;
        }
        return true;
    }

    public void remove(int a) {
        V[a] = -1;
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
                weights[a] = null;
            }
        }
    }

    public static void main(String[] args) {
        WeightedUndirectedGraph g = new WeightedUndirectedGraph(5);

        g.testAndAdd(0, 1, 0.1);
        g.testAndAdd(0, 2, 0.2);
        g.testAndAdd(0, 3, 0.3);
        g.testAndAdd(0, 4, 0.4);

        g.testAndAdd(1, 0, 1.0);
        g.testAndAdd(1, 2, 1.2);
        g.testAndAdd(1, 3, 1.3);
        g.testAndAdd(1, 4, 1.4);

        g.testAndAdd(2, 0, 2.0);
        g.testAndAdd(2, 1, 2.1);
        g.testAndAdd(2, 3, 2.3);
        g.testAndAdd(2, 4, 2.4);

        g.testAndAdd(3, 0, 3.0);
        g.testAndAdd(3, 1, 3.1);
        g.testAndAdd(3, 2, 3.2);
        g.testAndAdd(3, 4, 3.4);

        g.testAndAdd(4, 0, 4.0);
        g.testAndAdd(4, 1, 4.1);
        g.testAndAdd(4, 2, 4.2);
        g.testAndAdd(4, 3, 4.3);

        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.remove(3);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.remove(1);
        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.remove(2);

        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.remove(0);

        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));

        g.remove(4);

        logger.info(Arrays.deepToString(g.out));
        logger.info(Arrays.deepToString(g.in));
        logger.info(Arrays.deepToString(g.weights));
    }

}
