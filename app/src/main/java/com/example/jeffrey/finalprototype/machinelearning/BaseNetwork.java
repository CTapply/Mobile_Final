package com.example.jeffrey.finalprototype.machinelearning;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Created by tjvalcourt on 2/21/2017.
 */

public class BaseNetwork {

    private BasicNetwork network;

    // Test on the tried and true example of XOR
    private double[][] XORInput = {
            {0,0},
            {0,1},
            {1,0},
            {1,1}
    };
    private double[][] XOROutput = {
            {0},{1},{1},{1}
    };

    public BaseNetwork(){
        network = new BasicNetwork();

        // add Basic layer, 2 sigmoid layers 1 with bias and 1 without
        network.addLayer(new BasicLayer(null, true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
        network.getStructure().finalizeStructure();
        network.reset();

        // create a training data set
        BasicMLDataSet trainingSet = new BasicMLDataSet(XORInput, null);
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        //BasicMLDataSet set = new BasicMLDataSet()

        // Train!
        int epoch = 1 ;
        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + "\tError: " + train.getError());
            epoch++;
        } while (train.getError() > 0.01);
        train.finishTraining();

        // Display Results
        System.out.println("\nNeural Network Results");
        for(MLDataPair pair : trainingSet){
            MLData output = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0) + ", "
                    + pair.getInput().getData(1) + ",actual="
                    + output.getData(0)+ "\tideal="
                    + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }

    /**
     * Helper function for calcSnowDelay();
     * Calculates the coefficient between [0.25, 1.25] based on the input latitude
     * @param latitude Latitude in degrees of the selected location
     * @return Coefficient in range [0.25, 1.25]
     */
    public float calcCoefficient(float latitude){
        final float latMax = 55, latMin = 20;
        final float coefficientMax = 1.25f, coefficientMin = 0.25f;

        /**
         * We bind the range of the latitude between 55 degrees (Thompson, Manitoba)
         * and 20 degrees (Mexico City) for simplicity. Anything above or below
         * is set to these thresholds
         */
        if(latitude > latMax)
            latitude = latMax;

        if(latitude < latMin)
            latitude = latMin;

        // The offset is the amount per degree latitude we add to the coefficient
        float offset = (coefficientMax - coefficientMin) / (latMax - latMin);

        // Calculated coefficient based on the offset from the input latitude
        float coefficient = coefficientMin + ((latMax - latitude) * offset);

        return coefficient;
    }

    /**
     * Calculate the snow delay time (in minutes) based on the latitude and amount of snowfall
     * @param snowfall Amount of snowfall in inches
     * @param latitude Location of the commute in degrees
     * @return Number of minutes delayed by the snow
     */
    public int calcSnowDelay(int snowfall, float latitude){
        return (int)(calcCoefficient(latitude)*(snowfall + 10));
    }
}
/**
    Calculate Snow Delay based on: inches, location
    Calculate Prep Time based on : User_Time, Last_Prep_By_Day_of_Week, + Snow Delay

    Set some form of latitude as a range;
        > ex south carolina coefficient is 1 (most extreme)
               : North of Maine asymptotes to something low (doesn't matter as much)

                coefficient * (num_inches + 10)               c [0.25, 1.25]
                max_delay caps at 15 minutes

    training_set = {expected = 20, Monday}
    output_set = {actual_time = 22}

    prep_time = output + snow_delay
 */

/**
    Thresh_Max = 55.7435 (Thompson, Manitoba, Canada    ~ 55
    Thresh_Min = 19.428471 (Mexico City, Mexico)        ~ 20
 */

/**
    Use the database to store training data about the neural network
        : Each commute needs to keep track of its own data

        : Does this mean that we re-train the network every single time???
 */

/**
private int input[][] = {
        {expected_time, last_prep}
}

private int output[][] = {
        {actual_time}
}

        prep = actual_time + snow_delay
*/