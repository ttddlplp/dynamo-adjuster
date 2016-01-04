package org.gz.dynamoadjuster;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class AdjusterTest {

    @InjectMocks
    private Adjuster adjuster;

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

        adjuster.action();
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(consumed * Adjuster.MARGIN, argument.getValue(), 0.00001);
    }

    @Test
    public void testProvisionThroughputLowerThanRealPlusMargin() throws Exception {
        when(reporter.getReadProvisionThroughput()).thenReturn(5.0);
        double consumed = 10.0;
        when(reporter.getConsumedReadThroughput()).thenReturn(consumed);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(long.class);

        adjuster.action();
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(consumed * Adjuster.MARGIN, argument.getValue(), 0.00001);
    }

    @Test
    public void testCanOnlyIncreaseToHardlimit() throws Exception {
        double hardlimit = 10.0;
        adjuster.setHardlimit(hardlimit);
        when(reporter.getReadProvisionThroughput()).thenReturn(5.0);
        double consumed = 20.0;
        when(reporter.getConsumedReadThroughput()).thenReturn(consumed);
        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(long.class);

        adjuster.action();
        verify(updater, times(1)).updateReadThroughput(argument.capture());

        assertEquals(hardlimit, argument.getValue(), 0.00001);
    }
}
