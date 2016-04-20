package com.game.wargame.Model.Entities.VirtualMap;

import android.content.res.Resources;

import com.game.wargame.R;

import java.util.HashMap;

/**
 * Created by clement on 03/04/16.
 */
public class Repository {

    private java.util.Map<Integer, Map> mVirtualMapByMapId;
    private MapBuilder mMapBuilder;

    public Repository(Resources res) {
        mVirtualMapByMapId = new HashMap<>();
        mMapBuilder = new MapBuilder(res);

        createMaps();
    }

    public void pushMap(int mapId, Map<Cell> virtualMap) {
        mVirtualMapByMapId.put(mapId, virtualMap);
    }

    public Map<Cell> get(int mapId) {
        return mVirtualMapByMapId.get(mapId);
    }

    private void createMaps() {
        Map<Cell> mapExample0 = buildMapExample0();
        Map<Cell> mushroomMap = mMapBuilder.build(R.mipmap.mushroom);

        mVirtualMapByMapId.put(0, mapExample0);
        mVirtualMapByMapId.put(1, mushroomMap);
    }

    private Map<Cell> buildMapExample0() {
        Map<Cell> map0 = new Map(16, 16, new Cell(CellTypeEnum.EMPTY));

        for(int i=0; i<map0.width(); ++i) {
            for(int j=0; j<map0.height(); ++j) {
                if(i == 0 || j== 0 || i==map0.width()-1 || j==map0.height()-1) {
                    map0.set(i, j, new Cell(CellTypeEnum.BLOCK));
                }

                if(i == j) {
                    map0.set(i, j, new Cell(CellTypeEnum.BLOCK));
                }
            }
        }
        map0.set(10, 4, new Cell(CellTypeEnum.BLOCK));

        return map0;
    }

}
