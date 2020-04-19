package com.tanksgame.threads;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.EnemyTank;

import java.util.ArrayList;
import java.util.Random;

public class AddEnemyToField extends Thread{
    ArrayList<EnemyTank> enemies;
    private boolean enemiesEnded;
    private int totalNumOfEnemies = GameMapView.totalNumOfEnemy;
    private int numOfSpawned;

    public AddEnemyToField(ArrayList<EnemyTank> enemies){
        this.enemies = enemies;
        enemiesEnded = false;
        numOfSpawned = 1;
    }

    @Override
    public void run(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(!enemiesEnded){
            if(numOfSpawned + 4 >= totalNumOfEnemies){
                enemiesEnded = true;
            }
            if(enemies.size() < GameMapView.maxEnemyOnField){
                System.out.println("Spawn");
                Random rand = new Random();
                int pos = rand.nextInt(GameMapView.enemyStartPos.length);
                EnemyTank enemyTank = new EnemyTank(GameMapView.enemyStartPos[pos][0], GameMapView.enemyStartPos[pos][1]);
                enemyTank.sprite.setImage(enemyTank.getTank1DownImg());
                enemyTank.sprite.setPosition(GameMapView.enemyStartPos[pos][0], GameMapView.enemyStartPos[pos][1]);
                enemyTank.sprite.setSize(EnemyTank.tankSize);
                enemies.add(enemyTank);
                numOfSpawned++;
                System.out.println("Number: " + numOfSpawned);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
