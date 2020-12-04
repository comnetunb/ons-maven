package ons.util.ml;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpRemoteModel<IT, OT> implements Model<IT, OT> {
    private final HttpClient httpClient;
    private final String endpointUrl;
    private final Gson gson;

    public HttpRemoteModel(String endpointUrl) {
        this.httpClient = HttpClientBuilder.create().build();
        this.endpointUrl = endpointUrl;
        this.gson = new Gson();
    }

    private class Request {
        IT input;
    }

    private class Response {
        OT output;
    }

    public OT predict(IT input) throws Exception {
        var request = new Request();
        request.input = input;

        var httpPost = new HttpPost(endpointUrl);
        var entity = new StringEntity(gson.toJson(request));

        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "application/json");

        var response = httpClient.execute(httpPost);

        var statusCode = response.getStatusLine().getStatusCode();

        if (200 > statusCode || statusCode >= 300){
            throw new HttpResponseException(statusCode, response.getStatusLine().getReasonPhrase());
        }

        var jsonString = EntityUtils.toString(response.getEntity());
        var responseObj = gson.fromJson(jsonString, Response.class);

        return (OT)responseObj.output;
    }

    public ModelFramework getModelFramework() {
        return ModelFramework.UNKNOWN;
    }
}
