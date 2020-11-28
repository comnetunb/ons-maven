package ons.util.ml;

import java.io.InputStream;

public abstract class LocalModel<IT, OT> implements Model<IT, OT> {
    protected final ModelFramework modelFramework;
    protected boolean ready = false;

    protected LocalModel(final ModelFramework modelFramework) {
        this.modelFramework = modelFramework;
    }

    abstract void load(String path) throws Exception;
    abstract void load(String path, ModelFormat format) throws Exception;
    abstract void load(InputStream inputStream) throws Exception;
    abstract void load(InputStream inputStream, ModelFormat format) throws Exception;

    protected abstract OT _predict(IT input) throws Exception;

    public OT predict(IT input) throws Exception {
        if (!ready) {
            throw new IllegalStateException("Must load local model before predicting");
        }

        return _predict(input);
    }

    public ModelFramework getModelFramework() {
        return modelFramework;
    }
}
