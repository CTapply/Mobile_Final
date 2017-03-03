package com.example.jeffrey.finalprototype.machinelearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.jeffrey.finalprototype.Content;

import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

import java.io.File;
import java.io.IOException;

import static com.example.jeffrey.finalprototype.Content.COMMUTE_MAP;

/**
 * Created by tjvalcourt on 2/21/2017.
 */

public class BaseNetwork extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("MACHINE")) {
            try{
                // Train the network
                File trainingFile = new File(context.getFilesDir(), "training_data_fin.csv");
                trainingFile.createNewFile(); // create new file if doesn't exist, otherwise open

                // Define the format of the data file.
                // This area will change, depending on the columns and
                // format of the file that you are trying to model.
                VersatileDataSource source = new CSVDataSource(trainingFile, false, CSVFormat.DECIMAL_POINT);
                VersatileMLDataSet data = new VersatileMLDataSet(source);
                data.defineSourceColumn("prepTime", 0, ColumnType.continuous);
                data.defineSourceColumn("day", 1, ColumnType.continuous);
                data.defineSourceColumn("snowfall", 2, ColumnType.continuous);

                // Define the output column
                ColumnDefinition outputColumn = data.defineSourceColumn("newPrepTime", 3, ColumnType.continuous);

                // Analyze the data
                data.analyze();

                // Map prediction column to the output
                data.defineSingleOutputOthersInput(outputColumn);

                // Create a feed forward network model
                EncogModel model = new EncogModel(data);
                model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

                // send output to console
                model.setReport(new ConsoleStatusReportable());

                // normalize the data; normalization handled by the type of model we chose
                data.normalize();

                // Hold back some data for a final validation, shuffle, and seed it with the same value
                model.holdBackValidation(0.3, true, 777);

                // Choose whatever is the default training type for this model.
                model.selectTrainingType(data);

                // Use a 5-fold cross-validated train.  Return the best method found.
                MLRegression bestMethod = (MLRegression) model.crossvalidate(5, true);

                // Display our normalization parameters.
                NormalizationHelper helper = data.getNormHelper();

                // Loop over the training dataset to first train the model
                ReadCSV csv = new ReadCSV(trainingFile, false, CSVFormat.DECIMAL_POINT);
                String[] line = new String[3];
                MLData input = helper.allocateInputVector();

                // Now test the data point from the Intent
                line[0] = intent.getStringExtra("prepTime");
                line[1] = intent.getStringExtra("day");
                line[2] = intent.getStringExtra("snowfall");

                String correct = intent.getStringExtra("newPrepTime");
                helper.normalizeInputVector(line, input.getData(), false);
                MLData output = bestMethod.compute(input);

                setCommutePrep(intent.getStringExtra("commuteID"), helper.denormalizeOutputVectorToString(output)[0]); // set the commute to the predicted time

                Encog.getInstance().shutdown(); // stop running the learning
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the commute to have the new prep time and also
     * call to update the database entry
     */
    private void setCommutePrep(String commuteID, String finalPrepTime){
        for(Content.Commute c : COMMUTE_MAP.values()) {
            if (c.id.equals(commuteID)) {
                c.preparationTime = Integer.parseInt(finalPrepTime);
                c.updateCommute();
            }
        }
    }
}