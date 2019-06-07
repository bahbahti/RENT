package com.netcracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DTO {
    @JsonProperty("Java version")
    private String javaVersion;

    @JsonProperty("Application version")
    private String buildVersion;

    public DTO() {
    }

    public DTO(String javaVersion, String buildVersion) {
        this.javaVersion = javaVersion;
        this.buildVersion = buildVersion;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
