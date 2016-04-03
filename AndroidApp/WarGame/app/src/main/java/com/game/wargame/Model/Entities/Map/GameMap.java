package com.game.wargame.Model.Entities.Map;

/**
 * Created by developer on 3/20/16.
 */
public class GameMap {

    int m_width;
    int m_height;
    MapCell[][] m_map;

    public GameMap(int width, int height) {
        m_width = width;
        m_height = height;

        m_map = new MapCell[m_width][m_height];

        for(int x=0; x<m_width; ++x) {
            for(int y=0; y<m_height; ++y) {
                m_map[x][y] = new MapCell(MapCellType.EMPTY);
            }
        }
    }

    public void setCell(int x, int y, MapCell cell) {
        m_map[x][y] = cell;
    }

    public MapCell cell(int x, int y) {
        return m_map[x][y];
    }

    public int width() {
        return m_width;
    }

    public int height() {
        return m_height;
    }
}
