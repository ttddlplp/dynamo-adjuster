package org.gz.dynamoadjuster;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class DynamoDbAdjuster {
    private final AWSCredentials awsCredentials;
    private final String tableName;
    private final Scheduler scheduler;

    public DynamoDbAdjuster(AWSCredentials awsCredentials, String tableName) {
        this.awsCredentials = awsCredentials;
        this.tableName = tableName;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException("Exception creating scheduler", e);
        }
    }

    public void start() {
        // define the job and tie it to our HelloJob class
        JobDetail job = newJob(MonitoringJob.class)
                .withIdentity("job1", "group1")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60)
                        .repeatForever())
                .build();

        try {
            scheduler.setJobFactory(new MonitoringJobFactory(awsCredentials, tableName));
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Exception scheduling monitoring job", e);
        }
    }

    public void shutdown() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                throw new RuntimeException("Exception shutting down adjuster", e);
            }
        }
    }

    public static void main(String[] args) {
        new DynamoDbAdjuster(new BasicAWSCredentials("AKIAJKOF44TXVIHQWDGQ", "oSstS5/nJKsawAGBmTXEEIM9HwYjhqKffEMyizS6"), "test-table");
    }
}
