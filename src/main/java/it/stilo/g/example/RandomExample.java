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
import it.stilo.g.algo.EdgeNormalizer;
import it.stilo.g.algo.PageRankPI;
import it.stilo.g.algo.PageRankRW;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.MemInfo;
import it.stilo.g.util.WeightedRandomGenerator;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class RandomExample {

    protected static final Logger logger = LogManager.getLogger(RandomExample.class);

    public static void main(String[] args) {
        int size = 1000;
        int worker = (int) (Runtime.getRuntime().availableProcessors());
        int degree = 15;
        logger.info("\t----\t" + size + "@" + degree + ":" + worker + "\t----");
        WeightedDirectedGraph g = new WeightedDirectedGraph(size);
        WeightedRandomGenerator.generate(g, degree, worker);
        EdgeNormalizer.normalize(g, worker);
        ArrayList<DoubleValues> list;

        double sum = 0.0;
        list = PageRankPI.compute(g, 0.85, 0.01, worker);
        for (int i = 0; i < 10; i++) {
            logger.info(list.get(i).value + ":\t" + list.get(i).index);
        }

        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).value;
        }

        sum = 0.0;
        list = PageRankRW.compute(g, 0.85, worker);
        for (int i = 0; i < 10; i++) {
            logger.info(list.get(i).value + ":\t" + list.get(i).index);
        }

        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).value;
        }

        MemInfo.info();
    }
}
