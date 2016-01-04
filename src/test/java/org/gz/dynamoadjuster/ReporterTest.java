package org.gz.dynamoadjuster;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReporterTest {
    @Mock
    AmazonCloudWatchClient amazonCloudWatchClient;
    private Reporter reporter;
    private String TEST_TABLE_NAME;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TEST_TABLE_NAME = "test-table";
        reporter = new Reporter(amazonCloudWatchClient, TEST_TABLE_NAME);
    }

    @Test
    public void testGetCurrentThroughputQuery() throws Exception {
        when(amazonCloudWatchClient.getMetricStatistics(any(GetMetricStatisticsRequest.class)))
                .thenReturn(new GetMetricStatisticsResult());
        ArgumentCaptor<GetMetricStatisticsRequest> captor =
                ArgumentCaptor.forClass(GetMetricStatisticsRequest.class);
        reporter.getCurrentReadProvisionThroughput();
        verify(amazonCloudWatchClient, times(1)).getMetricStatistics(captor.capture());
        GetMetricStatisticsRequest actualValue = captor.getValue();
        assertThat(actualValue.getDimensions())
                .contains(new Dimension().withName("TableName").withValue(TEST_TABLE_NAME));
        assertThat(actualValue.getMetricName()).isEqualTo("ConsumedReadCapacityUnits");
    }

    @Test
    public void testGetCurrentThroughput() throws Exception {
        double average = 10.0;
        when(amazonCloudWatchClient.getMetricStatistics(any(GetMetricStatisticsRequest.class)))
                .thenReturn(
                        new GetMetricStatisticsResult().withDatapoints(
                                Lists.newArrayList(
                                        new Datapoint().withTimestamp(new Date()).withAverage(average),
                                        new Datapoint()
                                                .withTimestamp(new Date(new Date().getTime() - 600 * 1000))
                                                .withAverage(789.0)
                                )
                        )
                );
        double currentProvisionThroughput = reporter.getCurrentReadProvisionThroughput();
        assertThat(currentProvisionThroughput).isEqualTo(average);
    }
}
