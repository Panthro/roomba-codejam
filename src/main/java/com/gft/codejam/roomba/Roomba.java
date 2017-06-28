package com.gft.codejam.roomba;

import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.util.ArrayList;
import java.util.List;

import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * Created by panthro on 22/06/2017.
 */
public class Roomba extends Robot {

    private static final int FULL_TURN = 360;
    private static final int INITIAL_ENERGY = 100;
    private static final int MIN_POSSIBLE_ENERGY = 0;
    private static final int BULLET_POWER_DIVISOR = 782;
    private static final int MAX_BULLET_POWER = 3;
    private static final String logPattern = ""; // --- put here a string that will be used to filter the log TODO maybe a regex?
    private boolean moveForward = true;
    private double lastBulletHitEnergy = 0.0;
    private boolean logEnabled = true;

    //Variables for future use:
    //TODO: We should improve this. Over-storage of information
//    private ArrayList<BulletHitEvent> bulletHitInfoContainer;
//    private ArrayList<HitByBulletEvent> hitByBullerContainer;
    private List<ScannedRobotEvent> scannedContainer = new ArrayList<ScannedRobotEvent>();
    private long lastHitWallTime = 0;

    public void run() {

        turnLeft(getHeading());

        setAdjustRadarForRobotTurn(true);

        while (true) {
            //Radar FailSafe
            turnRadarRight(FULL_TURN);
        }
    }

    private void log(String message) {
        if (logEnabled) {
            if (message.contains(logPattern)) {
                out.println(message);
            }
        }
    }

    private double getMaxBulletPower() {
        return MAX_BULLET_POWER; //TODO should we calculate this based on energy?
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        double enemyPreviousEnergy;

        /*   ScannedRobotEvent Info:
             double	getBearing()
             double	getBearingRadians()
             double	getDistance()
             double	getEnergy()
             double	getHeading()
             double	getHeadingRadians()
             double getVelocity()
         */


        //Check previous enemy tank energy in order to detect
        if (scannedContainer.isEmpty()) {
            enemyPreviousEnergy = INITIAL_ENERGY; //TODO check if this constant is actually a constant
        } else if (lastBulletHitEnergy > MIN_POSSIBLE_ENERGY) {
            //When we hit, we lower energy, we should take into account this
            enemyPreviousEnergy = lastBulletHitEnergy;
            lastBulletHitEnergy = MIN_POSSIBLE_ENERGY;
        } else {
            enemyPreviousEnergy = scannedContainer.get(scannedContainer.size() - 1).getEnergy();
            lastBulletHitEnergy = MIN_POSSIBLE_ENERGY;
        }
        this.scannedContainer.add(e);

        printScannedRobotEvent(e);


        // bulletPower
        double bulletPower = Math.min(BULLET_POWER_DIVISOR / e.getDistance(), getMaxBulletPower());
        double velocityPrediction = 0;

        if(e.getVelocity() >= 2) {
            //Predicted time:
            long time = (long) (e.getDistance() / (20 - bulletPower * 3)); //Defaults: bulletSpeed = 20 - bulletPower * 3;
            log(" >>> {time: " + time + "}");
            velocityPrediction = (time * e.getVelocity() / 20);
        }

        // Lock enemy tank (almost 99% times)
        // TODO: Improve this method using predictive shooting
        double gunTurnAmount = normalRelativeAngleDegrees((getHeading() + e.getBearing() + velocityPrediction) - this.getGunHeading());
        turnGunRight(gunTurnAmount);

        fire(bulletPower);


        log("{getBearing: " + e.getBearing() + "}");

        log("{gunAmountTurn: " + gunTurnAmount + "}");


        // TODO: Improve this method using better predictive enemy future position
        // Fire enemy tank
        log("{bulletPower: " + bulletPower + "}");

        //Be at 90º of enemy
        turnRight(normalRelativeAngleDegrees(e.getBearing()) + 90);

        //Avoid enemy shoots
        if (enemyPreviousEnergy > e.getEnergy()) {
            String log = "";
            log += "Energy { " +
                "enemyPreviousEnergy: " + enemyPreviousEnergy + "," +
                "e.getEnergy(): " + e.getEnergy() + "," +
                "Distance: " + e.getBearing() + "}";
            log(log);

            move();


        }

        // Call scan again
        scan();
    }

    private void move() {
        //Try to avoid enemy fire
        if (moveForward) {
            ahead(100);
        } else {
            back(100);
        }
    }

    private void printScannedRobotEvent(ScannedRobotEvent e) {

        String logTxt = String.format("ScannedRobotEvent { Bearing: %s,BearingRads: %s,Distance: %s,Energy: %s,Heading: %s,HeadingRads: %s,Velocity: %s}",
            e.getBearing(),
            e.getBearingRadians(),
            e.getBearing(),
            e.getBearing(),
            e.getBearing(),
            e.getHeadingRadians(),
            e.getVelocity());
        log(logTxt);

    }

    public void onHitWall(HitWallEvent e) {
        log("{HitWall e.getBearing(): " + e.getBearing() + "}");
        log("{HitWall getTime() =" + getTime());


        if (getTime() - lastHitWallTime < 150) { // -> means the robot has hit the wall twice
            turnRight(e.getBearing());
        }

        lastHitWallTime = getTime();

        moveForward = !moveForward;
        move();
    }

    public void onHitByBullet(HitByBulletEvent e) {
        //Logs Event
        //this.hitByBullerContainer.add(e);
        //log(e.toString());

        //When we are hit by bullet, we cannot use last bullet energy as the enemy tank regenerates energy
        lastBulletHitEnergy = MIN_POSSIBLE_ENERGY;
        /*
            double getBearing;
            double getBearingRadians;
            Bullet getBullet;
            double getHeading;
            double getHeadingRadians;
            String getName;
            double getPower;
            double getVelocity;
         */
    }


    public void onBulletHit(BulletHitEvent e) {
        //Logs Event
        //this.bulletHitInfoContainer.add(e);
        //log(e.toString());
        //When we hit a tank with a bullet, we reduce enemy max energy.
        lastBulletHitEnergy = e.getEnergy();
        /*
            Bullet getBullet();
            double getEnergy();
            String getName();
        */
    }

}
