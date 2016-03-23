package eu.tnova.crat.cplb.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import eu.tnova.crat.cplb.data.Constants;
import eu.tnova.crat.cplb.data.TempData;

public class ODLRESTClient {

	// Create a local instance of cookie store
    public static CookieStore cookieStore; 
    // Create local HTTP context
    public static HttpClientContext localContext;
    // Bind custom cookie store to the local context
    static {
    	cookieStore = new BasicCookieStore();
    	localContext = new HttpClientContext();
    	localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);	
    }
    
	public static JSONObject post(String url) throws Exception {
		return post(url, null);
	}
    public static JSONObject post(String url, JSONObject body) throws ClientProtocolException, IOException {
    	TempData.LOGGER.info("POST "+url);
    	TempData.LOGGER.info("Body "+body);
    	
    		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(url);
            
            String authStr = Constants.ODL_username + ":" + Constants.ODL_password;
            String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());
            
            request.addHeader("Authorization", "Basic " + encodedAuthStr);
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");

            if (body != null) {
                request.setEntity(new StringEntity(body.toString()));
            }

            HttpResponse result = httpClient.execute(request, localContext);

            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            /*JSONParser parser = new JSONParser();
            Object resultObject = parser.parse(json);

             if (resultObject instanceof JSONObject) {
                    JSONObject obj =(JSONObject)resultObject;
                    return obj;
             }*/
            
            TempData.LOGGER.info("Response "+json);

        return new JSONObject(json);
    }
    
    
    public static JSONObject get(String url) throws ClientProtocolException, IOException {
    	
    	TempData.LOGGER.info("GET "+url);
    	
    	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        
        
        String authStr = Constants.ODL_username + ":" + Constants.ODL_password;
        String encodedAuthStr = Base64.encodeBase64String(authStr.getBytes());
        
        request.addHeader("Authorization", "Basic " + encodedAuthStr);
        request.addHeader("Accept", "application/json");
        request.addHeader("Content-Type", "application/json");

        HttpResponse result = httpClient.execute(request, localContext);

        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
        /* JSONParser parser = new JSONParser();
        Object resultObject = parser.parse(json);

         if (resultObject instanceof JSONObject) {
                JSONObject obj =(JSONObject)resultObject;
                return obj;
         } */
        
        TempData.LOGGER.info("Response "+json);
        

    return new JSONObject(json);
}
}