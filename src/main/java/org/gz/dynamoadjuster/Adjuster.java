package org.gz.dynamoadjuster;

public class Adjuster {
    private Updater updater;
    private Reporter reporter;

    public Adjuster(Updater updater, Reporter reporter) {
        this.updater = updater;
        this.reporter = reporter;
    }

    public void action() {
        double currentProvisionThroughput = reporter.getCurrentProvisionThroughput();
        double consumedThroughput = reporter.getConsumedThroughput();

        if (currentProvisionThroughput > consumedThroughput * 1.1) {
            updater.updateThroughput(consumedThroughput * 1.1);
        }
    }
}
