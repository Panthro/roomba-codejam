package com.gft.codejam.roomba;

import robocode.*;

import java.util.ArrayList;

import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * Created by panthro on 22/06/2017.
 */
public class Roomba extends Robot {

    private boolean moveForward = true;
    private double lastBulletHitEnergy = 0.0;
    private boolean log = false;

    //Variables for future use:
    //TODO: We should improve this. Over-storage of information
    public ArrayList<BulletHitEvent> bulletHitInfoContainer;
    public ArrayList<HitByBulletEvent> hitByBullerContainer;
    public ArrayList<ScannedRobotEvent> scannedContainer;

    public void run() {
        ConfigureStartingMatch();

        turnLeft(getHeading());

        setAdjustRadarForRobotTurn(true);

        while(true) {
            //Radar FailSafe
            turnRadarRight(360);
        }
    }


    public void onScannedRobot(ScannedRobotEvent e) {

        double enemyPreviusEnergy;

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
        if(scannedContainer.isEmpty()){
            enemyPreviusEnergy = 100;
        }else if (lastBulletHitEnergy > 0) {
            //When we hit, we lower energy, we should take into account this
            enemyPreviusEnergy = lastBulletHitEnergy;
        } else {
            enemyPreviusEnergy = scannedContainer.get(scannedContainer.size()-1).getEnergy();
            lastBulletHitEnergy = 0;
        }
        this.scannedContainer.add(e);
        if(log) {
            printScannedRobotEvent(e);
        }

        // bulletPower
        double bulletPower = Math.min(782 / e.getDistance(), 3);
        //Predicted time:
        long time = (long)(e.getDistance() / (20 - bulletPower * 3)); //Defaults: bulletSpeed = 20 - bulletPower * 3;
        out.println(" >>> {time: " + time + "}");

        // Lock enemy tank (almost 99% times)
        // TODO: Improve this method using predictive shooting
        double gunTurnAmount = normalRelativeAngleDegrees((getHeading() + e.getBearing() + (time * e.getVelocity() / 20)) - this.getGunHeading());
        turnGunRight(gunTurnAmount);
        if(log) {
            out.println("{gunAmountTurn: " + gunTurnAmount + "}");
        }

        // TODO: Improve this method using better predictive enemy future position
        // Fire enemy tank
        if(log) {
            out.println("{bulletPower: " + bulletPower + "}");
        }
        fire(bulletPower);

        //Be at 90º of enemy
        turnRight(normalRelativeAngleDegrees(e.getBearing()) + 90);

        //Avoid enemy shoots
        if(enemyPreviusEnergy > e.getEnergy()){
            if(log) {
                String log = "";
                log += "Energy { " +
                    "enemyPreviusEnergy: " + enemyPreviusEnergy + "," +
                    "e.getEnergy(): " + e.getEnergy() + "," +
                    "Distance: " + e.getBearing() + "}";
                out.println(log);
            }

            //Try to avoid enemy fire
            if(moveForward){
                ahead(100);
            }else{
                back( 100);
            }
        }

        // Call scan again
        scan();
    }

    private void printScannedRobotEvent(ScannedRobotEvent e){
        String logTxt = "";

        logTxt += "ScannedRobotEvent { " +
            "Bearing: " + e.getBearing() + "," +
            "BearingRads: " + e.getBearingRadians() + "," +
            "Distance: " + e.getBearing() + "," +
            "Energy: " + e.getBearing() + "," +
            "Heading: " + e.getBearing() + "," +
            "HeadingRads: " + e.getHeadingRadians() + "," +
            "Velocity: " + e.getVelocity() + "}";

        if(log) {
            out.println(logTxt);
        }
    }

    public void onHitWall(HitWallEvent e){
        if(log) {
            out.println("{HitWall e.getBearing(): " + e.getBearing() + "}");
        }
        moveForward = !moveForward;
    }

    public void onHitByBullet(HitByBulletEvent e) {
        //Logs Event
        //this.hitByBullerContainer.add(e);
        //out.println(e.toString());

        //When we are hit by bullet, we cannot use last bullet energy as the enemy tank regenerates energy
        lastBulletHitEnergy = 0;
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
        //out.println(e.toString());
        //When we hit a tank with a bullet, we reduce enemy max energy.
        lastBulletHitEnergy = e.getEnergy();
        /*
            Bullet getBullet();
            double getEnergy();
            String getName();
        */
    }


    private void ConfigureStartingMatch() {
        this.bulletHitInfoContainer = new ArrayList<BulletHitEvent>();
        this.hitByBullerContainer = new ArrayList<HitByBulletEvent>();
        this.scannedContainer = new ArrayList<ScannedRobotEvent>();
    }
}
