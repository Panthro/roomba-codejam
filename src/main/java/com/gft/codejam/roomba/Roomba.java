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

    public static final int FULL_TURN = 360;
    public static final int INITIAL_ENERGY = 100;
    public static final int MIN_POSSIBLE_ENERGY = 0;
    public static final int BULLET_POWER_DIVISOR = 782;
    public static final int MAX_BULLET_POWER = 3;
    private boolean moveForward = true;
    private double lastBulletHitEnergy = 0.0;
    private boolean logEnabled = false;

    //Variables for future use:
    //TODO: We should improve this. Over-storage of information
//    private ArrayList<BulletHitEvent> bulletHitInfoContainer;
//    private ArrayList<HitByBulletEvent> hitByBullerContainer;
    private List<ScannedRobotEvent> scannedContainer = new ArrayList<ScannedRobotEvent>();

    public void run() {

        turnLeft(getHeading());

        setAdjustRadarForRobotTurn(true);

        while (true) {
            //Radar FailSafe
            turnRadarRight(FULL_TURN);
        }
    }

    private void log(String message) {
        out.println(message);
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
        if (logEnabled) {
            printScannedRobotEvent(e);
        }

        // bulletPower
        double bulletPower = Math.min(BULLET_POWER_DIVISOR / e.getDistance(), MAX_BULLET_POWER);
        //Predicted time:
        long time = (long) (e.getDistance() / (20 - bulletPower * MAX_BULLET_POWER)); //Defaults: bulletSpeed = 20 - bulletPower * 3; //TODO check this is actually equals max bullet power
        log(" >>> {time: " + time + "}");

        // Lock enemy tank (almost 99% times)
        // TODO: Improve this method using predictive shooting
        double gunTurnAmount = normalRelativeAngleDegrees((getHeading() + e.getBearing() + (time * e.getVelocity() / 20)) - this.getGunHeading());
        turnGunRight(gunTurnAmount);
        if (logEnabled) {
            log("{gunAmountTurn: " + gunTurnAmount + "}");
        }

        // TODO: Improve this method using better predictive enemy future position
        // Fire enemy tank
        if (logEnabled) {
            log("{bulletPower: " + bulletPower + "}");
        }
        fire(bulletPower);

        //Be at 90º of enemy
        turnRight(normalRelativeAngleDegrees(e.getBearing()) + 90);

        //Avoid enemy shoots
        if (enemyPreviousEnergy > e.getEnergy()) {
            if (logEnabled) {
                String log = "";
                log += "Energy { " +
                    "enemyPreviousEnergy: " + enemyPreviousEnergy + "," +
                    "e.getEnergy(): " + e.getEnergy() + "," +
                    "Distance: " + e.getBearing() + "}";
                log(log);
            }

            //Try to avoid enemy fire
            if (moveForward) {
                ahead(100);
            } else {
                back(100);
            }
        }

        // Call scan again
        scan();
    }

    private void printScannedRobotEvent(ScannedRobotEvent e) {

        if (logEnabled) {
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
    }

    public void onHitWall(HitWallEvent e) {
        if (logEnabled) {
            log("{HitWall e.getBearing(): " + e.getBearing() + "}");
        }
        moveForward = !moveForward;
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
