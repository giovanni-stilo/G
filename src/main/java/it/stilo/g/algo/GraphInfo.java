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
import com.google.common.util.concurrent.AtomicDouble;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;

import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class GraphInfo implements Runnable {
    private static final Logger logger = LogManager.getLogger(GraphInfo.class);

    private WeightedGraph a;
    private AtomicDouble[] values;
    private int[] vertices;
    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private GraphInfo(WeightedGraph a, int[] vertices, AtomicDouble[] values, CountDownLatch cb, int chunk, int runner) {
        this.a = a;
        this.vertices = vertices;
        this.values = values;

        this.chunk = chunk;
        this.runner = runner;
        barrier = cb;
    }

    public void run() {
        int v = -1;
        for (int i = chunk; i < vertices.length; i += runner) {
            v = vertices[i];
            if (a.out[v] != null) {
                values[1].addAndGet(a.out[v].length); //Edges count
            }
        }
        barrier.countDown();
    }

    public static AtomicDouble[] getGraphInfo(final WeightedGraph a, int runner) {

        long time = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(runner);
        int[] vertexs = a.getVertex();

        // Info Values TODO: convert to a map?!
        // 0 number of vertices
        // 1 number of edges
        // 2 Density (E)/(V*(V-1))
        AtomicDouble[] values = new AtomicDouble[3];
        for (int i = 0; i < values.length; i++) {
            values[i] = new AtomicDouble();
        }

        values[0].set(vertexs.length);

        Thread[] workers = new Thread[runner];
        for (int i = 0; i < runner; i++) {
            workers[i] = new Thread(new GraphInfo(a, vertexs, values, latch, i, runner));
            workers[i].setName("" + i);
            workers[i].start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.debug(e);
        }

        values[0].set(vertexs.length);
        values[2].set((values[0].get()) / (values[1].get() * (values[1].get() - 1)));

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return values;
    }

    public static void main(String[] args) {
        WeightedUndirectedGraph g = new WeightedUndirectedGraph(15);

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

        AtomicDouble[] gInfo = GraphInfo.getGraphInfo(g, 8);
        logger.info(gInfo[0] + "-" + gInfo[1] + "-" + gInfo[2]);

        g.testAndAdd(10, 11, 0.1);
        g.testAndAdd(10, 12, 0.2);
        g.testAndAdd(10, 13, 0.3);
        g.testAndAdd(10, 14, 0.4);

        g.testAndAdd(11, 10, 1.0);
        g.testAndAdd(11, 12, 1.2);
        g.testAndAdd(11, 13, 1.3);
        g.testAndAdd(11, 14, 1.4);

        g.testAndAdd(12, 10, 2.0);
        g.testAndAdd(12, 11, 2.1);
        g.testAndAdd(12, 13, 2.3);
        g.testAndAdd(12, 14, 2.4);

        g.testAndAdd(13, 10, 3.0);
        g.testAndAdd(13, 11, 3.1);
        g.testAndAdd(13, 12, 3.2);
        g.testAndAdd(13, 14, 3.4);

        g.testAndAdd(14, 10, 4.0);
        g.testAndAdd(14, 11, 4.1);
        g.testAndAdd(14, 12, 4.2);
        g.testAndAdd(14, 13, 4.3);

        gInfo = GraphInfo.getGraphInfo(g, 1);
        logger.info(gInfo[0] + "-" + gInfo[1] + "-" + gInfo[2]);
    }
}
