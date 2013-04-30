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
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;
    private HttpClient client;

    public HueNotificationTransport(String host, String port, String username, ResultsSummaryManager resultsSummaryManager, AdministrationConfigurationManager administrationConfigurationManager)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.resultsSummaryManager = resultsSummaryManager;
        this.administrationConfigurationManager = administrationConfigurationManager;
        this.client = new HttpClient();
    }

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

            method = setupPutMethod(color);

            System.out.println(" >>>>>>>>>> STATE: " + color);
        }


            try
            {
                client.executeMethod(method);
                System.out.println(" >>>>>>>>>> RESULT: " + client.getState());
                System.out.println(" >>>>>>>>>> RESULT: " + method.getResponseBodyAsString());
                System.out.println(" >>>>>>>>>> RESULT: " + method.getRequestHeaders());
                System.out.println(" >>>>>>>>>> RESULT: " + method.getStatusText());
                System.out.println(" >>>>>>>>>> RESULT: " + method.getStatusCode());
            } catch (IOException e)
            {
                log.error("Error using Hue API: " + e.getMessage(), e);
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

    private PutMethod setupPutMethod(String color){

        String JSON = "";

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
              PutMethod m = new PutMethod("http://87.174.121.30:2342/api/newdeveloper/lights/4/state");
            m.setRequestEntity(requestEntity);
            return m;

        }catch (java.io.UnsupportedEncodingException e){}

        return null;


    }

}
