package com.codeages.framework.cache;

import com.codeages.framework.scheduler.AbstractJob;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
public class CacheSyncJob extends AbstractJob {

    @Value("${cache.sync.internal.time}")
    private Long internalTime;

    @Autowired
    private CacheConfig cacheConfig;

    @Override
    public String getJobName() {
        return null;
    }


    @Override
    public String getCron() {
        return "0/" + internalTime + " * * * * ?";
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            Date start = new Date();
            start.setTime(start.getTime()-internalTime*1000);
            cacheConfig.syncCache(start, new Date());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
