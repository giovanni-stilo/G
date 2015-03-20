package it.stilo.g.algo;

/*
 * #%L
 * G
 * %%
 * Copyright (C) 2013 - 2015 Giovanni Stilo
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
import it.stilo.g.structures.WeightedUndirectedGraph;
import it.stilo.g.util.ArraysUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author stilo
 */
public class BronKerbosch {

    private static final Logger logger = LogManager.getLogger(BronKerbosch.class);

    public static Set<Set<Integer>> maxCliques(WeightedUndirectedGraph g) {
        long time = System.currentTimeMillis();
        Set<Set<Integer>> cliques = new HashSet<Set<Integer>>();
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (int i = 0; i < g.out.length; i++) {
            if (g.out[i] != null && g.out[i].length > 0) {
                candidates.add(i);
            }
        }
        ArrayList<Integer> potential_clique = new ArrayList<Integer>();
        ArrayList<Integer> already_found = new ArrayList<Integer>();

        findCliques(potential_clique, candidates, already_found, g, cliques);
        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return cliques;
    }

    private static void findCliques(ArrayList<Integer> potential_clique, ArrayList<Integer> candidates, ArrayList<Integer> already_found, WeightedUndirectedGraph g, Set<Set<Integer>> cliques) {
        ArrayList<Integer> candidates_array = new ArrayList(candidates);

        if (!end(candidates, already_found, g)) {
            for (Integer candidate : candidates_array) {
                ArrayList<Integer> new_candidates = new ArrayList<Integer>();
                ArrayList<Integer> new_already_found = new ArrayList<Integer>();
                potential_clique.add(candidate);
                candidates.remove(candidate);

                for (Integer new_candidate : candidates) {
                    if (Arrays.binarySearch(g.out[candidate], new_candidate) > -1) {
                        new_candidates.add(new_candidate);
                    }
                }

                for (Integer new_found : already_found) {
                    if (Arrays.binarySearch(g.out[candidate], new_found) > -1) {
                        new_already_found.add(new_found);
                    }
                }
                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
                    cliques.add(new HashSet<Integer>(potential_clique));
                } else {
                    findCliques(potential_clique, new_candidates, new_already_found, g, cliques);
                }
                already_found.add(candidate);
                potential_clique.remove(candidate);
            }
        }
    }

    private static boolean end(ArrayList<Integer> candidates, ArrayList<Integer> already_found, WeightedUndirectedGraph g) {
        int[] cands = new int[candidates.size()];
        for (int i = 0; i < cands.length; i++) {
            cands[i] = candidates.get(i);
        }

        for (Integer found : already_found) {
            if (ArraysUtil.contained(cands, g.out[found])) {
                return true;
            }
        }
        return false;
    }
}
