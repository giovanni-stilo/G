/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.stilo.g.algo;

import com.google.common.util.concurrent.AtomicDouble;
import it.stilo.g.structures.WeightedGraph;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ditommaso,stilo
 */
public class Assortativity implements Runnable {

    private static final Logger logger = LogManager.getLogger(Assortativity.class);

    private WeightedGraph g;
    private int chunk;
    private int runner;
    private CountDownLatch matrixStep;
    private int[] partition;
    private int classes;
    private AtomicDouble[][] matrix;

    private Assortativity(WeightedGraph g, CountDownLatch matrixStep, AtomicDouble[][] matrix, int[] partition, int classes, int chunk, int runner) {

        this.g = g;
        this.chunk = chunk;
        this.runner = runner;
        this.matrixStep = matrixStep;
        this.matrix = matrix;
        this.partition = partition;
        this.classes = classes;
    }

    @Override
    public void run() {
        for (int i = chunk; i < g.size; i += runner) {
            int type_src = partition[i];

            if (g.out[i] != null) {

                for (int p = 0; p < g.out[i].length; p++) {
                    int j = g.out[i][p];
                    int type_dst = partition[j];

                    matrix[type_src][type_dst].addAndGet(g.weights[i][p]);

                }
            }

        }
        matrixStep.countDown(); //Signal to other threads
        try {
            matrixStep.await();
        } catch (InterruptedException ex) {
            logger.debug(ex);
        }
    }

    //partition vettore delle classi assegnate ai vertici, le classi vanno da 0 a classes-1
    public static double[][] assortativityMatrix(final WeightedGraph g, int[] partition, int classes, int runner) {
        long time = System.currentTimeMillis();
        AtomicDouble[][] matrix = new AtomicDouble[classes][classes];

        //Init Matrix
        for (int i = 0; i < classes; i++) {
            for (int j = 0; j < classes; j++) {
                matrix[i][j] = new AtomicDouble(0.0);
            }
        }

        // Launch Thread
        {
            CountDownLatch matrixStep = new CountDownLatch(runner);
            Thread[] workers = new Thread[runner];
            for (int i = 0; i < runner; i++) {
                workers[i] = new Thread(new Assortativity(g, matrixStep, matrix, partition, classes, i, runner), "" + i);
                workers[i].start();
            }

            try {
                matrixStep.await();
            } catch (InterruptedException e) {
                logger.debug(e);
            }
        }

        // Compute Final Matrix
        double[][] total_matrix;
        {
            total_matrix = new double[classes + 1][classes + 1];

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    total_matrix[i][j] = matrix[i][j].doubleValue();
                    total_matrix[i][classes] += matrix[i][j].doubleValue();
                    total_matrix[classes][j] += matrix[i][j].doubleValue();
                    total_matrix[classes][classes] += matrix[i][j].doubleValue();
                }
            }

            double tot = total_matrix[classes][classes];

            for (int i = 0; i < total_matrix.length; i++) {
                for (int j = 0; j < total_matrix.length; j++) {
                    total_matrix[i][j] /= tot;
                }
            }
            total_matrix[classes][classes] = tot;
        }
        logger.trace(((System.currentTimeMillis() - time) / 1000d));

        return total_matrix;
    }

    public static double assortativity(final WeightedGraph g, int[] partition, int classes, int runner) {
        return assortativity(assortativityMatrix(g, partition, classes, runner));
    }

    public static double assortativity(double[][] m) {
        int classes = m.length - 1;

        double sum_e_ii = 0.0;
        double sum_ai_bi = 0.0;

        for (int i = 0; i < classes; i++) {
            sum_e_ii += m[i][i];
            sum_ai_bi += (m[classes][i] * m[i][classes]);
        }

        return (sum_e_ii - sum_ai_bi) / (1 - sum_ai_bi);
    }
    /*public static double computeAssortativity(final WeightedGraph g, int[] partition, int classes, int runner) {
     double assortativity = 0.0;

     double[] parameters = createAssortativityFormula(g, partition, classes, runner);
     assortativity = (parameters[0] - parameters[1]) / (1 - parameters[1]); //sum eii - sum aibi/ 1 - sum aibi

     return assortativity;
     }

     private static double[] createAssortativityFormula(final WeightedGraph g, int[] partition, int classes, int runner) {
     double[] parameters = new double[2];

     double[][] m = compute(g, partition, classes, runner);
     double e_ii = 0.0;
     double a_ib_i = 0.0;

     for (int i = 0; i < m.length - 1; i++) {
     parameters[0] += m[i][i];//sum eii
     parameters[1] += (m[classes][i] * m[i][classes]);//sum aibi

     }
     return parameters;

     }*/

    public static double[] localAssortativity(final WeightedGraph g, int[] partition, int classes, int runner) {
        return localAssortativity(assortativityMatrix(g, partition, classes, runner));
    }

    public static double[] localAssortativity(double[][] m) {
        int classes = m.length - 1;

        double[] local_assortativity = new double[classes];

        double sum_ai_bi = 0.0;

        for (int i = 0; i < classes; i++) {
            sum_ai_bi += (m[classes][i] * m[i][classes]);
        }

        for (int i = 0; i < classes; i++) {
            local_assortativity[i] = (m[i][i] - (m[classes][i] * m[i][classes])) / (1 - sum_ai_bi);
        }

        return local_assortativity;
    }
}
