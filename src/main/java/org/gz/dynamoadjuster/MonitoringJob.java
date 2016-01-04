package org.gz.dynamoadjuster;

import com.google.common.annotations.VisibleForTesting;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static java.lang.Math.max;
import static java.lang.Math.min;

class MonitoringJob implements Job {
    protected static final double MARGIN = 1.1;
    protected static final long MIN = 1;
    private Updater updater;
    private Reporter reporter;
    private Double hardlimit;

    @VisibleForTesting
    protected MonitoringJob(Updater updater, Reporter reporter) {
        this.updater = updater;
        this.reporter = reporter;
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        double currentProvisionThroughput = reporter.getReadProvisionThroughput();
        double consumedThroughput = reporter.getConsumedReadThroughput();

        if (currentProvisionThroughput > consumedThroughput * MARGIN
                || currentProvisionThroughput < consumedThroughput * MARGIN) {
            if (hardlimit != null) {
                double targetValue = max(min(consumedThroughput * MARGIN, hardlimit), MIN);
                updater.updateReadThroughput((long) Math.ceil(targetValue));
            } else {
                double targetValue = max(consumedThroughput * MARGIN, MIN);
                updater.updateReadThroughput((long) Math.ceil(targetValue));
            }
        }
    }

    public void setHardlimit(double hardlimit) {
        this.hardlimit = hardlimit;
    }
}
