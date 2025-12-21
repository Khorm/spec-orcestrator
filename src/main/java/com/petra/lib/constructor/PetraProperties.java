package com.petra.lib.constructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "petra")
public class PetraProperties {
    private int threadCount;
    private String serviceName;

    public int getThreadCount() {
        return threadCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
