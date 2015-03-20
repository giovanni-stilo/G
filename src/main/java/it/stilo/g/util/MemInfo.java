package it.stilo.g.util;

import it.stilo.g.structures.IntValues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 *
 * @author stilo
 */
public class MemInfo {
    private static final Logger logger = LogManager.getLogger(MemInfo.class);
    static Runtime runtime = Runtime.getRuntime();

    public static void info() {
        int mb = 1024 * 1024;
        logger.info("\t-----------------------------------");
        //Print used memory
        logger.info("\tUsed Memory:\t\t"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb+ "Mb");
        //Print free memory
        logger.info("\tFree Memory:\t\t"
                + runtime.freeMemory() / mb+ "Mb");
        //Print total available memory
        logger.info("\tTotal Memory:\t\t" + runtime.totalMemory() / mb+ "Mb");
        //Print Maximum available memory
        logger.info("\tMax Memory:\t\t" + runtime.maxMemory() / mb+ "Mb");
        logger.info("\tAvailable Processors:\t" +Runtime.getRuntime().availableProcessors());
        logger.info("\t-----------------------------------");
    }
}
