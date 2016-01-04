package org.gz.dynamoadjuster;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class MonitoringJobTest {

    @InjectMocks
    private MonitoringJob monitoringJob;

    @Mock
    private Updater updater;

    @Mock
    private Reporter reporter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProvisionThroughputMuchHigherThanReal() throws Exception {
        when(reporter.getReadProvisionThroughput()).thenReturn(5.0);
        double consumed = 1.0;
        when(reporter.getConsumedReadThroughput()).thenReturn(consumed);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(long.class);

        monitoringJob.execute(null);
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(Math.ceil(consumed * MonitoringJob.MARGIN), argument.getValue(), 0.00001);
    }

    @Test
    public void testProvisionThroughputLowerThanRealPlusMargin() throws Exception {
        when(reporter.getReadProvisionThroughput()).thenReturn(5.0);
        double consumed = 10.0;
        when(reporter.getConsumedReadThroughput()).thenReturn(consumed);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(long.class);

        monitoringJob.execute(null);
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(Math.ceil(consumed * MonitoringJob.MARGIN), argument.getValue(), 0.00001);
    }

    @Test
    public void testConsumedIsZero() throws Exception {
        when(reporter.getReadProvisionThroughput()).thenReturn(5.0);
        double consumed = 0.0;
        when(reporter.getConsumedReadThroughput()).thenReturn(consumed);

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(long.class);

        monitoringJob.execute(null);
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(1.0, argument.getValue(), 0.00001);
    }

    @Test
    public void testCanOnlyIncreaseToHardlimit() throws Exception {
        double hardlimit = 10.0;
        monitoringJob.setHardlimit(hardlimit);
        when(reporter.getReadProvisionThroughput()).thenReturn(5.0);
        double consumed = 20.0;
        when(reporter.getConsumedReadThroughput()).thenReturn(consumed);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(long.class);

        monitoringJob.execute(null);
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(hardlimit, argument.getValue(), 0.00001);
    }
}
