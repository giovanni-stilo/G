/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.util;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;

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
public class GreedySCP {
    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(GreedySCP.class);


    private static int[][] testData = {
        //1,8         Ordered by distance form Average Length and lexicografical order.  
        {0, 3},//0,2
        {1, 2},//0,2
        {2, 3},//0,2       
        {2},//0,8        
        {3},//0,8
        {0, 1, 3}//1,2            
    };

    public static int[] greedySCP(int[][] data) {
        int[] sol = new int[data.length];
        int solP = 0;

        int[] elements = MatrixUtils.toUniqList(data);

        for (int j = 0; j < data.length; j++) {

            int maxEl = 0, tmp = 0, pos = -1;
            for (int i = j; i < data.length; i++) {
                if (data[i].length < maxEl) {
                    break;
                }
                if ((tmp = ArraysUtil.intersection(data[i], elements).length) > maxEl) {
                    maxEl = tmp;
                    pos = i;
                }
            }
            if (pos > -1) {
                sol[solP] = pos;
                elements = ArraysUtil.remove(data[pos], elements);
                solP++;
                if (elements.length == 0) {
                    break;
                }
            }
        }

        return Arrays.copyOf(sol, solP);
    }

    public static void main(String[] args) {
        int[] sol = greedySCP(testData);
        for (int i : sol) {
            logger.info(Arrays.toString(testData[i]));
        }
    }
}
