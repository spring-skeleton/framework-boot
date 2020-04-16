package com.codeages.framework.scheduler;

import org.springframework.scheduling.quartz.QuartzJobBean;

abstract public class AbstractJob extends QuartzJobBean {
    abstract public String getJobName();

    public String getJobGroupName() {
        return "default";
    }

    public String getCron() {
        return "";
    }

    public Long getTime() {
        return 0L;
    }
}
