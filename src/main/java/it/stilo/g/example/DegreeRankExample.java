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
import it.stilo.g.algo.CoreDecomposition;
import it.stilo.g.structures.Core;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.MemInfo;
import it.stilo.g.util.ZacharyNetwork;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class DegreeRankExample {

    protected static final Logger logger = LogManager.getLogger(DegreeRankExample.class);

    public static void main(String[] args) throws InterruptedException {

        int worker = (int) (Runtime.getRuntime().availableProcessors());

        WeightedDirectedGraph g = new WeightedDirectedGraph(ZacharyNetwork.VERTEX);
        ZacharyNetwork.generate(g, worker);

        List<Core> subs = CoreDecomposition.topsInnerMost(g, worker);
        for (Core sub : subs) {
            logger.info("MinDegree: " + sub.minDegree + " Vertices: " + Arrays.toString(sub.seq));
        }

        MemInfo.info();
    }

}
