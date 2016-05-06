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
import it.stilo.g.structures.WeightedGraph;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author stilo
 */
public class ArraysUtil {

    public static int INDEX_NOT_FOUND = -1;

   /* public static int[][] addToComponents(WeightedGraph g, int[][] components, int candidate) {
        int[][] ret;

        int[] cand = g.out[candidate];
        double[] weigths = g.weights[candidate];
        if (cand == null) {
            return components;
        }

        int[] toMerge = new int[components.length];
        int[] merged = new int[]{candidate};
        int mergeSize = 0;

        for (int i = 0; i < components.length; i++) {
            if (thereIsIntersection(cand, components[i], weigths)) {
                int[] tmp = components[i];
                components[i] = null;
                merged = Arrays.copyOf(merged, merged.length + tmp.length);
                System.arraycopy(tmp, 0, merged, (merged.length - tmp.length), tmp.length);

                mergeSize++;
            }
        }

        if (mergeSize == 0) {
            ret = Arrays.copyOf(components, components.length + 1);
            ret[components.length] = merged;
        } else {
            merged = ArraysUtil.uniq(merged);
            ret = new int[(components.length - mergeSize) + 1][];
            for (int i = 0, j = 0; i < components.length; i++) {
                if (components[i] != null) {
                    ret[j] = components[i];
                    j++;
                }
            }
            ret[ret.length - 1] = merged;
        }
        return ret;
    }

    public static int[][] addToComponents(int[][] components, int[] vect, double[] weigths, int candidate) {
        int[][] ret;

        if (vect == null) {
            return components;
        }

        int[] toMerge = new int[components.length];
        int[] merged = new int[]{candidate};
        int mergeSize = 0;

        for (int i = 0; i < components.length; i++) {
            if (Arrays.binarySearch(components[i], candidate) >= 0) {
                return components;
            }

            if (thereIsIntersection(vect, components[i], weigths)) {
                int[] tmp = components[i];
                components[i] = null;
                merged = Arrays.copyOf(merged, merged.length + tmp.length);
                System.arraycopy(tmp, 0, merged, (merged.length - tmp.length), tmp.length);

                mergeSize++;
            }
        }

        if (mergeSize == 0) {
            ret = Arrays.copyOf(components, components.length + 1);
            ret[components.length] = merged;
        } else {
            merged = ArraysUtil.uniq(merged);
            ret = new int[(components.length - mergeSize) + 1][];
            for (int i = 0, j = 0; i < components.length; i++) {
                if (components[i] != null) {
                    ret[j] = components[i];
                    j++;
                }
            }
            ret[ret.length - 1] = merged;
        }
        return ret;
    }*/

    public static int[] uniq(int[] a) {
        if (a == null || a.length == 0) {
            return a;
        }

        Arrays.sort(a);

        int[] tmp = new int[a.length];

        int j = 0;
        tmp[0] = a[0];
        for (int i = 0; i < a.length && j < tmp.length; i++) {
            if (a[i] != tmp[j]) {
                j++;
                if (j < tmp.length) {
                    tmp[j] = a[i];
                }
            }
        }

        return Arrays.copyOf(tmp, j + 1);
    }

    public static int[] concat(int[] first, int[] second) {
        if (first == null && second == null) {
            return new int[0];
        }

        if (first == null) {
            return Arrays.copyOf(second, second.length);
        }

        if (second == null) {
            return Arrays.copyOf(first, first.length);
        }

        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    public static long[] uniq(long[] a) {
        if (a == null || a.length == 0) {
            return a;
        }

        Arrays.sort(a);

        long[] tmp = new long[a.length];

        int j = 0;
        tmp[0] = a[0];
        for (int i = 0; i < a.length && j < tmp.length; i++) {
            if (a[i] != tmp[j]) {
                j++;
                if (j < tmp.length) {
                    tmp[j] = a[i];
                }
            }
        }

        return Arrays.copyOf(tmp, j + 1);
    }

    public static long[] concat(long[] first, long[] second) {
        if (first == null && second == null) {
            return new long[0];
        }

        if (first == null) {
            return Arrays.copyOf(second, second.length);
        }

        if (second == null) {
            return Arrays.copyOf(first, first.length);
        }

        long[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     *
     * @param a contained
     * @param b container
     * @return
     */
    public static boolean contained(int[] a, int[] b) {

        int i = 0;
        for (int j = 0; i < a.length && j < b.length;) {
            if (a[i] == b[j]) {
                i++;
                j++;
            } else {
                j++;
            }
        }
        return i == a.length;
    }

    //b-a
    public static int[] remove(int[] a, int[] b) {
        Arrays.sort(a);
        Arrays.sort(b);

        int[] rest = null;
        int k = 0;
        if (a.length > 0 && b.length > 0) {
            rest = Arrays.copyOf(b, b.length);
            for (int j = 0; j < b.length; j++) {
                for (int i = 0; i < a.length; i++) {
                    if (a[i] == b[j]) {
                        rest[j] = -1;
                        j++;
                        k++;
                        if (j >= b.length) {
                            break;
                        }
                    }
                }
            }
        } else {
            return b;
        }

        Arrays.sort(rest);

        return Arrays.copyOfRange(rest, k, rest.length);
    }

    public static int[] intersection(int[] array1, int[] array2) {
        int[] intersection = new int[array1.length < array2.length ? array1.length : array2.length];
        int k = 0;
        if (array1.length > 0 && array2.length > 0) {
            for (int i = 0, j = 0; i < array1.length && j < array2.length;) {

                if (array1[i] == array2[j]) {
                    intersection[k] = array1[i];
                    k++;
                    i++;
                    j++;
                }

                if (j < array2.length && i < array1.length) {
                    if (array2[j] < array1[i]) {
                        j++;
                    } else if (array2[j] > array1[i]) {
                        i++;
                    }
                }

            }
        }

        if (k < intersection.length) {
            intersection = Arrays.copyOf(intersection, k);
        }
        return intersection;
    }
    
    
    public static long[] intersection(long[] array1, long[] array2) {
        long[] intersection = new long[array1.length < array2.length ? array1.length : array2.length];
        int k = 0;
        if (array1.length > 0 && array2.length > 0) {
            for (int i = 0, j = 0; i < array1.length && j < array2.length;) {

                if (array1[i] == array2[j]) {
                    intersection[k] = array1[i];
                    k++;
                    i++;
                    j++;
                }

                if (j < array2.length && i < array1.length) {
                    if (array2[j] < array1[i]) {
                        j++;
                    } else if (array2[j] > array1[i]) {
                        i++;
                    }
                }

            }
        }

        if (k < intersection.length) {
            intersection = Arrays.copyOf(intersection, k);
        }
        return intersection;
    }

    /**
     *
     * @param array1 vector of the candidate
     * @param array2 connected components
     * @param weigths weigth of the candidate
     * @return
     */
    public static boolean thereIsIntersection(int[] array1, int[] array2, double[] weigths) {

        if (array1.length > 0 && array2.length > 0) {
            for (int i = 0, j = 0; i < array1.length && j < array2.length;) {

                if (array1[i] == array2[j]) {
                    if (weigths[i] > 0.0) {
                        return true;
                    }
                }

                if (j < array2.length && i < array1.length) {
                    if (array2[j] < array1[i]) {
                        j++;
                    } else if (array2[j] > array1[i]) {
                        i++;
                    } else if (array2[j] == array1[i]) {
                        i++;
                        j++;
                    }

                }

            }
        }
        return false;
    }

    public static double collapse(double[] w) {
        if (w == null) {
            return 0.0;
        }

        double summ = 0.0d;
        for (int i = 0; i < w.length; i++) {
            summ += w[i];
        }
        return summ;
    }

    public static int[] sum(int[] a, int[] b) {
        int[] ret = new int[a.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i] + b[i];
        }
        return ret;
    }

    public static int[] mSum(int[][] a) {
        int[] ret = new int[a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                ret[j] += a[i][j];
            }
        }
        return ret;
    }

    public static int[] sub(int[] a, int[] b) {
        int[] ret = new int[a.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i] - b[i];
        }
        return ret;
    }

    public static double[] sub(double[] a, double[] b) {
        double[] ret = new double[a.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i] - b[i];
        }
        return ret;
    }

    public static double L1(double[] x) {
        double l1 = 0.0;
        for (int i = 0; i < x.length; i++) {
            l1 += Math.abs(x[i]);
        }

        return l1;
    }

    public static int lastIndexOf(int[] array, int valueToFind) {
        int startIndex = array.length - 1;

        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

  /*  public static int[] toIntArray(Set<Integer> set) {
        int[] ints = new int[set.size()];
        int index = 0;
        for (Integer i : set) {
            ints[index++] = i;

        }
        return ints;
    }*/
    
     public static int[] toIntArray(Collection<Integer> colls) {
        int[] ints = new int[colls.size()];
        int index = 0;
        for (Integer i : colls) {
            ints[index++] = i;

        }
        return ints;
    }

    //Pre ordered arrays
    public static double cosineSimilarity(int[] a, int[] b) {
        int[] inter = ArraysUtil.intersection(a, b);

        if (inter.length == 0) {
            return 0.0d;
        }

        return inter.length / (Math.sqrt(a.length) * Math.sqrt(b.length));
    }

    public static  String toCSV(int[] a) {
        String ret = "";
        for (int i = 0; i < a.length; i++) {
            if (i != 0) {
                ret += ',' + a[i];
            } else {
                ret += a[i];
            }
        }
        return ret;
    }
}
