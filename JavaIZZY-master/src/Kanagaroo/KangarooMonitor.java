package Kanagaroo;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/11/2017
 */
public class KangarooMonitor {

    //public boolean isValid;
    //public KangarooStatus status;
    public KangarooChannel channel;

    public KangarooMonitor(KangarooChannel channel) {
        this.channel = channel;
    }

    public KangarooMonitor update() {
        return update(this.channel.commandTimeout);
    }

    public KangarooMonitor update(int timeoutMS) {
        return update(new KangarooTimeout(timeoutMS));
    }

    public KangarooMonitor update(KangarooTimeout timeout) {
        while(isValid() && !this.channel.updateMonitoredResult(timeout, true)) ;
        return this;
    }

    public KangarooMonitor wait(int timeoutMS) {
        return wait(new KangarooTimeout(timeoutMS));
    }

    public KangarooMonitor wait(KangarooTimeout timeout) {
        while(!status().isDone()) { update(timeout); }
        return this;
    }

    public static boolean waitAll(KangarooMonitor[] monitors, int timeoutMS) {
        return waitAll(monitors, new KangarooTimeout(timeoutMS));

    }
    public static boolean waitAll(KangarooMonitor[] monitors, KangarooTimeout timeout) {
        if(monitors==null) { throw new IllegalArgumentException("monitors"); }

        for(KangarooMonitor monitor : monitors) {
            if(monitor == null) { continue; }

            monitor.wait(timeout);
            if(monitor.status().isTimedOut()) { return false; }
        }

        return true;
    }

    public static int waitAny(KangarooMonitor[] monitors, int timeoutMS) {
        return waitAny(monitors, new KangarooTimeout(timeoutMS));
    }

    public static int waitAny(KangarooMonitor[] monitors, KangarooTimeout timeout) {
        if(monitors == null) { throw new IllegalArgumentException("monitors"); }

        while(true) {
            if(timeout.expired()) { return -1; }

            for(int i = 0; i < monitors.length; i++) {
                KangarooMonitor monitor = monitors[i];
                if(monitor == null) { continue; }

                monitor.update(timeout);
                if(monitor.status().isTimedOut()) { return -1; }
                if(monitor.status().isDone()) { return i; }
            }
        }
    }

    public boolean isValid() {
        return this.channel.monitor == this;
    }

    public KangarooStatus status() {
        return isValid() ? this.channel.monitoredGetResult : KangarooStatus.createInvalidStatus();
    }






}
