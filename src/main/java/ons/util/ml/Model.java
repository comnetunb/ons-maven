package ons.util.ml;

import java.io.InputStream;

public interface Model<IT, OT> {
    OT predict(IT input) throws Exception;
}
