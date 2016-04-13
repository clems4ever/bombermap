package com.game.wargame.Model.Entities.VirtualMap;

import java.util.ArrayList;

/**
 * Created by developer on 3/20/16.
 */
public class Map<T> {

    int m_width;
    int m_height;
    ArrayList<ArrayList<T>> mMap;

    public Map(int width, int height, T defaultValue) {
        m_width = width;
        m_height = height;

        mMap = new ArrayList<>(m_width);
        for(int i=0; i<m_width; ++i) {
            ArrayList<T> list = new ArrayList<T>(m_height);
            for(int j=0; j<m_height; ++j) {
                list.add(defaultValue);
            }
            mMap.add(list);
        }
    }

    public void set(int x, int y, T cell) {
        mMap.get(x).set(y, cell);
    }

    public T get(int x, int y) {
        return mMap.get(x).get(y);
    }

    public int width() {
        return m_width;
    }

    public int height() {
        return m_height;
    }
}
