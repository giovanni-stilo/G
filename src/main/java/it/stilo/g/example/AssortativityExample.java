/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.example;

import it.stilo.g.algo.Assortativity;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.AssortativityNetwork;
import it.stilo.g.util.MemInfo;
import it.stilo.g.util.ZacharyNetwork;
import java.text.DecimalFormat;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ditommaso
 */
public class AssortativityExample {

    protected static final Logger logger = LogManager.getLogger(AssortativityExample.class);

    public static void main(String[] args) {

        int worker = (int) (Runtime.getRuntime().availableProcessors());


        /* WeightedDirectedGraph g = new WeightedDirectedGraph(ZacharyNetwork.VERTEX);
        ZacharyNetwork.generate(g, worker);
        logger.info(Arrays.deepToString(g.out)); */
        WeightedDirectedGraph g = new WeightedDirectedGraph(AssortativityNetwork.VERTEX);
        AssortativityNetwork.generate(g, worker);
        logger.info(Arrays.deepToString(g.out));

        int classes = 2;
        //int[] partition = {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        //int[] partition = {0, 0, 0, 1, 1};
        // int[] partition = {0, 0, 0};
        int[] partition = {0, 0, 0, 1, 1, 0};
        /* for(int i=0;i<partition.length;i++){
            logger.info(partition[i]+ " ");
        }*/
        
        DecimalFormat formatter = new DecimalFormat("#0.0000");

        double[][] assortativity_matrix = Assortativity.assortativityMatrix(g, partition, classes, worker);
        String mat;
        for (int i = 0; i < assortativity_matrix.length; i++) {
            mat="";
            for (int j = 0; j < assortativity_matrix.length; j++) {
                mat += formatter.format(assortativity_matrix[i][j]) +"\t";
            }
            logger.info(mat);
        }
        double assortativity_value = Assortativity.assortativity(assortativity_matrix);
        logger.info("assortativity value: " + assortativity_value);

        double[] local_assortativity = Assortativity.localAssortativity(assortativity_matrix);
        for (int i = 0; i < local_assortativity.length; i++) {
            logger.info("local assortativity class: " + i + " value: " + local_assortativity[i]);
        }

        MemInfo.info();
    }

}
