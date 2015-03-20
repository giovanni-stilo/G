package it.stilo.g.structures;

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
import it.stilo.g.util.ArraysUtil;
import java.util.Arrays;

/**
 *
 * @author stilo
 */
public abstract class WeightedGraph {

    public double[][] weights;
    public int[][] out;
    public int[][] in;
    public int size;
    public Object[] rows;
    public Object[] cols;
    public int[] V;
    public double[] vWeights;

    public abstract void add(int a, int b, double w);

    public abstract double get(int a, int b);

    public abstract boolean testAndAdd(int a, int b, double w);

    public abstract void remove(int a);

    public abstract void reset();

    public abstract boolean update(int a, int b, double w);

    public int[] getVertex() {
        int[] tmp = Arrays.copyOf(V, V.length);
        Arrays.sort(tmp);
        tmp = Arrays.copyOfRange(tmp, ArraysUtil.lastIndexOf(tmp, -1) + 1, tmp.length);

        return tmp;
    }

    public boolean setVertexWeight(int v, double w) {
        if (V[v] != -1) {
            vWeights[v] = w;
        } else {
            return false;
        }
        return true;
    }
}
