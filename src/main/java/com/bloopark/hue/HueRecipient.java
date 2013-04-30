package com.bloopark.hue;

import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.notification.NotificationTransport;
import com.atlassian.bamboo.notification.recipients.AbstractNotificationRecipient;
import com.atlassian.bamboo.plugin.descriptor.NotificationRecipientModuleDescriptor;
import com.atlassian.bamboo.resultsummary.ResultsSummaryManager;
import com.atlassian.bamboo.template.TemplateRenderer;
import com.atlassian.bamboo.user.gravatar.GravatarService;
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
    private static String API_TOKEN = "apiToken";
    private static String ROOM = "room";
    private static String NOTIFY = "notify";

    private String host = null;
    private String port = null;
    private String username = null;
    private boolean notify = false;
    private TemplateRenderer templateRenderer;
    private ResultsSummaryManager resultsSummaryManager;
    private AdministrationConfigurationManager administrationConfigurationManager;
    private GravatarService gravatarService;



    @NotNull
    @Override
    public List<NotificationTransport> getTransports()
    {
        ArrayList list = new ArrayList();
        list.add(new HueNotificationTransport(host, port, username, notify, resultsSummaryManager, administrationConfigurationManager, gravatarService));
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

    public void setGravatarService(GravatarService gravatarService)
    {
        this.gravatarService = gravatarService;
    }
}
