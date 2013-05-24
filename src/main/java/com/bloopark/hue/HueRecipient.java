package com.bloopark.hue;

import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.spring.ComponentAccessor;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.google.common.base.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private String bulps        = null;
    private String state        = null;
    private String reset_ms     = null;
    private String color        = null;
    private boolean reset       = false;

    private TemplateRenderer templateRenderer;
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;

    public static final Supplier<AdministrationConfigurationManager> ADMINISTRATION_CONFIGURATION_MANAGER = ComponentAccessor.newLazyComponentReference("administrationConfigurationManager");
    /*
        this is need to save the reuse the settings
     */
    @Override
    public void populate(@NotNull Map<String, String[]> params)
    {
        for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); )
        {
            String next = iterator.next();
        }

           if (params.containsKey("hue_ids"))
            bulps = params.get("hue_ids")[0];
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

    }

    /*
        INIT the setting from bamboo config
     */
    @Override
    public void init(@Nullable String configurationData)
    {
        // getting the setting from Admin Backend
        final AdministrationConfiguration administrationConfiguration = ADMINISTRATION_CONFIGURATION_MANAGER.get().getAdministrationConfiguration();

        host = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_HOST);
        port = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_PORT);
        username = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_USER);

        int firstIdx = configurationData.indexOf(';');
        if (firstIdx > 0)
        {
            String conf[]   = configurationData.split(";");

            this.bulps      = conf[0];

            if(conf[1].equals("true"))
                this.reset = true;

            this.reset_ms   = conf[2];
            this.color      = conf[3];
            this.state      = conf[4];

        }
    }


    /*
        put the config into bamboo
     */
    @NotNull
    @Override
    public String getRecipientConfig()
    {

        String reset_str = "false";
        if(reset){
            reset_str = "true";
        }
        return bulps + ';' + reset_str + ';' + reset_ms + ';' + color + ';' + state;
    }

    /*
        display the setting pannel on the notification tab
     */
    @NotNull
    @Override
    public String getEditHtml()
    {
        Map context = new HashMap();

        if (this.bulps != null)
            context.put("hue_ids", this.bulps);
        if (this.reset_ms != null)
            context.put("hue_reset_ms", this.reset_ms);

        if(this.color == null){
            context.put("hue_color", "green");
        }else{
            context.put("hue_color", this.color);
        }

        if(this.state == null){
            context.put("hue_state", "successfull");
        }else{
            context.put("hue_state", this.state);
        }

        if(this.reset){
            context.put("hue_reset","true");
        }


        return templateRenderer.render("editHue.ftl", context);
    }

    /*
        DISPLAY the settings
     */
    @NotNull
    @Override
    public String getViewHtml()
    {
        String reset_str = "";

        if(reset){
            reset_str = "<br/>Reset time: " + this.reset_ms;
        }

        return "<b>Hue</b>"
                + "<br/>Bulp: " + this.bulps
                + reset_str
                + "<br/>Color: " + this.color
                + "<br/>State: " + this.state;
    }


    /*
        Add our new notification option to bamboo notifications list
     */
    @NotNull
    @Override
    public List<NotificationTransport> getTransports()
    {
        ArrayList list = new ArrayList();

        list.add(new HueNotificationTransport(host, port, username, bulps, reset, reset_ms, color, state, resultsSummaryManager, administrationConfigurationManager));
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
