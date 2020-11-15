package ons.util.ml;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class OnnxModel<IT, OT> implements Model<IT, OT> {
    protected OrtSession session;
    protected OrtEnvironment environment;
    protected String inputName;

    public void load(String path) throws IOException, OrtException {
        load(path, ModelFormat.ONNX);
    }

    public void load(String path, ModelFormat format) throws IOException, OrtException {
        InputStream targetStream = new FileInputStream(path);
        load(targetStream, format);
    }

    public void load(InputStream inputStream) throws IOException, OrtException {
        load(inputStream, ModelFormat.ONNX);
    }

    public void load(InputStream is, ModelFormat format) throws IOException, OrtException {
        if (format != ModelFormat.ONNX) {
            throw new UnsupportedOperationException("Unsupported format");
        }

        environment = OrtEnvironment.getEnvironment();
        session = environment.createSession(is.readAllBytes());
        inputName = session.getInputNames().iterator().next();
    }
}
