package org.gz.dynamoadjuster;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

class Reporter {
    private AmazonCloudWatchClient amazonCloudWatchClient;
    private Dimension tableNameDimension;

    public Reporter(AmazonCloudWatchClient amazonCloudWatchClient, String tableName) {
        this.amazonCloudWatchClient = amazonCloudWatchClient;
        tableNameDimension = new Dimension();
        tableNameDimension.setName("TableName");
        tableNameDimension.setValue(tableName);
    }

    public double getReadProvisionThroughput() {
        return getLatestMetric(MetricNames.PROVISIONED_READ_CAPACITY_UNITS);
    }

    public double getConsumedReadThroughput() {
        return getLatestMetric(MetricNames.CONSUMED_READ_CAPACITY_UNITS);
    }

    private double getLatestMetric(MetricNames metricName) {
        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
            .withStartTime(new Date(new Date().getTime() - 600 * 1000))
            .withNamespace("AWS/DynamoDB")
            .withPeriod(300)
            .withMetricName(metricName.getName())
            .withStatistics("Average")
            .withDimensions(Arrays.asList(tableNameDimension))
            .withEndTime(new Date());

        GetMetricStatisticsResult getMetricStatisticsResult = amazonCloudWatchClient.getMetricStatistics(request);
        Optional<Datapoint> latest = getMetricStatisticsResult.getDatapoints()
                .stream()
                .max((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        if (latest.isPresent()) {
            return latest.get().getAverage();
        } else {
            return 0;
        }
    }
}
