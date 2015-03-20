package it.stilo.g.util;

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

import java.util.Arrays;

/**
 *
 * @author stilo
 */
public class QuickUnionFind {

    private int[] id;    // id[i] = parent of i
    private int[] sz;    // sz[i] = number of objects in subtree rooted at i
    private int count;   // number of components

    public QuickUnionFind(int N, int[] V) {

        count = V.length;
        id = new int[N];
        sz = new int[N];
        Arrays.fill(id, -1);
        Arrays.fill(sz, -1);
        for (int i = 0; i < V.length; i++) {
            int v = V[i];
            id[v] = v;
        }

    }

    public int size() {
        return id.length;
    }

    public int count() {
        return count;
    }
    
    public void add(int p, int q) {
        if (!this.connected(p, q)) {
            this.union(p, q);
            //logger.info(p + " " + q);
        }
    }

    public int find(int p) {
        while (p != id[p] && (p = id[p]) != -1) {
            p = id[p];
        }
        return p;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public void union(int p, int q) {

        if (id[p] == -1 || id[q] == -1) {
            return;
        }

        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) {
            return;
        }

        if (sz[rootP] < sz[rootQ]) {
            id[rootP] = rootQ;
        } else {
            id[rootQ] = rootP;
        }
        sz[rootQ] += sz[rootP];

        count--;
    }
}
