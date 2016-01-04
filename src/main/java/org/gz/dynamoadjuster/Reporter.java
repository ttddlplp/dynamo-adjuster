package org.gz.dynamoadjuster;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public class Reporter {
    private AmazonCloudWatchClient amazonCloudWatchClient;

    public Reporter(AmazonCloudWatchClient amazonCloudWatchClient, String TEST_TABLE_NAME) {
        this.amazonCloudWatchClient = amazonCloudWatchClient;
    }

    public double getCurrentReadProvisionThroughput() {
        Dimension instanceDimension = new Dimension();
        instanceDimension.setName("TableName");
        instanceDimension.setValue("test-table");

        GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - 600 * 1000))
                .withNamespace("AWS/DynamoDB")
                .withPeriod(300)
                .withMetricName("ConsumedReadCapacityUnits")
                .withStatistics("Average")
                .withDimensions(Arrays.asList(instanceDimension))
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

    public double getConsumedReadThroughput() {
        return 0;
    }
}
