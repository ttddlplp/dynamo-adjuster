package org.gz.dynamoadjuster;

public class Adjuster {
    protected static final double MARGIN = 1.1;
    private Updater updater;
    private Reporter reporter;
    private Double hardlimit;

    public Adjuster(Updater updater, Reporter reporter) {
        this.updater = updater;
        this.reporter = reporter;
    }

    public void action() {
        double currentProvisionThroughput = reporter.getReadProvisionThroughput();
        double consumedThroughput = reporter.getConsumedReadThroughput();

        if (currentProvisionThroughput > consumedThroughput * MARGIN
                || currentProvisionThroughput < consumedThroughput * MARGIN) {
            if (hardlimit != null) {
                updater.updateReadThroughput(Math.min(consumedThroughput * MARGIN, hardlimit));
            } else {
                updater.updateReadThroughput(consumedThroughput * MARGIN);
            }
        }
    }

    public void setHardlimit(double hardlimit) {
        this.hardlimit = hardlimit;
    }
}
