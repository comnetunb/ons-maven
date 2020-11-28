package ons.util.ml;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Dl4JLocalModel<IT, OT> extends LocalModel<IT, OT> {
    protected MultiLayerNetwork model;

    public Dl4JLocalModel() {
        super(ModelFramework.DEEPLEARNING4J);
    }

    public void load(String path) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        load(path, ModelFormat.HDF5);
    }

    public void load(String path, ModelFormat format) throws IOException, UnsupportedKerasConfigurationException, InvalidKerasConfigurationException {
        InputStream targetStream = new FileInputStream(path);
        load(targetStream, format);
    }

    public void load(InputStream inputStream) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        load(inputStream, ModelFormat.HDF5);
    }

    public void load(InputStream is, ModelFormat format) throws UnsupportedKerasConfigurationException, IOException, InvalidKerasConfigurationException {
        if (format == ModelFormat.HDF5) {
            this.model = KerasModelImport.importKerasSequentialModelAndWeights(is);
            ready = true;
        } else {
            throw new UnsupportedOperationException("Unsupported format");
        }
    }
}
