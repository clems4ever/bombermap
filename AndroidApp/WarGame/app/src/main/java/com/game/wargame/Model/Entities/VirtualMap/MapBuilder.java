package com.game.wargame.Model.Entities.VirtualMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * Created by clement on 17/04/16.
 */
public class MapBuilder {

    private Resources mResources;

    public MapBuilder(Resources resources) {
        mResources = resources;
    }

    public Map<Cell> build(int resId) {
        Bitmap image = BitmapFactory.decodeResource(mResources, resId);

        Map<Cell> map = new Map(image.getWidth(), image.getHeight(), new Cell(CellTypeEnum.EMPTY));

        for(int i=0; i<image.getWidth(); ++i) {
            for(int j=0; j<image.getHeight(); ++j) {
                int c = image.getPixel(i, j);
                int alpha = Color.alpha(c);

                if(alpha > 128) {
                    map.set(i, j, new Cell(CellTypeEnum.BLOCK));
                }
            }
        }

        return map;
    }

}
