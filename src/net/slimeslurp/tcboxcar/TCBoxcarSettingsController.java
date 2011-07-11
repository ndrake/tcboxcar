package net.slimeslurp.tcboxcar;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.AjaxRequestProcessor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.XmlResponseUtil;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.WebResourcesManager;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.version.ServerVersionHolder;
import jetbrains.buildServer.version.ServerVersionInfo;
import jetbrains.buildServer.util.PropertiesUtil;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import com.intellij.openapi.diagnostic.Logger;

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
import org.apache.http.HttpStatus;
import org.apache.http.HttpException;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * Controller to handle test form on TCBoxcar Settings page.
 * 
 * Based on the TC Profiler plugin controller. 
 *   http://www.jetbrains.net/confluence/display/TW/Server+Profiling
 * 
 * @author Nathanial Drake
 */
public class TCBoxcarSettingsController extends BaseController {

    private static final Logger LOG = Logger.getInstance(TCBoxcarSettingsController.class.getName());

    public TCBoxcarSettingsController(final SBuildServer server, WebControllerManager manager,
                                      WebResourcesManager webResources) {
        super(server);
        manager.registerController("/tcboxcarSettings.html", this);

        //in TeamCity < 4.x EAP we need to register plugin resources automatically.
        final ServerVersionInfo serverVersionInfo = ServerVersionHolder.getVersion();
        if (serverVersionInfo.getDisplayVersionMajor()<4){
            webResources.addPluginResources("tcBoxcar", "tcboxcar.jar");
        }
    }

    @Nullable
    protected ModelAndView doHandle(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        new AjaxRequestProcessor().processRequest(request, response, new AjaxRequestProcessor.RequestHandler() {
            public void handleRequest(final HttpServletRequest request, final HttpServletResponse response, final Element xmlResponse) {
                try {
                    doAction(request);
                } catch (Exception e) {
                    Loggers.SERVER.warn(e);
                    ActionErrors errors = new ActionErrors();
                    errors.addError("tcboxcarProblem", getMessageWithNested(e));
                    errors.serialize(xmlResponse);
                }
            }
            });

            return null;
    }

    static private String getMessageWithNested(Throwable e) {
        String result = e.getMessage();
        Throwable cause = e.getCause();
        if (cause != null) {
            result += " Caused by: " + getMessageWithNested(cause);
        }
        return result;
    }

    private void doAction(final HttpServletRequest request) throws Exception {
        String boxcarEmail = request.getParameter("boxcarEmail");
        String boxcarPassword = request.getParameter("boxcarPassword");
        String msg = request.getParameter("boxcarTestMessage");

        // Do Boxcar Send
        LOG.warn("doAction boxcar settings controller");
        try {
            
            DefaultHttpClient client = new DefaultHttpClient(); 

            client.getCredentialsProvider().setCredentials(
                new AuthScope("boxcar.io", 443, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(boxcarEmail, boxcarPassword));
                
                
            //CredentialsProvider credsProvider = new BasicCredentialsProvider();
            //Credentials defaultcreds = new UsernamePasswordCredentials(boxcarEmail, boxcarPassword);
            //credsProvider.setCredentials(new AuthScope("boxcar.io", 443, AuthScope.ANY_REALM), defaultcreds);
            
            // Send data
		    HttpPost http = new HttpPost("https://boxcar.io/notification");

		    //POST with login
		    List <BasicNameValuePair> nvps = new ArrayList <BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("email", boxcarEmail));
            nvps.add(new BasicNameValuePair("notification[from_screen_name]", "TeamCity"));
            nvps.add(new BasicNameValuePair("notification[message]", msg));
            http.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse response = client.execute(http);
            //HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            	throw new HttpException();
            }
            
            // URL url = new URL ("https://boxcar.io/notification");
            // String encoding = Base64Encoder.encode (growlEmail+":"+growlPassword);
            // 
            // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setRequestMethod("POST");
            // connection.setDoOutput(true);
            // connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            // InputStream content = (InputStream)connection.getInputStream();
            // BufferedReader in   = 
            //     new BufferedReader (new InputStreamReader (content));
            // String line;
            // while ((line = in.readLine()) != null) {
            //     System.out.println(line);
            // }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void addSuccessMessage(HttpServletRequest request, String result) {
        if (result != null) {
            getOrCreateMessages(request).addMessage("tcboxcarMessage", result);
        }
    }
}
