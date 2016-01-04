package org.gz.dynamoadjuster;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class MonitoringJobFactory implements JobFactory{
    private AWSCredentials awsCredentials;
    private String tableName;

    public MonitoringJobFactory(AWSCredentials awsCredentials, String tableName) {
        this.awsCredentials = awsCredentials;
        this.tableName = tableName;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        AmazonCloudWatchClient amazonCloudWatchClient = new AmazonCloudWatchClient(awsCredentials);
        DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(awsCredentials));
        Reporter reporter = new Reporter(amazonCloudWatchClient, tableName);
        Updater updater = new Updater(dynamoDB, tableName);
        return new MonitoringJob(updater, reporter);
    }
}
