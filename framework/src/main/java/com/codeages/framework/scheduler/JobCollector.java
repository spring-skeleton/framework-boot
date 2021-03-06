package com.codeages.framework.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCollector implements ApplicationRunner {
    @Autowired
    private SchedulerManager quartzManager;

    @Autowired(required = false)
    private List<AbstractJob> jobs;

    @Value("${scheduler.job.auto-start}")
    private Boolean autoStart;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!autoStart){
            return;
        }

        if(jobs!=null){
            jobs.forEach(job -> {
                if(!"".equals(job.getCron())) {
                    quartzManager.addJob(job.getClass(), job.getJobName(), job.getJobGroupName(), job.getCron());
                } else {
                    quartzManager.addJob(job.getClass(), job.getJobName(), job.getJobGroupName(), job.getTime().intValue());
                }
            });
        }
    }
}