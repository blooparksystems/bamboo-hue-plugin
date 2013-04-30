package com.bloopark.hue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.plugin.webresource.WebResourceManager;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HueConfigurationServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(HueConfigurationServlet.class);
    private final TemplateRenderer templateRenderer;

    private final WebResourceManager webResourceManager;

    public HueConfigurationServlet(TemplateRenderer templateRenderer, WebResourceManager webResourceManager) {
        this.templateRenderer = templateRenderer;
        this.webResourceManager = webResourceManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.webResourceManager.requireResource("com.bloopark.hue:hue-resources");
        resp.setContentType("text/html;charset=utf-8");
        templateRenderer.render("admin.vm", resp.getWriter());
    }
}