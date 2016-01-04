package org.gz.dynamoadjuster;

public class Adjuster {
    protected static final double MARGIN = 1.1;
    private Updater updater;
    private Reporter reporter;

    public Adjuster(Updater updater, Reporter reporter) {
        this.updater = updater;
        this.reporter = reporter;
    }

    public void action() {
        double currentProvisionThroughput = reporter.getCurrentReadProvisionThroughput();
        double consumedThroughput = reporter.getConsumedReadThroughput();

        if (currentProvisionThroughput > consumedThroughput * MARGIN
                || currentProvisionThroughput < consumedThroughput * MARGIN) {
            updater.updateThroughput(consumedThroughput * MARGIN);
        }
    }
}
