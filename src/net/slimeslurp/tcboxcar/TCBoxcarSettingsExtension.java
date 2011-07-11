package net.slimeslurp.tcboxcar;

import javax.servlet.http.HttpServletRequest;

import jetbrains.buildServer.web.openapi.SimplePageExtension;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;

public class TCBoxcarSettingsExtension extends SimplePageExtension {
    
    public TCBoxcarSettingsExtension(PagePlaces pagePlaces) {    
        super(pagePlaces);        
        setIncludeUrl("tcboxcarSettings.jsp");
        setPlaceId(PlaceId.NOTIFIER_SETTINGS_FRAGMENT);
        setPluginName("tcboxcar");
        register();
    }
    
    public boolean isAvailable(HttpServletRequest request) {
        return super.isAvailable(request);
    }    
}