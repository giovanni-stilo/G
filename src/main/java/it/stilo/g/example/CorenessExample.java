/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.example;

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
