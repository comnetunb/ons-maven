package ons.util.ml;

import java.io.InputStream;

public interface Model<IT, OT> {
    void load(String path) throws Exception;
    void load(String path, ModelFormat format) throws Exception;
    void load(InputStream inputStream) throws Exception;
    void load(InputStream inputStream, ModelFormat format) throws Exception;

    OT predict(IT input) throws Exception;
}
