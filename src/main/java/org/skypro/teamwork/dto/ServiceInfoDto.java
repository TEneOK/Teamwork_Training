package org.skypro.teamwork.dto;

public class ServiceInfoDto {
    private String name;
    private String version;

    public ServiceInfoDto(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}