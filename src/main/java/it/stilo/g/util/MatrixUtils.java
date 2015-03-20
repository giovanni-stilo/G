/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.util;

import java.util.Arrays;
import java.util.Random;

/*
 * #%L
 * G
 * %%
 * Copyright (C) 2014 - 2015 Giovanni Stilo
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
/**
 *
 * @author stilo
 */
public class MatrixUtils {

    public static int[] toUniqList(int[][] sets) {
        int[] elements = new int[0];
        for (int[] set : sets) {
            elements = ArraysUtil.concat(elements, set);
        }
        elements = ArraysUtil.uniq(elements);
        return elements;
    }

    public static String toCSVLines(int[][] m) {
        String ret = "";
        for (int i = 0; i < m.length; i++) {
            ret += ArraysUtil.toCSV(m[i]) + "\n";
        }

        return ret;
    }

    public static int[][] shuffling(int[][] m) {
        Random rnd = new Random(System.currentTimeMillis());
        int[][] shuff = new int[m.length][];
        long tLen = 0;

        for (int i = 0; i < m.length; i++) {
            shuff[i] = Arrays.copyOf(m[i], m[i].length);
            tLen += m[i].length;
        }

        tLen = (long) tLen * (long) (tLen * 0.5);
        if (tLen < 0) {
            tLen = Integer.MAX_VALUE;
        }

        for (long i = 0; i < tLen; i++) {
            int row0 = rnd.nextInt(shuff.length);
            int row1;
            while ((row1 = rnd.nextInt(shuff.length)) == row0);

            int col0 = rnd.nextInt(shuff[row0].length);
            int col1 = rnd.nextInt(shuff[row1].length);

            int tmp = shuff[row0][col0];
            shuff[row0][col0] = shuff[row1][col1];
            shuff[row1][col1] = tmp;
        }

        return shuff;
    }
}
