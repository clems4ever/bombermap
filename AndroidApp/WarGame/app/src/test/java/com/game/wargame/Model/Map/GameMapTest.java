package com.game.wargame.Model.Map;

import com.game.wargame.Model.Entities.Map.GameMap;
import com.game.wargame.Model.Entities.Map.MapCell;
import com.game.wargame.Model.Entities.Map.MapCellType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by developer on 3/20/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class GameMapTest {


    @Test
    public void testThatSizeIsCorrect() {
        GameMap gameMap = new GameMap(10, 20);

        assertEquals(10, gameMap.width());
        assertEquals(20, gameMap.height());
    }

    @Test
    public void testThatInitializedMapHasOnlyEmptyCells() {
        int width = 10;
        int height = 14;

        GameMap gameMap = new GameMap(10, 14);

        for(int i=0; i<width; ++i) {
            for(int j=0; j<height; ++j) {
                assertNotNull(gameMap.cell(i, j));
                assertEquals(MapCellType.EMPTY, gameMap.cell(i, j).type());
            }
        }
    }

    @Test
    public void testThatCellsAreCorrectlySet() {
        GameMap gameMap = new GameMap(3, 3);
        MapCell mapCell = new MapCell(MapCellType.BLOCK);

        gameMap.setCell(0, 2, mapCell);

        assertEquals(mapCell, gameMap.cell(0, 2));
        assertNotEquals(mapCell, gameMap.cell(1, 1));
    }

}
