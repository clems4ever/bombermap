package com.game.wargame.Model.Entities.Map;

/**
 * Created by developer on 3/20/16.
 */
public class MapCell {

    private MapCellType m_type;

    public MapCell(MapCellType type) {
        m_type = type;
    }

    public MapCellType type() {
        return m_type;
    }

}
