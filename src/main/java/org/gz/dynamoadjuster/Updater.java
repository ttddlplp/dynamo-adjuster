package org.gz.dynamoadjuster;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class Updater {
    private DynamoDB dynamoDB;
    private String tableName;

    public Updater(DynamoDB dynamoDB, String tableName) {
        this.dynamoDB = dynamoDB;
        this.tableName = tableName;
    }

    public void updateReadThroughput(long throughput) {
        Table table = dynamoDB.getTable(tableName);
        table.updateTable(new ProvisionedThroughput().withReadCapacityUnits(throughput));
    }
}
