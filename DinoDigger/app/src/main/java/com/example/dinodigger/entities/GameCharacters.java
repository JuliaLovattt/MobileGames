package com.example.dinodigger.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dinodigger.MainActivity;
import com.example.dinodigger.R;

public enum GameCharacters {

    PLAYER(R.drawable.female_main),
    GHOST(R.drawable.ghost);

    private Bitmap spriteSheet;
    private Bitmap[][] sprites = new Bitmap[4][3];
    private BitmapFactory.Options options = new BitmapFactory.Options();

    GameCharacters(int resID) {
        options.inScaled = false;
        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for (int j = 0; j < sprites.length; j++) {
            for (int i = 0; i < sprites[j].length; i++) {
//        for (int j = 0; j < 1; j++)
//            for (int i = 0; i < 1; i++)
                sprites[j][i] = getScaledBitmap(Bitmap.createBitmap(spriteSheet, 32 * i, 32 * j, 32, 32));
            }
        }
    }

    public Bitmap getSpriteSheet() {
        return spriteSheet;
    }

    public Bitmap getSprite(int yPos, int xPos) {
        return sprites[yPos][xPos];
    }

    private Bitmap getScaledBitmap(Bitmap bitmap){
        return Bitmap.createScaledBitmap(bitmap,bitmap.getWidth() * 6,bitmap.getHeight()*6,false);
    }

}