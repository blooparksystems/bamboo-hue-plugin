package com.bloopark.hue;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.*;


public class HueSetColor extends Thread {

    private String current_state    = null;
    private String target_state     = null;
    private String color            = null;
    private String bulpID           = null;
    private String alert            = "none";
    private int reset_time          = 0;
    private boolean reset_color     = false;
    private String url              = null;
    private HttpClient client;
    private static final Logger log = Logger.getLogger(HueSetColor.class);
    /*
    *
    * Contructor for setting all data we need
    ******************************************************************************************************************/
    public HueSetColor(String url, String color, String alert, String bulpID, boolean reset, int reset_time){
        this.url            = url;
        this.color          = color;
        this.bulpID         = bulpID;
        this.reset_color    = reset;
        this.reset_time     = reset_time;
        this.alert          = alert;

        this.client = new HttpClient();

        this.current_state  = this.getBulpState();
        this.target_state   = this.getJsonFromColor(this.color);

    }

    /*
    *
    * run the thread
     ******************************************************************************************************************/
    public void run() {

        if(reset_color){
            this.current_state = getBulpState();

            try {
                this.setBulpState(this.target_state);
                sleep(reset_time);
                this.setBulpState(this.current_state);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else{

            this.setBulpState(this.target_state);
        }

    }

    /*
    *
    * set the bulp state via json
    *
    ******************************************************************************************************************/
    private void setBulpState(String json) {

        String url_state = url + "/state";

        try{
            StringRequestEntity requestEntity = new StringRequestEntity(
                    json,
                    "application/json",
                    "UTF-8");

            PutMethod m = new PutMethod(url_state);
            m.setRequestEntity(requestEntity);

            client.executeMethod(m);
        } catch (UnknownHostException e) {
            log.error("############# HUE API ###############");
            log.error("Could not connect to HUE API: " + url);
            log.error(e.getMessage());
        } catch (ConnectException e) {
            log.error("############# HUE API ###############");
            log.error("Could not connect to HUE API: " + url);
            log.error(e.getMessage());
        } catch (SocketException e) {
            log.error("############# HUE API ###############");
            log.error("Could not connect to HUE API: " + url);
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error("############# HUE API ###############");
            log.error("Could not connect to HUE API: " + url);
            log.error(e.getMessage());
        }

    }


    /*
    *
    * get the current bulp state and save it in an arraylist with the bulp_id as index
    *
     ******************************************************************************************************************/
    private String getBulpState(){

        String json = "";

        GetMethod get = new GetMethod(url);

        try {
            client.executeMethod(get);
            String response = get.getResponseBodyAsString();

            JSONObject jsonObject = new JSONObject( response );
            JSONObject state = jsonObject.getJSONObject("state");

            json = state.toString();

            get.releaseConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }


    /*
    *
    * create json by color
    *
     ******************************************************************************************************************/
    private String getJsonFromColor(String color) {

        String json = "{\"on\":true, \"alert\": \"__alert__\"}";

        color = color.trim();
        if(color.equals("green"))
            json = "{\"on\":true, \"bri\":206,\"hue\":24764,\"sat\":226,\"xy\":[0.4136,0.4967],\"ct\":296, \"alert\": \"__alert__\"}";
        if(color.equals("red"))
            json = "{\"on\":true,\"hue\": 0, \"alert\": \"__alert__\"}";
        if(color.equals("orange"))
            json = "{\"on\":true,\"bri\":206,\"hue\":10541,\"sat\":237,\"xy\":[0.4922,0.3897],\"ct\":424, \"alert\": \"__alert__\"}";
        if(color.equals("yellow"))
            json = "{\"on\":true,\"bri\":206,\"hue\":15665,\"sat\":161,\"xy\":[0.4382,0.4045],\"ct\":494, \"alert\": \"__alert__\"}";
        if(color.equals("blue"))
            json = "{\"on\":true,\"bri\":206,\"hue\":46282,\"sat\":252,\"xy\":[0.1779,0.0608],\"ct\":500, \"alert\": \"__alert__\"}";

        json = json.replace("__alert__", this.alert);

        return json;

    }
}
