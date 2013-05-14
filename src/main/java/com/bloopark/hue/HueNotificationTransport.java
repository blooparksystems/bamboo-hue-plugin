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
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
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
    private int reset_ms;
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;
    private HttpClient client;
    private ArrayList<String> bulpStates;
    private String color_success;
    private String color_failure;

    /*

        Descriptor of the new class

     */
    public HueNotificationTransport(String host, String port, String username, String bulps, String reset_ms, String color_success, String color_failure, ResultsSummaryManager resultsSummaryManager, AdministrationConfigurationManager administrationConfigurationManager)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.bulps = bulps;
        this.reset_ms = Integer.parseInt(reset_ms);
        this.resultsSummaryManager = resultsSummaryManager;
        this.administrationConfigurationManager = administrationConfigurationManager;
        this.client = new HttpClient();
        this.bulpStates = new ArrayList<String>();
        this.color_success = color_success;
        this.color_failure = color_failure;
    }

    /*
    *
    *   send notifcation method overwritten. We will just need the build summery events of SUC and FAI and send via put the light codes
    *
     */
    @Override
    public void sendNotification(@NotNull Notification notification)
    {
        Event event = notification.getEvent();


        if (event instanceof ChainResultEvent)
        {
            ResultsSummary result = getResultSummary(event);

            String color = "";

            if (result.getBuildState() == BuildState.FAILED)
                color = this.color_failure;
            else if  (result.getBuildState() == BuildState.SUCCESS)
                color = this.color_success;

            String[] bulp = this.bulps.split(",");

            for(int i=0;i<bulp.length;i++){

                getBulpState(i,bulp[i]);

                String json = getJsonFromColor(color);
                setBulpState(bulp[i], json);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                setBulpState(bulp[i],this.bulpStates.get(i));

            }

        }
    }

    /*
    *
    * create json by color
    *
     */
    private String getJsonFromColor(String color) {


        if(color == "green")
                return "{\"on\":true, \"hue\": 25500}";
        if(color == "red")
                return "{\"on\":true,\"hue\": 0}";
        if(color == "orange")
                return "{\"on\":true,\"hue\": 0}";
        if(color == "yellow")
                return "{\"on\":true,\"hue\": 0}";
        if(color == "blue")
                return "{\"on\":true,\"hue\": 46920}";


        return "{\"on\":true}";

    }

    /*
    *
    * set the bulp state via json
    *
     */
    private void setBulpState(String bulp_id, String json) {

        System.out.println(json);
         String url = "http://"+this.host+":"+this.port+"/api/"+this.username+"/lights/"+bulp_id+"/state";

         try{
            StringRequestEntity requestEntity = new StringRequestEntity(
                    json,
                    "application/json",
                    "UTF-8");


            PutMethod m = new PutMethod(url);
            m.setRequestEntity(requestEntity);

            client.executeMethod(m);

        }catch (java.io.UnsupportedEncodingException e){} catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /*
    *
    * add the config window to bamboo
    *
     */

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
    *
    * get the current bulp state and save it in an arraylist with the bulp_id as index
    *
     */
    private void getBulpState(int idx, String bulp_id){

        String url = "http://"+this.host+":"+this.port+"/api/"+this.username+"/lights/"+bulp_id;
        GetMethod get = new GetMethod(url);

        try {
            client.executeMethod(get);
            String response = get.getResponseBodyAsString();

            JSONObject jsonObject = new JSONObject( response );
            JSONObject state = jsonObject.getJSONObject("state");

            this.bulpStates.add(idx, state.toString());

            get.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
