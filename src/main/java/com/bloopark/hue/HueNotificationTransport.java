package com.bloopark.hue;
import com.atlassian.bamboo.builder.BuildState;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.event.ChainResultEvent;
import com.atlassian.bamboo.event.PlanResultEvent;
import com.atlassian.bamboo.notification.Notification;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.user.gravatar.GravatarService;
import com.atlassian.event.Event;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nonnull;
import java.net.*;
import java.io.*;
import java.io.IOException;
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
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;
    private HttpClient client;

    /*

        Descriptor of the new class

     */
    public HueNotificationTransport(String host, String port, String username, String bulps, ResultsSummaryManager resultsSummaryManager, AdministrationConfigurationManager administrationConfigurationManager)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.bulps = bulps;
        this.resultsSummaryManager = resultsSummaryManager;
        this.administrationConfigurationManager = administrationConfigurationManager;
        this.client = new HttpClient();
    }

    /*

        send notifcation method overwritten. We will just need the build summery events of SUC and FAI and send via put the light codes
     */
    @Override
    public void sendNotification(@NotNull Notification notification)
    {
        Event event = notification.getEvent();


        System.out.println("#####" + event);
        PutMethod method = null;

        if (event instanceof ChainResultEvent)
        {
            ResultsSummary result = getResultSummary(event);

            String color = "unknown";

            if (result.getBuildState() == BuildState.FAILED)
                color = "red";
            else if  (result.getBuildState() == BuildState.SUCCESS)
                color = "green";

            try
            {
                String[] bulp = this.bulps.split(",");

                for(int i=0;i<bulp.length;i++){
                    method = setupPutMethod(color, bulp[i]);
                    client.executeMethod(method);
                }

            } catch (IOException e)
            {
                log.error("Error using Hue API: " + e.getMessage(), e);
            }

        }
    }


    private ResultsSummary getResultSummary(Event event)
    {
        if (event instanceof PlanResultEvent)
        {
            PlanResultEvent planResultEvent = (PlanResultEvent) event;
            return resultsSummaryManager.getResultsSummary(planResultEvent.getPlanResultKey());
        }

        return null;
    }


    /*
        PUT the JSON stuff to the API
     */
    private PutMethod setupPutMethod(String color, String bulp_id){

        String JSON = "";
        String url = "http://"+this.host+":"+this.port+"/api/"+this.username+"/lights/"+bulp_id+"/state";

        if(color == "green"){
            JSON = "{\"on\":true, \"hue\": 25500}";

        }else{
            JSON = "{\"on\":true,\"hue\": 0, \"alert\": \"lselect\"}";
        }
        try{
            StringRequestEntity requestEntity = new StringRequestEntity(
                    JSON,
                    "application/json",
                    "UTF-8");


              PutMethod m = new PutMethod(url);
            m.setRequestEntity(requestEntity);
            return m;

        }catch (java.io.UnsupportedEncodingException e){}

        return null;

    }

}
