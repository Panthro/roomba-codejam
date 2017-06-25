package com.gft.codejam.roomba;

import robocode.*;

import java.util.ArrayList;


/**
 * Created by panthro on 22/06/2017.
 */
public class Roomba extends Robot {

    //Variables for future use
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

        /*   ScannedRobotEvent Info:
             double	getBearing()
             double	getBearingRadians()
             double	getDistance()
             double	getEnergy()
             double	getHeading()
             double	getHeadingRadians()
             double getVelocity()
         */

        this.scannedContainer.add(e);
        printScannedRobotEvent(e);

        // TODO: Improve bug when changing directions that produces a full weapon turn
        // Lock enemy tank (almost 99% times)
        double gunTurnAmount  = getHeading() - getGunHeading() + e.getBearing();

        if(gunTurnAmount == 360 || gunTurnAmount == -360) {
            gunTurnAmount = 0;
        }
        if(e.getBearing() < 0){
            gunTurnAmount += 360;
        }
        turnGunRight(gunTurnAmount);

        out.println("{gunAmountTurn: " + gunTurnAmount + "}");

        fireByDistance(e.getDistance());

        // Call scan again, before we turn the gun
        scan();
    }

    /* TODO: Improve this
    Basic FireByDistance method */
    private void fireByDistance(double distance){

        if(distance < 100) {
            fire(3);
        } else if (distance < 200 ) {
            fire(2);
        } else if (distance < 300 ) {
            fire(1);
        } else {
            fire(.5);
        }

    }

    private void printScannedRobotEvent(ScannedRobotEvent e){
        String log = "";

        log += "ScannedRobotEvent { " +
            "Bearing: " + e.getBearing() + "," +
            "BearingRads: " + e.getBearingRadians() + "," +
            "Distance: " + e.getBearing() + "," +
            "Energy: " + e.getBearing() + "," +
            "Heading: " + e.getBearing() + "," +
            "HeadingRads: " + e.getHeadingRadians() + "," +
            "Velocity: " + e.getVelocity() + "}";

        out.println(log);
    }

    public void onHitByBullet(HitByBulletEvent e) {

        //Logs Event
        //this.hitByBullerContainer.add(e);
        //out.println(e.toString());
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
