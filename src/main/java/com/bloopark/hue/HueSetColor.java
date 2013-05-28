package com.bloopark.hue;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;

public class HueSetColor extends Thread {

    private String current_state    = null;
    private String target_state     = null;
    private String color            = null;
    private String bulpID           = null;
    private int reset_time          = 0;
    private boolean reset_color     = false;
    private String url              = null;
    private HttpClient client;
    /*
    *
    * Contructor for setting all data we need
    ******************************************************************************************************************/
    public HueSetColor(String url, String color, String bulpID, boolean reset, int reset_time){
        this.url            = url;
        this.color          = color;
        this.bulpID         = bulpID;
        this.reset_color    = reset;
        this.reset_time     = reset_time;

        this.client = new HttpClient();

        this.current_state  = this.getBulpState();
        this.target_state   = this.getJsonFromColor(this.color);

    }

    /*
    *
    * run the thread
     ******************************************************************************************************************/
    public void run() {

        this.setBulpState(this.target_state);

        if(reset_color){
            try {
                sleep(reset_time);
                this.setBulpState(this.current_state);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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

        }catch (java.io.UnsupportedEncodingException e){} catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

        color = color.trim();

        if(color.equals("green"))
            return "{\"on\":true, \"hue\": 25500}";
        if(color.equals("red"))
            return "{\"on\":true,\"hue\": 0}";
        if(color.equals("orange"))
            return "{\"on\":true,\"hue\": 0}";
        if(color.equals("yellow"))
            return "{\"on\":true,\"hue\": 0}";
        if(color.equals("blue"))
            return "{\"on\":true,\"hue\": 46920}";

        return "{\"on\":true}";

    }
}
