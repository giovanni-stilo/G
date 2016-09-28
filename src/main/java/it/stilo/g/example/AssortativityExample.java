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
