package com.tanksgame.threads;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.Bullet;
import com.tanksgame.objects.EnemyTank;

public class EnemyAddBulletThread extends Thread{
    EnemyTank enemy;
    private Bullet bullet;
    public boolean pause = false;

    public EnemyAddBulletThread(EnemyTank enemy){
        this.enemy = enemy;
    }

    @Override
    public void run(){
        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println("Thread has been interrupted");
        }
        while(enemy.alive){
            try{
                Thread.sleep(2000);
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println("Thread has been interrupted");
            }
            System.out.println(currentThread().getName());
            if(!enemy.shootPause){
                addBullet();
            }
        }
    }

    public void addBullet(){
        if("UP".equals(enemy.orient)){
            bullet = new Bullet(enemy.currentTankPosX + Bullet.bulletWidth / enemy.tankSize, enemy.currentTankPosY, enemy.orient);
        }
        if("DOWN".equals(enemy.orient)){
            bullet = new Bullet(enemy.currentTankPosX + Bullet.bulletWidth / enemy.tankSize, enemy.currentTankPosY + enemy.tankSize / GameMapView.tileSize, enemy.orient);
        }
        if("LEFT".equals(enemy.orient)){
            bullet = new Bullet(enemy.currentTankPosX, enemy.currentTankPosY + Bullet.bulletWidth / enemy.tankSize, enemy.orient);
        }
        if("RIGHT".equals(enemy.orient)){
            bullet = new Bullet(enemy.currentTankPosX + enemy.tankSize / GameMapView.tileSize , enemy.currentTankPosY + Bullet.bulletWidth / enemy.tankSize, enemy.orient);
        }
        bullet.speed = 0.07;
        enemy.bullets.add(bullet);
    }
}
