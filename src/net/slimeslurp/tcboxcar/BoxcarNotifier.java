package net.slimeslurp.tcboxcar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Collection;


import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.UserPropertyInfo;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.SProject;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vcs.VcsRoot;

import org.apache.http.HttpException;


/**
 * Sends build notifications via Boxcar 
 *
 * @author Nathanial Drake 
 */
public class BoxcarNotifier implements Notificator {

    private static final Logger LOG = Logger.getInstance(BoxcarNotifier.class.getName());
    
    /** Holds the user configuration properties */
    private ArrayList<UserPropertyInfo> userProps;
    
    private static final String TYPE = "tcboxcarNotifier";
    private static final String BOXCAR_EMAIL_KEY = "tcboxcar.email";
    private static final String BOXCAR_PASSWORD_KEY = "tcboxcar.password";
    public static final String APP_NAME = "Team City";
        
    private static final PropertyKey EMAIL_KEY = new NotificatorPropertyKey(TYPE, BOXCAR_EMAIL_KEY);
    private static final PropertyKey PASSWORD_KEY = new NotificatorPropertyKey(TYPE, BOXCAR_PASSWORD_KEY);
        
    /** 
     * Map to hold info each user that has added this notifier
     */
    private Hashtable<String, Boolean> configMap;
    
    /**
     * 
     */
    public BoxcarNotifier(NotificatorRegistry nr) {
        LOG.warn("Registering BoxcarNotifier...");
        userProps = new ArrayList<UserPropertyInfo>();
        userProps.add(new UserPropertyInfo(BOXCAR_EMAIL_KEY, "Boxcar Email"));
        userProps.add(new UserPropertyInfo(BOXCAR_PASSWORD_KEY, "Boxcar Password"));
        nr.register(this, userProps);
    }
    
    public String getDisplayName() {
        return "Boxcar Notifier";
    }
    
    public String getNotificatorType() {
        return TYPE;
    }
	

	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyBuildFailed(jetbrains.buildServer.serverSide.SRunningBuild, java.util.Set)
	 */
	public void notifyBuildFailed(SRunningBuild srb, Set<SUser> users) {		
		LOG.warn("notifyBuildFailed");
		doNotifications("Build " + srb.getFullName() + " failed " + 
		                srb.getBuildNumber(), users);
	}
	
	public void notifyBuildFailedToStart(SRunningBuild build, Set<SUser> users) {
	    LOG.warn("notifyBuildFailedToStart");
	}

	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyBuildFailing(jetbrains.buildServer.serverSide.SRunningBuild, java.util.Set)
	 */
	public void notifyBuildFailing(SRunningBuild srb, Set<SUser> users) {		
	    LOG.warn("notifyBuildFailing");
	    doNotifications("Build " + srb.getFullName() + " failing " + srb.getBuildNumber(), users);
	}

	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyBuildProbablyHanging(jetbrains.buildServer.serverSide.SRunningBuild, java.util.Set)
	 */
	public void notifyBuildProbablyHanging(SRunningBuild srb, Set<SUser> users) {	
	    LOG.warn("notifyBuildProbablyHanging");	
	    doNotifications("Build " + srb.getFullName() + " probably hanging " + srb.getBuildNumber(), users);
	}

	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyBuildStarted(jetbrains.buildServer.serverSide.SRunningBuild, java.util.Set)
	 */
	public void notifyBuildStarted(SRunningBuild srb, Set<SUser> users) {		
	    LOG.warn("notifyBuildStarted");
        doNotifications("Build " + srb.getFullName() + " started " + srb.getBuildNumber(), users);
	}

	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyBuildSuccessful(jetbrains.buildServer.serverSide.SRunningBuild, java.util.Set)
	 */
	public void notifyBuildSuccessful(SRunningBuild srb, Set<SUser> users) {		
	    LOG.warn("notifyBuildSuccessful");
	    doNotifications("Build " + srb.getFullName() + " successful " + srb.getBuildNumber(), users);
	}

	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyResponsibleChanged(jetbrains.buildServer.serverSide.SBuildType, java.util.Set)
	 */
	public void notifyResponsibleChanged(SBuildType sbt, Set<SUser> users) {		
	    LOG.warn("notifyResponsibleChanged");
	    doNotifications("Responsible user changed...", users);
	}
	
	public void notifyResponsibleChanged(Collection<TestName> testNames,
	                                     ResponsibilityEntry respEntry,
	                                     SProject project,
	                                     Set<SUser> users)
	{
	    LOG.warn("notifyResponsibleChanged");
	    doNotifications("Responsible user changed for tests...", users);
	}

	public void notifyResponsibleAssigned(Collection<TestName> testNames, 
	                                      ResponsibilityEntry respEntry,
	                                      SProject project,
	                                      Set<SUser> users)
    {
        LOG.warn("notifyResponsibleAssigned");
        doNotifications("Responsible user assigned", users);
    }	                                      

	public void notifyResponsibleAssigned(TestNameResponsibilityEntry tnre,
	                                      TestNameResponsibilityEntry tnre2,
	                                      SProject project,
	                                      Set<SUser> users)
	{
	    LOG.warn("notifyResponsibleAssigned");
	}
	
	public void notifyResponsibleChanged(TestNameResponsibilityEntry tnre,
	                                      TestNameResponsibilityEntry tnre2,
	                                      SProject project,
	                                      Set<SUser> users)
	{
	    LOG.warn("notifyResponsibleChanged");
	}	
    
	
	/* (non-Javadoc)
	 * @see jetbrains.buildServer.notification.Notificator#notifyLabelingFailed(jetbrains.buildServer.Build, jetbrains.buildServer.vcs.VcsRoot, java.lang.Throwable, java.util.Set)
	 */
	public void notifyLabelingFailed(jetbrains.buildServer.Build build,
	        jetbrains.buildServer.vcs.VcsRoot root,
	        java.lang.Throwable t,
	        java.util.Set<jetbrains.buildServer.users.SUser> users) {
	    LOG.warn("notifyLabelingFailed");
	    doNotifications("Labeling failed...", users);
	}
	
	public void notifyResponsibleAssigned(SBuildType sbt, Set<SUser> users) {		
	    LOG.warn("notifyResponsibleAssigned");
	    //doNotifications("Responsible user assigned...", users);
	}
	
	public void notifyTestsUnmuted(Collection<STest> tests, 
	                   jetbrains.buildServer.serverSide.mute.MuteInfo muteInfo,
	                   Set<SUser> users) {
	    LOG.warn("notifyTestUnmuted");
	    doNotifications("Tests Unmuted...", users);
	                       
    }
    
	public void notifyTestsMuted(Collection<STest> tests, 
	                   jetbrains.buildServer.serverSide.mute.MuteInfo muteInfo,
	                   Set<SUser> users) {
	    LOG.warn("notifyTestMmuted");
	    doNotifications("Tests Muted...", users);
	                       
    }    
	
	/**
	 * Send the boxcar notifications to the specified users.
	 * 
	 * @param message The message to send
	 * @param users The users to notify
	 *
	 */
	private void doNotifications(String message, Set<SUser> users) {

	    
	    for(SUser user : users) {	        
            LOG.warn("notifying user: " + user.getUsername());
            String username = user.getUsername();
            String boxcarEmail = user.getPropertyValue(EMAIL_KEY);
            String boxcarPasswd = user.getPropertyValue(PASSWORD_KEY);

            try {
                BoxcarApi.sendNotification(boxcarEmail, boxcarPasswd, message, "Teamcity", 
                    (int)System.currentTimeMillis());            
            } catch(IOException e) {
                LOG.warn("IOException sending boxcar notification to user: " + username, e);  
            } catch(HttpException e) {
                LOG.warn("HttpException sending boxcar notification to user: " + username, e);  
            }
	    }    
	    
	}
	
}
