package com.bloopark.hue;



import com.atlassian.bamboo.configuration.AdministrationConfiguration;
import com.atlassian.bamboo.ww2.BambooActionSupport;
import org.apache.commons.lang.StringUtils;

public class HueConfigurationAction extends BambooActionSupport {
    private static final long serialVersionUID = -2079002342690913834L;

    private String blooparkHueHost;
    private String blooparkHuePort;
    private String blooparkHueUser;

    /*

    ******************************************************************************************************************/
    public void validate() {
        super.validate();

        if (StringUtils.isBlank(blooparkHueHost)) {
            addFieldError(Constants.BLOOPARK_HUE_HOST_FIELD, "Please specifiy the hue host path");
        }

        if (StringUtils.isBlank(blooparkHuePort)) {
            addFieldError(Constants.BLOOPARK_HUE_PORT_FIELD, "Please specifiy the hue port, default is 80");
        }

        if (StringUtils.isBlank(blooparkHueUser)) {
            addFieldError(Constants.BLOOPARK_HUE_USER_FIELD, "Please specifiy the hue API username");
        }
    }

    /*

    ******************************************************************************************************************/
    public String doDefault() {
        AdministrationConfiguration administrationConfiguration = getAdministrationConfiguration();
        if(administrationConfiguration == null) {
            return "error";
        }

        String host = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_HOST);
        String port = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_PORT);
        String user = administrationConfiguration.getSystemProperty(Constants.BLOOPARK_HUE_USER);

        System.out.println(host);
        System.out.println(port);
        System.out.println(user);


        if(host == null) {
            host = "";
        }
        if(port == null) {
            port = "80";
        }
        if(user == null) {
            user = "";
        }


        setBlooparkHueHost(host);
        setBlooparkHuePort(port);
        setBlooparkHueUser(user);

        return "input";
    }

    /*

    ******************************************************************************************************************/
    public String doExecute() {

        AdministrationConfiguration administrationConfiguration = getAdministrationConfiguration();
        if(administrationConfiguration == null) {
            return "error";
        }

        System.out.println(blooparkHueHost);
        System.out.println(blooparkHuePort);
        System.out.println(blooparkHueUser);


        administrationConfiguration.setSystemProperty(Constants.BLOOPARK_HUE_HOST, blooparkHueHost);
        administrationConfiguration.setSystemProperty(Constants.BLOOPARK_HUE_PORT, blooparkHuePort);
        administrationConfiguration.setSystemProperty(Constants.BLOOPARK_HUE_USER, blooparkHueUser);

        this.administrationConfigurationManager.saveAdministrationConfiguration(administrationConfiguration);

        addActionMessage(getText("config.updated"));
        return "success";
    }

    /*

    ******************************************************************************************************************/
    public void setBlooparkHueHost(String blooparkHueHost) {
        this.blooparkHueHost = blooparkHueHost;
    }

    /*

    ******************************************************************************************************************/
    public String getBlooparkHueHost() {
        return blooparkHueHost;
    }

    /*

    ******************************************************************************************************************/
    public void setBlooparkHuePort(String blooparkHuePort) {
        this.blooparkHuePort = blooparkHuePort;
    }

    /*

    ******************************************************************************************************************/
    public String getBlooparkHuePort() {
        return blooparkHuePort;
    }

    /*

    ******************************************************************************************************************/
    public void setBlooparkHueUser(String blooparkHueUser) {
        this.blooparkHueUser = blooparkHueUser;
    }

    /*

    ******************************************************************************************************************/
    public String getBlooparkHueUser() {
        return blooparkHueUser;
    }
}

