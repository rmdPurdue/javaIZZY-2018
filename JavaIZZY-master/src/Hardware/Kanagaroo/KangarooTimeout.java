package Hardware.Kanagaroo;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Hardware.Kanagaroo
 * @date 12/11/2017
 */
public class KangarooTimeout {

    private long start;
    private Integer timeoutMS;


    public KangarooTimeout(int timeoutMS) {
        if(timeoutMS < 0) {
            if(timeoutMS != -1) {
                throw new IllegalArgumentException("Timeout is negative and not equal to infinite.");
            }
            this.timeoutMS = null;
        } else {
            this.timeoutMS = timeoutMS;
        }

        this.start = System.currentTimeMillis();
    }

    public int getTimeout() {
        if(timeoutMS == null) {
            return -1;
        } else {
            return this.timeoutMS;
        }
    }

    public void expire() {
        if(this.timeoutMS == null) {
            return;
        }
        this.start = System.currentTimeMillis() - timeoutMS;
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public boolean canExpire() {
        return this.timeoutMS != null;
    }

    public boolean expired() {
        return timeLeft() == 0;
    }

    public int timeLeft() {
        if(!canExpire()) {
            return -1;
        }

        long elapsedTime = System.currentTimeMillis() - this.start;
        long allowedTime = this.timeoutMS;
//        System.out.println("Time left = " + (allowedTime - elapsedTime));
        return elapsedTime >= allowedTime ? 0 : (int)(allowedTime - elapsedTime);
    }

    public static boolean greaterThan(int lhs, int rhs) {
        if (lhs > rhs) return true;
        else return false;
    }

    public static boolean lessThan(int lhs, int rhs) {
        if( lhs < rhs) return true;
        else return false;
    }

}
