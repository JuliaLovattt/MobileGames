package com.example.dinodigger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.dinodigger.entities.GameCharacters;
import com.example.dinodigger.helpers.GameConstants;
import com.example.dinodigger.inputs.TouchEvents;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint();
    private SurfaceHolder holder;
    private float x = 200, y = 200;
    private boolean movePlayer;
    private PointF lastTouchDiff;
    private Random rand = new Random();
    private TouchEvents touchEvents;
    private GameLoop gameLoop;
    //private ArrayList<PointF> mushrooms = new ArrayList<>();
    private PointF ghostPos;
    private int ghostDir = GameConstants.Face_Dir.DOWN;
    private long lastDirChange = System.currentTimeMillis();
    private int playerFaceDir = GameConstants.Face_Dir.DOWN, playerAniIndexY;
    private int aniTick;
    private int aniSpeed = 10;

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);
        gameLoop = new GameLoop(this);

        ghostPos = new PointF(rand.nextInt(1080), rand.nextInt(1920));


    }


    public void render() {
            Canvas c = holder.lockCanvas();
            c.drawColor(Color.BLACK);
            touchEvents.draw(c);

            c.drawBitmap(GameCharacters.PLAYER.getSprite(playerFaceDir, playerAniIndexY), x, y, null);
            c.drawBitmap(GameCharacters.GHOST.getSprite(ghostDir, playerAniIndexY), ghostPos.x, ghostPos.y, null);

            holder.unlockCanvasAndPost(c);
    }
    public void update(double delta) {

        if(System.currentTimeMillis() - lastDirChange >= 5000) {
            ghostDir = rand.nextInt(4);
            lastDirChange = System.currentTimeMillis();
        }
        switch (ghostDir) {
            case GameConstants.Face_Dir.DOWN:
                ghostPos.y += delta * 300;
                if (ghostPos.y >= 1720)
                    ghostDir = GameConstants.Face_Dir.UP;
                break;

            case GameConstants.Face_Dir.UP:
                ghostPos.y -= delta * 300;
                if (ghostPos.y <= 0)
                    ghostDir = GameConstants.Face_Dir.DOWN;
                break;

            case GameConstants.Face_Dir.RIGHT:
                ghostPos.x += delta * 300;
                if (ghostPos.x >= 1080)
                    ghostDir = GameConstants.Face_Dir.LEFT;
                break;

            case GameConstants.Face_Dir.LEFT:
                ghostPos.x -= delta * 300;
                if (ghostPos.x <= 0)
                    ghostDir = GameConstants.Face_Dir.RIGHT;
                break;
        }
        updatePlayerMove(delta);

        updateAnimation();
    }

    private void updatePlayerMove(double delta) {
        if (!movePlayer)
            return;

        float baseSpeed = (float) (delta * 300);
        float ratio = Math.abs(lastTouchDiff.y) / Math.abs(lastTouchDiff.x);
        double angle = Math.atan(ratio);

        float xSpeed = (float) Math.cos(angle);
        float ySpeed = (float) Math.sin(angle);

       // Log.v("GamePanel", "Angle: " + Math.toDegrees(angle));
       // Log.v("GamePanel", "xSpeed: "+ xSpeed  +  " |  ySpeed: " + ySpeed);

        if (xSpeed > ySpeed) {
            if (lastTouchDiff.x > 0) playerFaceDir = GameConstants.Face_Dir.RIGHT;
            else playerFaceDir = GameConstants.Face_Dir.LEFT;
        } else {
            if (lastTouchDiff.y > 0) playerFaceDir = GameConstants.Face_Dir.DOWN;
            else playerFaceDir = GameConstants.Face_Dir.UP;
        }

        if (lastTouchDiff.x < 0)
            xSpeed *= -1;
        if (lastTouchDiff.y < 0)
            ySpeed *= -1;

        x += xSpeed * baseSpeed;
        y += ySpeed * baseSpeed;


    }
    private void updateAnimation(){
        if (!movePlayer)
            return;
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            playerAniIndexY++;
            if (playerAniIndexY >= 3)
                playerAniIndexY = 0;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchEvents.touchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
    public void setPlayerMoveTrue(PointF lastTouchDiff) {
        movePlayer = true;
        this.lastTouchDiff = lastTouchDiff;
    }

    public void setPlayerMoveFalse() {
        movePlayer = false;
        resetAnimation();
    }

    private void resetAnimation() {
        aniTick = 0;
        playerAniIndexY = 0;
    }

}