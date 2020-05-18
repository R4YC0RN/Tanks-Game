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
    private boolean[] canSpawn;
    public boolean pause = false;
    public int id = GameMapView.enemyStartPos.length - 1;

    public AddEnemyToField(ArrayList<EnemyTank> enemies){
        this.enemies = enemies;
        enemiesEnded = false;
        numOfSpawned = 1;
        canSpawn = new boolean[GameMapView.enemyStartPos.length];
        for(int spawnIndex = 0; spawnIndex < GameMapView.enemyStartPos.length; spawnIndex++){
            canSpawn[spawnIndex] = true;
        }
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
                //boolean findFree = true;
                boolean foundedObstacle = true;
                int pos = rand.nextInt(GameMapView.enemyStartPos.length);
                while(foundedObstacle){
                    foundedObstacle = false;
                    for(int index = 0; index < GameMapView.enemyStartPos.length; index++){
                        if (pos == index && !canSpawn[index]) {
                            rand = new Random();
                            pos = rand.nextInt(GameMapView.enemyStartPos.length);
                            System.out.println("----Try to find new point----");
                            foundedObstacle = true;
                            break;
                        }
                    }
                }
                //int posToSet = pos;
                if(!pause){
                    EnemyTank enemyTank = new EnemyTank(GameMapView.enemyStartPos[pos][0], GameMapView.enemyStartPos[pos][1]);
                    enemyTank.sprite.setImage(enemyTank.getTank1DownImg());
                    enemyTank.sprite.setPosition(GameMapView.enemyStartPos[pos][0], GameMapView.enemyStartPos[pos][1]);
                    enemyTank.sprite.setSize(EnemyTank.tankSize);
                    this.id++;
                    enemyTank.id = this.id;
                    enemies.add(enemyTank);
                    numOfSpawned++;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCanSpawn(boolean status, int index){
        canSpawn[index] = status;
    }
}
