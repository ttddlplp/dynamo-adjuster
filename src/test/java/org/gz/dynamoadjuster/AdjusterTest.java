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
        when(reporter.getCurrentProvisionThroughput()).thenReturn(5.0);
        double consumed = 1.0;
        when(reporter.getConsumedThroughput()).thenReturn(consumed);
        ArgumentCaptor<Double> argument = ArgumentCaptor.forClass(double.class);

        adjuster.action();
        verify(updater, times(1)).updateThroughput(argument.capture());

        assertEquals(consumed * 1.1, argument.getValue(), 0.00001);
    }
}