package net.slimeslurp.tcboxcar;

import javax.servlet.http.HttpServletRequest;

import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;

public class TCBoxcarSettingsExtension extends SimplePageExtension {
    
    private static final String PLUGIN_NAME = "tcboxcar";
    
    public TCBoxcarSettingsExtension(PagePlaces pagePlaces) {    
        super(pagePlaces);        
        setIncludeUrl("tcboxcarSettings.jsp");
        setPlaceId(PlaceId.NOTIFIER_SETTINGS_FRAGMENT);
        setPluginName(PLUGIN_NAME);
        register();
    }
    
    public boolean isAvailable(HttpServletRequest request) {
        // Only show this when displaying our notificator settings
        String notificatorParam = request.getParameter("notificatorType");
        return notificatorParam != null && notificatorParam.equals("tcboxcarNotifier");
    }    
}