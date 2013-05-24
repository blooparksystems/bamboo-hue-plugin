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
    private String reset_ms     = null;

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
        if (params.containsKey("hue_reset_ms"))
            reset_ms = params.get("hue_reset_ms")[0];

    }

    /*
        INIT the setting from bamboo config
     */
    @Override
    public void init(@Nullable String configurationData)
    {

        final AdministrationConfiguration administrationConfiguration = ADMINISTRATION_CONFIGURATION_MANAGER.get().getAdministrationConfiguration();

        host = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_HOST);
        port = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_PORT);
        username = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_USER);

        this.host = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_HOST);

        int firstIdx = configurationData.indexOf('|');
        if (firstIdx > 0)
        {
            int secondIdx = configurationData.indexOf('|', firstIdx + 1);

            bulps = configurationData.substring(firstIdx);
            reset_ms = configurationData.substring(forthIdx + 1);
        }
    }


    /*
        put the config into bamboo
     */
    @NotNull
    @Override
    public String getRecipientConfig()
    {
        return host + '|' + port + '|' + username + '|' + bulps + '|' + reset_ms;
    }

    /*
        display the setting pannel on the notification tab
     */
    @NotNull
    @Override
    public String getEditHtml()
    {
        Map context = new HashMap();
        if (this.host != null)
            context.put("hue_host", this.host);
        if (this.port != null)
            context.put("hue_port", this.port);
        if (this.username != null)
            context.put("hue_username", this.username);
        if (this.bulps != null)
            context.put("hue_ids", this.bulps);
        if (this.reset_ms != null)
            context.put("hue_reset_ms", this.reset_ms);

        return templateRenderer.render("editHue.ftl", context);
    }

    /*
        DISPLAY the settings
     */
    @NotNull
    @Override
    public String getViewHtml()
    {
        return "Hue configuration:"
                + "<br/>Host: " + this.host
                + "<br/>Port: " + this.port
                + "<br/>API username: " + this.username
                + "<br/>Bulps: " + this.bulps
                + "<br/>Reset time: " + this.reset_ms;
    }


    /*
        Add our new notification option to bamboo notifications list
     */
    @NotNull
    @Override
    public List<NotificationTransport> getTransports()
    {
        ArrayList list = new ArrayList();
        list.add(new HueNotificationTransport(host, port, username, bulps, reset_ms,  resultsSummaryManager, administrationConfigurationManager));
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
