package it.stilo.g.example;

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

import it.stilo.g.algo.ComunityLPA;
import it.stilo.g.algo.PageRankPI;
import it.stilo.g.algo.PageRankRW;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.MemInfo;
import it.stilo.g.util.ZacharyNetwork;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class ZacharyExample {
    protected static final Logger logger = LogManager.getLogger(ZacharyExample.class);

    public static void main(String[] args) {

        int worker = (int) (Runtime.getRuntime().availableProcessors());

        WeightedDirectedGraph g = new WeightedDirectedGraph(ZacharyNetwork.VERTEX);
        ZacharyNetwork.generate(g, worker);
        logger.info(Arrays.deepToString(g.out));

        ArrayList<DoubleValues> list;
        
        list = PageRankPI.compute(g,0.99,0.5, worker);
        for (int i = 0; i < ZacharyNetwork.VERTEX; i++) {
            logger.info(list.get(i).value + ":\t\t" + list.get(i).index);
        }
        
        list = PageRankRW.compute(g,0.99, worker);
        for (int i = 0; i < ZacharyNetwork.VERTEX; i++) {
            logger.info(list.get(i).value + ":\t\t" + list.get(i).index);
        }


        int[] labels = ComunityLPA.compute(g,1.0d, worker);
        logger.info(Arrays.toString(labels));

        MemInfo.info();
    }
}
