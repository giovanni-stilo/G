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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class ConnectedComponents implements Runnable {

    private static final Logger logger = LogManager.getLogger(ConnectedComponents.class);

    private WeightedGraph a;
    private int[] nodes;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private LinkedBlockingQueue<Integer> que;
    private Set<Integer> done;
    private Set<Integer> allDone;

    private ConnectedComponents(WeightedGraph a, int[] nodes, CountDownLatch cb, int chunk, int runner, Set<Integer> done, LinkedBlockingQueue<Integer> que, Set<Integer> allDone) {
        this.a = a;
        this.nodes = nodes;
        this.chunk = chunk;
        this.runner = runner;
        barrier = cb;
        this.que = que;
        this.done = done;
        this.allDone = allDone;
    }

    public void run() {
        Integer cand = 0;

        try {
            while ((cand = que.poll()) != null) {
                if (a.out[cand] != null) {
                    for (int i = 0; i < a.out[cand].length; i++) {
                        if (/*a.weights[cand][i] > 0.0 &&*/!done.contains(a.out[cand][i])) {
                            done.add(a.out[cand][i]);
                            allDone.add(a.out[cand][i]);
                            que.put(a.out[cand][i]);
                        }

                    }
                }
            }
        } catch (InterruptedException ex) {
            logger.debug("", ex);
        }

        barrier.countDown();
    }

    public static Set<Set<Integer>> rootedConnectedComponents(final WeightedGraph a, final int[] nodes, int runner) throws InterruptedException {
        long time = System.currentTimeMillis();
        Set<Set<Integer>> components = new HashSet<Set<Integer>>();

        Set<Integer> allDone = Collections.synchronizedSet(new HashSet<Integer>());
        LinkedBlockingQueue<Integer> que = new LinkedBlockingQueue<Integer>(/*a.size*/);
        Set<Integer> done;

        for (int c = 0; c < nodes.length; c++) {
            if (!allDone.contains(nodes[c]) && a.out[nodes[c]] != null && a.in[nodes[c]] != null) {
                que.clear();
                done = Collections.synchronizedSet(new HashSet<Integer>());

                que.put(nodes[c]);
                done.add(nodes[c]);
                allDone.add(nodes[c]);

                final CountDownLatch latch = new CountDownLatch(runner);

                Thread[] workers = new Thread[runner];
                for (int i = 0; i < runner; i++) {
                    workers[i] = new Thread(new ConnectedComponents(a, nodes, latch, i, runner, done, que, allDone));
                    workers[i].setName("" + i);
                    workers[i].start();
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    logger.debug(e);
                }

                components.add(done);
            }
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");

        return components;
    }

}
