package com.tourist_bot.bot.conf;


public class AppConfYaml {

    private String token = null;
    private String geoJsonPath = null;
    private Integer maxSearchDistMeters = null;
    private Integer defaultSearchDistStepMeters = null;
    private Integer sessionTimeoutSec = null;
    private Integer sessionsCleanupPeriodSec = null;
    private String sessionManager = null; //EMBEDDED || REDIS

    public AppConfYaml() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGeoJsonPath() {
        return geoJsonPath;
    }

    public void setGeoJsonPath(String geoJsonPath) {
        this.geoJsonPath = geoJsonPath;
    }

    public Integer getMaxSearchDistMeters() {
        return maxSearchDistMeters;
    }

    public void setMaxSearchDistMeters(Integer maxSearchDistMeters) {
        this.maxSearchDistMeters = maxSearchDistMeters;
    }

    public Integer getDefaultSearchDistStepMeters() {
        return defaultSearchDistStepMeters;
    }

    public void setDefaultSearchDistStepMeters(Integer defaultSearchDistStepMeters) {
        this.defaultSearchDistStepMeters = defaultSearchDistStepMeters;
    }

    public Integer getSessionTimeoutSec() {
        return sessionTimeoutSec;
    }

    public void setSessionTimeoutSec(Integer sessionTimeoutSec) {
        this.sessionTimeoutSec = sessionTimeoutSec;
    }


    public Integer getSessionsCleanupPeriodSec() {
        return sessionsCleanupPeriodSec;
    }

    public void setSessionsCleanupPeriodSec(Integer sessionsCleanupPeriodSec) {
        this.sessionsCleanupPeriodSec = sessionsCleanupPeriodSec;
    }

    public String getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(String sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String toString() {
        return "AppConfYaml{" +
                "token='" + token + '\'' +
                ", geoJsonPath='" + geoJsonPath + '\'' +
                ", maxSearchDistMeters=" + maxSearchDistMeters +
                ", defaultSearchDistStepMeters=" + defaultSearchDistStepMeters +
                ", sessionTimeoutSec=" + sessionTimeoutSec +
                ", sessionsCleanupPeriodSec=" + sessionsCleanupPeriodSec +
                ", sessionManager='" + sessionManager + '\'' +
                '}';
    }
}
