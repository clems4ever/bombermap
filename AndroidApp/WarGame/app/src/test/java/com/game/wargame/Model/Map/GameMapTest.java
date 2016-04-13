package com.game.wargame.Model.Map;

import com.game.wargame.Model.Entities.VirtualMap.Map;
import com.game.wargame.Model.Entities.VirtualMap.Cell;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;

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
        Map virtualMap = new Map(10, 20, null);

        assertEquals(10, virtualMap.width());
        assertEquals(20, virtualMap.height());
    }

    @Test
    public void testThatCellsAreCorrectlySet() {
        Map virtualMap = new Map(3, 3, null);
        Cell mapCell = new Cell(CellTypeEnum.BLOCK);

        virtualMap.set(0, 2, mapCell);

        assertEquals(mapCell, virtualMap.get(0, 2));
        assertNotEquals(mapCell, virtualMap.get(1, 1));
    }

}
