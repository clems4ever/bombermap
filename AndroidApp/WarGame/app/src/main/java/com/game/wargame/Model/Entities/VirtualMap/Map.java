package com.game.wargame.Model.Entities.VirtualMap;

/**
 * Created by developer on 3/20/16.
 */
public class Map {

    int m_width;
    int m_height;
    Cell[][] m_map;

    public Map(int width, int height) {
        m_width = width;
        m_height = height;

        m_map = new Cell[m_width][m_height];

        for(int x=0; x<m_width; ++x) {
            for(int y=0; y<m_height; ++y) {
                m_map[x][y] = new Cell(CellTypeEnum.EMPTY);
            }
        }
    }

    public void setCell(int x, int y, Cell cell) {
        m_map[x][y] = cell;
    }

    public Cell cell(int x, int y) {
        return m_map[x][y];
    }

    public int width() {
        return m_width;
    }

    public int height() {
        return m_height;
    }
}
