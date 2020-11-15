package ons.tools;

import ons.util.ml.Dl4jModel;
import org.nd4j.linalg.factory.Nd4j;

public class Dl4jClassifier extends Dl4jModel<float[][], Integer> {
    public Integer predict(float[][] testData) {
        var input = Nd4j.create(testData);
        var x = this.model.output(input);
        if (x.getColumn(0).getDouble(0) >= x.getColumn(1).getDouble(0) && x.getColumn(0).getDouble(0) >= x.getColumn(2).getDouble(0)) return 0;
        if (x.getColumn(1).getDouble(0) >= x.getColumn(0).getDouble(0) && x.getColumn(1).getDouble(0) >= x.getColumn(2).getDouble(0)) return 1;
        return 2;
    }
}
