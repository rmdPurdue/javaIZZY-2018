package com.rmdPurdue.izzyRobot.obstacleDetection;

public class ObstacleMessage {

    private boolean obstacleDetected;
    private boolean obstacleAcknowledged;
    private double angle;

    public ObstacleMessage(boolean obstacleDetected, boolean obstacleAcknowledged, double angle) {
        this.obstacleDetected = obstacleDetected;
        this.obstacleAcknowledged = obstacleAcknowledged;
        this.angle = angle;
    }

    public ObstacleMessage() {
        this.obstacleDetected = false;
        this.obstacleAcknowledged = false;
        this.angle = 0.0;
    }

    public boolean isObstacleDetected() {
        return obstacleDetected;
    }

    public void setObstacleDetected(boolean obstacleDetected) {
        this.obstacleDetected = obstacleDetected;
    }

    public boolean isObstacleAcknowledged() {
        return obstacleAcknowledged;
    }

    public void setObstacleAcknowledged(boolean obstacleAcknowledged) {
        this.obstacleAcknowledged = obstacleAcknowledged;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public String toString() {
        return "ObstacleMessage{" +
                "obstacleDetected=" + obstacleDetected +
                ", obstacleAcknowledged=" + obstacleAcknowledged +
                ", angle=" + angle +
                '}';
    }
}
