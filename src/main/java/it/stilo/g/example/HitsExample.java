package it.stilo.g.example;


import it.stilo.g.algo.HubnessAuthority;
import java.util.ArrayList;
import java.util.Arrays;

import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.RandomTestNet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Di Tommaso, Stilo
 */
public class HitsExample {
    private static final Logger logger = LogManager.getLogger(HitsExample.class);

    public static void main(String[] args) {


        int worker = (int) (Runtime.getRuntime().availableProcessors());

        WeightedDirectedGraph g = new WeightedDirectedGraph(31);
        RandomTestNet.generate(g,RandomTestNet.RANDOM_WEIGHTED, worker);
        logger.trace(Arrays.deepToString(g.out));
        logger.trace(Arrays.deepToString(g.in));
        logger.trace(Arrays.deepToString(g.weights));
        
        ArrayList<ArrayList<DoubleValues>> list;

        list = HubnessAuthority.compute(g, 0.01, worker);

        for (int i = 0; i < list.size(); i++) {
            ArrayList<DoubleValues> score = list.get(i);
            String x = "";
            if (i == 0) {
                x = "Auth ";
            } else {
                x = "Hub ";
            }

            for (int j = 0; j < score.size(); j++) {
                logger.trace(x + score.get(j).value + ":\t\t" + score.get(j).index);
            }
        }
    }
}
