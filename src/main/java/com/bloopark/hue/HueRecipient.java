package com.bloopark.hue;

import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.spring.ComponentAccessor;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.google.common.base.Supplier;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cheimke
 * Date: 29.04.13
 * Time: 13:03
 * To change this template use File | Settings | File Templates.
 */
public class HueRecipient extends AbstractNotificationRecipient {
    private String host         = null;
    private String port         = null;
    private String username     = null;
    private String state        = null;
    private String reset_ms     = null;
    private String color        = null;
    private String alert        = null;
    private boolean reset       = false;
    private String bulp_id      = "";
    private List<Bulp> bulps          = new ArrayList<Bulp>();
    private HttpClient client;

    private TemplateRenderer templateRenderer;
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;

    public static final Supplier<AdministrationConfigurationManager> ADMINISTRATION_CONFIGURATION_MANAGER = ComponentAccessor.newLazyComponentReference("administrationConfigurationManager");
    /*
        this is need to save the reuse the settings
     ******************************************************************************************************************/
    @Override
    public void populate(@NotNull Map<String, String[]> params)
    {
        for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); )
        {
            String next = iterator.next();
        }

       if (params.containsKey("hue_bulp"))
           bulp_id = params.get("hue_bulp")[0];
        if (params.containsKey("hue_reset")) {
            if(params.get("hue_reset")[0].equals("true"))
                reset = true;
        }
        if (params.containsKey("hue_reset_ms"))
            reset_ms = params.get("hue_reset_ms")[0];
        if (params.containsKey("hue_color"))
            color = params.get("hue_color")[0];
        if (params.containsKey("hue_state"))
            state = params.get("hue_state")[0];
        if (params.containsKey("hue_alert"))
            alert = params.get("hue_alert")[0];

    }

    /*
        INIT the setting from bamboo config
     ******************************************************************************************************************/
    @Override
    public void init(@Nullable String configurationData)
    {
        int firstIdx = configurationData.indexOf(';');
        if (firstIdx > 0)
        {
            String conf[]   = configurationData.split(";");

            this.bulp_id      = conf[0];

            if(conf[1].equals("true"))
                this.reset = true;

            this.reset_ms   = conf[2];
            this.color      = conf[3];
            this.state      = conf[4];
            this.alert      = conf[5];

        }
    }


    /*
        get the bulp IDs and names via API call
     ******************************************************************************************************************/
    private void getBulps(){
        // getting the setting from Admin Backend
        final AdministrationConfiguration administrationConfiguration = ADMINISTRATION_CONFIGURATION_MANAGER.get().getAdministrationConfiguration();

        this.host = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_HOST);
        this.port = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_PORT);
        this.username = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_USER);

        String url = "http://"+this.host+":"+this.port+"/api/"+this.username+"/lights";

        GetMethod get = new GetMethod(url);

        try {
            client = new HttpClient();
            client.executeMethod(get);
            String response = get.getResponseBodyAsString();

            JSONObject jsonObject = new JSONObject( response );

            JSONObject subObject;

            if((jsonObject != null) && (jsonObject.length()>0)){
                bulps.clear();
                for(int i=1;i<=jsonObject.length();i++){
                    subObject = jsonObject.getJSONObject(String.valueOf(i)) ;
                    if(subObject != null){
                        bulps.add(new Bulp(String.valueOf(i),subObject.getString("name")));
                    }
                }
            }
            get.releaseConnection();
        } catch (IOException e) {

        }


    }

    /*
        put the config into bamboo
     ******************************************************************************************************************/
    @NotNull
    @Override
    public String getRecipientConfig()
    {

        String reset_str = "false";
        if(reset){
            reset_str = "true";
        }
        return bulp_id + ';' + reset_str + ';' + reset_ms + ';' + color + ';' + state + ';' + alert;
    }

    /*
        display the setting pannel on the notification tab
     */
    @NotNull
    @Override
    public String getEditHtml()
    {
        getBulps();
        Map context = new HashMap();

        if (this.reset_ms != null)
            context.put("hue_reset_ms", this.reset_ms);

        if(this.color == null){
            context.put("hue_color", "green");
        }else{
            context.put("hue_color", this.color);
        }

        if(this.alert == null){
            context.put("hue_alert", "none");
        }else{
            context.put("hue_alert", this.alert);
        }

        if(this.state == null){
            context.put("hue_state", "successfull");
        }else{
            context.put("hue_state", this.state);
        }

        if(this.reset){
            context.put("hue_reset","true");
        }


        context.put("bulps", bulps);

        return templateRenderer.render("editHue.ftl", context);
    }

    /*
        DISPLAY the settings
     ******************************************************************************************************************/
    @NotNull
    @Override
    public String getViewHtml()
    {

        getBulps();
        String reset_str = "";

        if(reset){
            reset_str = "<br/>Reset time: " + this.reset_ms;
        }

        Bulp b = this.bulps.get(Integer.valueOf(this.bulp_id)-1);
        return "<b>Hue</b>"
                + "<br/>Bulp: " + b.getName() + " (ID: " + this.bulp_id + ")"
                + reset_str
                + "<br/>Color: " + this.color
                + "<br/>Alert: " + this.alert
                + "<br/>State: " + this.state;
    }


    /*
        Add our new notification option to bamboo notifications list
     ******************************************************************************************************************/
    @NotNull
    @Override
    public List<NotificationTransport> getTransports()
    {
        ArrayList list = new ArrayList();
        // getting the setting from Admin Backend
        final AdministrationConfiguration administrationConfiguration = ADMINISTRATION_CONFIGURATION_MANAGER.get().getAdministrationConfiguration();

        this.host = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_HOST);
        this.port = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_PORT);
        this.username = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_USER);

        list.add(new HueNotificationTransport(host, port, username, bulp_id, reset, reset_ms, color, alert, state, resultsSummaryManager, administrationConfigurationManager));
        return list;
    }

    public void setTemplateRenderer(TemplateRenderer templateRenderer)
    {
        this.templateRenderer = templateRenderer;
    }

    public void setResultsSummaryManager(ResultsSummaryManager resultsSummaryManager)
    {
        this.resultsSummaryManager = resultsSummaryManager;
    }

    public void setAdministrationConfigurationManager(AdministrationConfigurationManager administrationConfigurationManager)
    {
        this.administrationConfigurationManager = administrationConfigurationManager;
    }

}
