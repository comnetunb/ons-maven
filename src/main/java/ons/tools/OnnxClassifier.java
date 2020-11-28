package ons.tools;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ons.util.ml.OnnxLocalModel;

import java.util.Collections;

public class OnnxClassifier extends OnnxLocalModel<float[][], Number> {
    public Integer _predict(float[][] testData) throws OrtException {
        try (OnnxTensor test = OnnxTensor.createTensor(this.environment, testData);
             OrtSession.Result output = this.session.run(Collections.singletonMap(this.inputName, test))) {
            var x = (float[][])output.get(0).getValue();
            if (x[0][0] >= x[0][1] && x[0][0] >= x[0][2]) return 0;
            if (x[0][1] >= x[0][0] && x[0][1] >= x[0][2]) return 1;
            return 2;
        }
    }
}
