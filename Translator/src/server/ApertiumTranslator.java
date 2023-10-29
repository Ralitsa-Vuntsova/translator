package server;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApertiumTranslator {
    public ApertiumTranslator() {}

    public String translateWord(String word, String languageFrom, String languageTo) throws IOException, ParseException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://apertium.org/apy/translate");

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("langpair", languageFrom + "|" + languageTo));
        params.add(new BasicNameValuePair("q", word));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        String response = getResponse(httpclient, httppost);
        if (response.isEmpty()) {
            return "Word is empty and cannot be translated.";
        } else {
            return response;
        }
    }

    private String getResponse(HttpClient httpclient, HttpPost httppost) throws IOException, ParseException {
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        String responseString = new String();
        if (entity != null) {
            String entityToString = EntityUtils.toString(entity);
            JSONParser parser = new JSONParser();
            JSONObject responseJson = (JSONObject) parser.parse(entityToString);
            JSONObject responseData = (JSONObject) responseJson.get("responseData");
            responseString = (String) responseData.get("translatedText");
        }

        if (responseString.isEmpty()) {
            return "Word is empty and cannot be translated.";
        } else {
            return responseString;
        }
    }
}
