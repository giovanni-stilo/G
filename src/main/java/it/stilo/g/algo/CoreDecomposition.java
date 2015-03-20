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
import it.stilo.g.structures.Core;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.IndexAndRank;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class CoreDecomposition {

    private static final Logger logger = LogManager.getLogger(CoreDecomposition.class);

    public static DoubleValues[] getCoreSequence(WeightedGraph g, int worker) throws InterruptedException {
        IndexAndRank<DoubleValues> rank = WeightedDegreeRank.buildRank(g, worker);
        DoubleValues val = null;
        int size = g.getVertex().length;
        DoubleValues[] seq = new DoubleValues[size];

        for (int i = 0; i < size; i++) {
            val = rank.pollLast();
            seq[i] = val;

            int[] neg = g.out[val.index];

            g.remove(val.index);
            WeightedDegreeRank.updateRank(g, neg, rank, worker);
        }
        return seq;

    }

    public static Core getInnerMostCore(WeightedGraph g, int worker) throws InterruptedException {
        long time = System.currentTimeMillis();
        IndexAndRank<DoubleValues> rank = WeightedDegreeRank.buildRank(g, worker);
        DoubleValues val = null;
        int size = g.getVertex().length;
        int[] seq = new int[size];
        DoubleValues best = new DoubleValues(0, -1.0);

        for (int i = 0; i < size; i++) {

            val = rank.pollLast();

            seq[i] = val.index;
            if (val.value > best.value) {
                best.index = i;
                best.value = val.value;
            }

            int[] neg = g.out[val.index];

            g.remove(val.index);

            if (neg != null) {
                WeightedDegreeRank.updateRank(g, neg, rank, worker);
            }
        }

        int[] core = Arrays.copyOfRange(seq, best.index, seq.length);
        Core ret = new Core();
        ret.minDegree = best.value;
        ret.seq = core;
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return ret;
    }

    public static List<Core> topsInnerMost(WeightedGraph g, int worker) throws InterruptedException {
        ArrayList<Core> list = new ArrayList<Core>();
        WeightedGraph copy;
        Core core = new Core();

        if (g instanceof WeightedDirectedGraph) {
            g = UnionDisjoint.copy((WeightedDirectedGraph) g, worker);
        } else {
            g = UnionDisjoint.copy((WeightedUndirectedGraph) g, worker);
        }

        while (core.seq.length > 0) {
            if (g instanceof WeightedDirectedGraph) {
                copy = UnionDisjoint.copy((WeightedDirectedGraph) g, worker);
            } else {
                copy = UnionDisjoint.copy((WeightedUndirectedGraph) g, worker);
            }

            core = CoreDecomposition.getInnerMostCore(g, worker);
            if (core.seq.length > 0) {
                //logger.info("MinDegree: " + core.minDegree + " Vertices: " + core.seq.length);
                list.add(core);
            }
            for (int i = 0; i < core.seq.length; i++) {
                int v = core.seq[i];
                copy.remove(v);
            }
            g = copy;
        }
        return list;
    }
}
