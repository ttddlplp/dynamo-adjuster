package org.gz.dynamoadjuster;

enum MetricNames {
    CONSUMED_READ_CAPACITY_UNITS("ConsumedReadCapacityUnits"),
    PROVISIONED_READ_CAPACITY_UNITS("ProvisionedReadCapacityUnits");

    private String name;

    public String getName() {
        return name;
    }

    private MetricNames(String name) {
        this.name = name;
    }
}
