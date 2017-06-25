package com.gft.codejam.roomba;

import robocode.*;

import java.util.ArrayList;

import static robocode.util.Utils.normalRelativeAngleDegrees;


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

        if(scannedContainer.isEmpty()){
            enemyPreviusEnergy = 100;
        }else {
            enemyPreviusEnergy = scannedContainer.get(scannedContainer.size()-1).getEnergy();
        }

        this.scannedContainer.add(e);
        printScannedRobotEvent(e);

        // TODO: Improve bug when changing directions that produces a full weapon turn
        // Lock enemy tank (almost 99% times)
        double gunTurnAmount  = normalRelativeAngleDegrees((getHeading() + e.getBearing()) - this.getGunHeading());

        turnGunRight(gunTurnAmount);
        out.println("{gunAmountTurn: " + gunTurnAmount + "}");

        fireByDistance(e.getDistance());

        if(enemyPreviusEnergy < e.getEnergy()){
            String log = "";
            log += "Energy { " +
                "enemyPreviusEnergy: " + enemyPreviusEnergy + "," +
                "e.getEnergy(): " + e.getEnergy() + "," +
                "Distance: " + e.getBearing() + "}";
            out.println(log);

            if(e.getBearing() > 0)
                back(200);
            else
                ahead(200);
        }

        // Call scan again, before we turn the gun
        scan();
    }

    /* TODO: Improve this
    Basic FireByDistance method */
    private void fireByDistance(double distance){

        if(distance < 200) {
            fire(3);
        } else if (distance < 400 ) {
            fire(2);
        } else if (distance < 600 ) {
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

    public void onHitWall(HitWallEvent e){
        out.println("{HitWall e.getBearing(): " + e.getBearing() + "}");
        turnRight(90);
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
