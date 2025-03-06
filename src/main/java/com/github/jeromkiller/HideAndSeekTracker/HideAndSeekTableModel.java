package com.github.jeromkiller.HideAndSeekTracker;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;

public class HideAndSeekTableModel extends AbstractTableModel {
    private final String[] columnNames = {"#", "Name", "Place", "Hints"};
    private final HashMap<String, HideAndSeekPlayer> data;

    HideAndSeekTableModel(HashMap<String, HideAndSeekPlayer> data)
    {
        this.data = data;
    }

    @Override
    public int getRowCount()
    {
        return data.size();
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col)
    {
        ArrayList<HideAndSeekPlayer> list = new ArrayList<>(data.values());
        if(row >= list.size())
        {
            return 0;
        }
        return list.get(row).getValue(col);
    }

    public Class<?> getColumnClass(int c)
    {
        return getValueAt(0, c).getClass();
    }

    public void update()
    {
        fireTableStructureChanged();
    }
}
