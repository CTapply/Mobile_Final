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
        BasicMLDataSet trainingSet = new BasicMLDataSet(XORInput, XOROutput);
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);

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

    public float calcCoefficient(float latitude){
        return 0.0f;
    }

    public int calcSnowDelay(int snowfall){
        return 0;
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