package org.gz.dynamoadjuster;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdaterTest {

    private String TABLE_NAME = "test-table";
    private Updater updater;

    @Mock
    DynamoDB dynamoDB;

    @Mock
    Table table;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        updater = new Updater(dynamoDB, TABLE_NAME);
        when(dynamoDB.getTable(TABLE_NAME)).thenReturn(table);
    }

    @Test
    public void testUpdateTable() throws Exception {
        long newThroughput = 20;
        updater.updateReadThroughput(newThroughput);
        ArgumentCaptor<ProvisionedThroughput> captor =
                ArgumentCaptor.forClass(ProvisionedThroughput.class);
        verify(table, times(1)).updateTable(captor.capture());
        assertThat(captor.getValue().getReadCapacityUnits()).isEqualTo(newThroughput);
    }
}
