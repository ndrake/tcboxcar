package net.slimeslurp.tcboxcar;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.HttpException;


/**
 * Simple Java utility to send Boxcar notifications via the Users API.
 * 
 * Requires HttpClient 4 (http://hc.apache.org/index.html)
 *
 * http://boxcar.io/help/api/users
 *
 */
public class BoxcarApi {
        
    private static final String HOST = "boxcar.io";
    private static final String POST_URL = "https://boxcar.io/notifications";
    
    /**
     * Send a notification via Boxcar Users API.
     *
     * @param email User's Boxcar username
     * @param password User's Boxcar password
     * @param message The message to send
     * @param fromScreenName User or application sending the notification
     * @param fromRemoteServiceId Unique id for the notification
     * 
     * @throws IOException
     * @throws HttpException
     */
    public static void sendNotification(String email, 
                                        String password,
                                        String message,
                                        String fromScreenName,
                                        Integer fromRemoteServiceId) 
        throws IOException, HttpException 
    {

        DefaultHttpClient client = new DefaultHttpClient(); 

        client.getCredentialsProvider().setCredentials(
            new AuthScope(HOST, 443, AuthScope.ANY_REALM),
            new UsernamePasswordCredentials(email, password));
            
                        
	    HttpPost http = new HttpPost(POST_URL);

	    //POST with login
	    List <BasicNameValuePair> nvps = new ArrayList <BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("email", email));
        nvps.add(new BasicNameValuePair("notification[from_screen_name]", fromScreenName));
        nvps.add(new BasicNameValuePair("notification[message]", message));
        nvps.add(new BasicNameValuePair("notification[from_remote_service_id]", 
            (fromRemoteServiceId != null ? fromRemoteServiceId.toString() : 
                                           String.valueOf(System.currentTimeMillis()))));
        http.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = client.execute(http);

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {                
        	throw new HttpException( "Bad response code: " + response.getStatusLine().getStatusCode());
        }
        
        
    }
    
}