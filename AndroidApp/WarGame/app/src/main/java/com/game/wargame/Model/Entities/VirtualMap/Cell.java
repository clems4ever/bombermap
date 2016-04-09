package com.game.wargame.Model.Entities.VirtualMap;

/**
 * Created by developer on 3/20/16.
 */
public class Cell {

    private CellTypeEnum m_type;

    public Cell(CellTypeEnum type) {
        m_type = type;
    }

    public CellTypeEnum type() {
        return m_type;
    }

}
