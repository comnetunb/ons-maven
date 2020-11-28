package ons.util.ml;

public interface Model<IT, OT> {
    OT predict(IT input) throws Exception;
    ModelFramework getModelFramework();
}
