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
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.set.hash.TLongHashSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author stilo
 */
public class TxtGraphUtils {

    protected static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(TxtGraphUtils.class);

    private static String SEP = "\t";

    public static void convertTextGraphWithLongMapGZ(String inputGraph, String outputGraph, String mapFile) throws IOException {

        TObjectLongHashMap map = loadTXT2LongGZMap(mapFile, false);
        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputGraph))));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputGraph))));

        String line, newLine;
        long id1, id2;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            id1 = map.get(parts[0]);
            id2 = map.get(parts[1]);
            if (id1 != 0 && id2 != 0) {
                newLine = "" + id1 + SEP + id2 + (parts.length > 2 ? (SEP + parts[2]) : "");
                bw.write(newLine);
                bw.newLine();
            }
        }
        bw.flush();
        bw.close();

        br.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

    public static void convertTextGraphAndCreateLongMapGZ(String inputGraph, String outputGraph, String mapFile) throws IOException {
        int count = 1;
        TLongLongHashMap map = new TLongLongHashMap();
        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputGraph))));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputGraph))));

        String line, newLine;
        long id1, id2, tId1, tId2;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            tId1 = Long.parseLong(parts[0]);
            tId2 = Long.parseLong(parts[1]);

            if (!map.contains(tId1)) {
                map.put(tId1, count);
                count++;
            }

            if (!map.contains(tId2)) {
                map.put(tId2, count);
                count++;
            }

            id1 = map.get(tId1);
            id2 = map.get(tId2);
            if (id1 != 0 && id2 != 0) {
                newLine = "" + id1 + SEP + id2 + (parts.length > 2 ? (SEP + parts[2]) : "");
                bw.write(newLine);
                bw.newLine();
            }
        }
        bw.flush();
        bw.close();

        br.close();
        TxtGraphUtils.saveLongLongList(mapFile, map);

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

    public static void filterGraphWithLongSetGZ(String inputGraph, String outputGraph, String setFile, int srcDst, boolean strict) throws IOException {
        TLongHashSet idSet = loadLongSet(setFile);
        filterGraphWithLongSetGZ(inputGraph, outputGraph, idSet, srcDst, strict);
    }

    public static void filterGraphWithLongSetGZ(String inputGraph, String outputGraph, TLongHashSet idSet, int srcDst, boolean strict) throws IOException {

        long time = System.currentTimeMillis();

        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputGraph))));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputGraph))));

        String line, newLine;
        long id1, id2;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            boolean write = false;
            switch (srcDst) {
                case -1:
                    if (idSet.contains(Long.parseLong(parts[0]))) {
                        write = true;
                    }
                    break;
                case 0:
                    if (idSet.contains(Long.parseLong(parts[0])) && idSet.contains(Long.parseLong(parts[1]))) {
                        write = true;
                    }
                    break;
                case 1:
                    if (idSet.contains(Long.parseLong(parts[1]))) {
                        write = true;
                    }
                    break;
            }
            if (write) {
                if (strict) {
                    if (parts.length > 2) {
                        newLine = "" + parts[0] + SEP + parts[1] + SEP + parts[2];
                    } else {
                        newLine = "" + parts[0] + SEP + parts[1] + SEP + 1;
                    }
                } else {
                    if (parts.length > 2) {
                        newLine = "" + parts[1] + SEP + parts[0] + SEP + parts[2];
                    } else {
                        newLine = "" + parts[1] + SEP + parts[0] + SEP + 1;
                    }

                }

                bw.write(newLine);
                bw.newLine();
            }
        }
        bw.flush();
        bw.close();

        br.close();

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

    public static TLongHashSet loadLongSet(String setFile) throws IOException {
        long time = System.currentTimeMillis();
        TLongHashSet idSet = new TLongHashSet();
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(setFile))));
        String line;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            idSet.add(Long.parseLong(parts[0]));
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return idSet;
    }

    public static void verifyIntIDS(String inputGraph) throws IOException {
        long time = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(inputGraph))));
        String line;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            Integer.parseInt(parts[0]);
            Integer.parseInt(parts[1]);
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
    }

    public static TObjectLongHashMap loadTXT2LongGZMap(String mapFile, boolean reverse) throws IOException {
        long time = System.currentTimeMillis();
        TObjectLongHashMap idMap = new TObjectLongHashMap();
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(mapFile))));
        String line;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);

            try {
                if (!reverse) {
                    idMap.put(parts[1], Long.parseLong(parts[0]));
                } else {
                    idMap.put(parts[0], Long.parseLong(parts[1]));
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                logger.info(line);
                System.exit(-1);
            }
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return idMap;
    }

    public static TLongObjectHashMap loadLong2TXTGZMap(String mapFile, boolean reverse) throws IOException {
        long time = System.currentTimeMillis();
        TLongObjectHashMap idMap = new TLongObjectHashMap();
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(mapFile))));
        String line;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            if (!reverse) {
                idMap.put(Long.parseLong(parts[0]), parts[1]);
            } else {
                idMap.put(Long.parseLong(parts[1]), parts[0]);
            }
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return idMap;
    }

    public static TLongLongHashMap loadLong2LongGZMap(String mapFile, boolean reverse) throws IOException {
        long time = System.currentTimeMillis();
        TLongLongHashMap idMap = new TLongLongHashMap();
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(mapFile))));
        String line;
        String[] parts;

        while ((line = br.readLine()) != null) {
            parts = line.split(SEP);
            if (!reverse) {
                idMap.put(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            } else {
                idMap.put(Long.parseLong(parts[1]), Long.parseLong(parts[0]));
            }
        }

        logger.info(((System.currentTimeMillis() - time) / 1000d) + "s");
        return idMap;
    }

    public static String[] loadTXTList(String listFile, int size) throws IOException {
        String[] list = new String[size];
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(listFile))));
        String line;
        int count = 0;

        while (count < size && (line = br.readLine()) != null) {
            list[count] = line;
            count++;
        }

        return list;
    }

    public static long[] loadLongList(String listFile, int size) throws IOException {
        long[] list = new long[size];
        BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(listFile))));
        String line;
        int count = 0;

        while (count < size && (line = br.readLine()) != null) {
            list[count] = Long.parseLong(line);
            count++;
        }

        return list;
    }

    public static void saveLongTxtList(long[] ids, String[] texts, String listFile) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(listFile))));
        for (int i = 0; i < texts.length; i++) {
            bw.append(ids[i] + SEP + texts[i]);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    public static void saveLongLongList(String listFile, TLongLongHashMap map) throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(listFile))));
        TLongLongIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            bw.append(iter.value() + SEP + iter.key());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
}
