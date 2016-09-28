/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.example;

/*
 * #%L
 * G-github
 * %%
 * Copyright (C) 2013 - 2016 Giovanni Stilo
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
import it.stilo.g.algo.CoreDecomposition;
import it.stilo.g.algo.GraphInfo;
import it.stilo.g.structures.Core;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import it.stilo.g.util.GraphReader;
import it.stilo.g.util.MemInfo;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class CorenessExample {

    protected static final Logger logger = LogManager.getLogger(CorenessExample.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        int worker = (int) (Runtime.getRuntime().availableProcessors());
        WeightedUndirectedGraph g = new WeightedUndirectedGraph(3000);
        GraphReader.readGraph(g, "src/main/resources/graphs/test-graph-dn-0.gz", true);
        AtomicDouble[] info=GraphInfo.getGraphInfo(g, worker);
        System.out.println(info[0]);
        System.out.println(info[1]);
        System.out.println(info[2]);
        Core c = CoreDecomposition.getInnerMostCore(g, worker);
        System.out.println(c.minDegree);
        System.out.println(c.seq.length);
        MemInfo.info();
    }

}
