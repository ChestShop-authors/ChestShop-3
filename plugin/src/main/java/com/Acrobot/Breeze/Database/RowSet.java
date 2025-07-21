package com.Acrobot.Breeze.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Acrobot
 */
public class RowSet {
    private List<Row> rowList = new ArrayList<Row>();

    /**
     * Adds a row to the set
     *
     * @param row Row to add
     */
    public void add(Row row) {
        rowList.add(row);
    }

    /**
     * Retrieves a row from its index
     *
     * @param index Row's index
     * @return Row
     */
    public Row get(int index) {
        return rowList.get(index);
    }

    /**
     * Returns the number of rows inside this RowSet
     *
     * @return row number
     */
    public int size() {
        return rowList.size();
    }

    /**
     * Returns if the RowSet is empty
     *
     * @return Is the RowSet empty?
     */
    public boolean isEmpty() {
        return size() == 0;
    }
}