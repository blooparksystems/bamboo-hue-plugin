package com.bloopark.hue;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.event.ChainResultEvent;
import com.atlassian.bamboo.event.PlanResultEvent;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.event.Event;
import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: cheimke
 * Date: 29.04.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class HueNotificationTransport implements NotificationTransport {

    private static final Logger log = Logger.getLogger(HueNotificationTransport.class);
    private String host;
    private String username;
    private String port;
    private String bulps;
    private String alert;
    private int reset_ms;
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;
    private String color;
    private String state;
    private boolean reset = false;

    /*

        Descriptor of the new class

    ******************************************************************************************************************/
    public HueNotificationTransport(String host, String port, String username, String bulps, boolean reset, String reset_ms, String color, String alert, String state, ResultsSummaryManager resultsSummaryManager, AdministrationConfigurationManager administrationConfigurationManager)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.bulps = bulps;
        this.reset_ms = Integer.parseInt(reset_ms);
        this.resultsSummaryManager = resultsSummaryManager;
        this.administrationConfigurationManager = administrationConfigurationManager;
        this.color = color;
        this.state = state;
        this.reset = reset;
        this.alert = alert;
    }

    /*
    *
    *   send notifcation method overwritten. We will just need the build summery events of SUC and FAI and send via put the light codes
    *
     ******************************************************************************************************************/
    @Override
    public void sendNotification(@NotNull Notification notification)
    {
        Event event = notification.getEvent();



        if (event instanceof ChainResultEvent){

            ResultsSummary result = getResultSummary(event);
            if(result.getBuildState() == BuildState.FAILED){
                createHueRequest(Constants.BLOOPARK_STATE_FAILED);
            }else if (result.getBuildState() == BuildState.SUCCESS){
                createHueRequest(Constants.BLOOPARK_STATE_SUCCESS);
            }else{
                createHueRequest(Constants.BLOOPARK_STATE_UNKNOWN);
            }

        }else{
            createHueRequest(null);
        }

    }

    /*
   *
   *   create the request
   *
    ******************************************************************************************************************/
    private void createHueRequest(String state){
        String[] bulp = this.bulps.split(",");

        for(int i=0;i<bulp.length;i++){
            if((state.equals(this.state)) || (state == null)){
                String url = "http://"+this.host+":"+this.port+"/api/"+this.username+"/lights/"+bulp[i];
                HueSetColor hsc = new HueSetColor(url,color,alert,bulp[i],this.reset,this.reset_ms);
                hsc.start();
            }
        }
    }


    /*
    *
    * add the config window to bamboo
    *
     ******************************************************************************************************************/

    private ResultsSummary getResultSummary(Event event)
    {
        if (event instanceof PlanResultEvent)
        {
            PlanResultEvent planResultEvent = (PlanResultEvent) event;
            return resultsSummaryManager.getResultsSummary(planResultEvent.getPlanResultKey());
        }

        return null;
    }




}
