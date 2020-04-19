package com.tanksgame.threads;

import com.tanksgame.maps.GameMapView;
import com.tanksgame.objects.Bullet;
import com.tanksgame.objects.EnemyTank;

public class EnemyAddBulletThread extends Thread{
    EnemyTank enemy = new EnemyTank();
    private Bullet bullet;

    public EnemyAddBulletThread(EnemyTank enemy){
        this.enemy = enemy;
    }

    @Override
    public void run(){
//        System.out.println("Thread has been interrupted");
        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            System.out.println("Thread has been interrupted");
        }
        while(enemy.alive){
            try{
                Thread.sleep(2000);
            }
            catch (InterruptedException e){
                System.out.println("Thread has been interrupted");
            }
            addBullet();
            System.out.println(Thread.currentThread().getName() + " fished..." + "Bullets: " + enemy.bullets.size());

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
