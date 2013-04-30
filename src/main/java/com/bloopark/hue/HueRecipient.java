package com.bloopark.hue;

import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.plugin.descriptor.NotificationRecipientModuleDescriptor;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.template.TemplateRenderer;
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
    private String host = null;
    private String port = null;
    private String username = null;

    private TemplateRenderer templateRenderer;
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;

    @Override
    public void populate(@NotNull Map<String, String[]> params)
    {
        for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); )
        {
            String next = iterator.next();
            System.out.println("next = " + next + " || " + params.get(next)[0]);
        }
        if (params.containsKey("hue_host"))
            host = params.get("hue_host")[0];
        if (params.containsKey("hue_port"))
            port = params.get("hue_port")[0];
        if (params.containsKey("hue_username"))
            username = params.get("hue_username")[0];

            System.out.println(">>>> Host: " + host + " Port: " + port + " API username: " + username);
    }


    @Override
    public void init(@Nullable String configurationData)
    {
        int firstIdx = configurationData.indexOf('|');
        if (firstIdx > 0)
        {
            int secondIdx = configurationData.indexOf('|', firstIdx + 1);
            host = configurationData.substring(0, firstIdx);
            port = configurationData.substring(firstIdx + 1, secondIdx);
            username = configurationData.substring(secondIdx + 1);
        }
    }


    @NotNull
    @Override
    public String getRecipientConfig()
    {
        // We can do this because API tokens don't have | in them, but it's pretty dodge. Better to JSONify or something?
        return host + '|' + port + '|' + username;
    }

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

        return templateRenderer.render("editNotification.ftl", context);
    }

    @NotNull
    @Override
    public String getViewHtml()
    {
        return "Hue configuration:<br/>Host: " + this.host + "<br/>Port: " + this.port + "<br/>API username: " + this.username;
    }


    @NotNull
    @Override
    public List<NotificationTransport> getTransports()
    {
        ArrayList list = new ArrayList();
        list.add(new HueNotificationTransport(host, port, username, resultsSummaryManager, administrationConfigurationManager));
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
