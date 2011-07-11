<%@ include file="/include.jsp" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<script type="text/javascript">
    function sendTest() {
    
        var gs = $('properties[tcboxcar.email].value');
        if(!gs || gs.value.length == 0) {
            alert("Please enter your Boxcar email address.");
            return;
        }
        var gp = $('properties[tcboxcar.gPassword].value');
        
        var gm = $('boxcarTestMessage').value;
        if(!gm || gm.length ==0) {
            return;
        }
    
        BS.ajaxRequest($('boxcarTestForm').action, {
            parameters: 'boxcarServer='+ gs.value + (gp&&gp.value.length>0?'&boxcarPassword='+gp.value+'&':'&')+'boxcarTestMessage='+gm,
            onComplete: function(transport) {
              if (transport.responseXML) {
                  $('tcboxcarTest').refresh();
              }             
            }
        });
        return false;        
    }
</script>

<bs:refreshable containerId="tcboxcarTest" pageUrl="${pageUrl}">
<bs:messages key="tcboxcarMessage"/>

<form action="/tcboxcarSettings.html" method="post" id="boxcarTestForm">
Send test message to boxcar server: <input id="boxcarTestMessage" name="boxcarTestMessage" type="text" />  <input type="button" name="Test" value="Test" onclick="return sendTest();"/>
</form>
</bs:refreshable>