package com.github.jeromkiller.HideAndSeekTracker;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.HashMap;

public class HideAndSeekTable extends JTable {
    HideAndSeekTable(HashMap<String, HideAndSeekPlayer> data)
    {
        super(new HideAndSeekTableModel(data));
        RowFilter<HideAndSeekTableModel, Object> filter = RowFilter.numberFilter(RowFilter.ComparisonType.NOT_EQUAL, Integer.MAX_VALUE,0, 1);
        TableRowSorter<HideAndSeekTableModel> sorter = new TableRowSorter<>(model());
        setRowSorter(sorter);
        sorter.setRowFilter(filter);
        setDefaultRenderer(HideAndSeekPlayer.Placement.class, new HideAndSeekPlacementRenderer() );
    }

    public void update()
    {
        HideAndSeekTableModel model = model();
        model.update();
        getColumnModel().getColumn(0).setMinWidth(1);
        getColumnModel().getColumn(0).setMaxWidth(1);
        getColumnModel().getColumn(1).setMinWidth(90);
        this.getRowSorter().toggleSortOrder(0);
    }

    public HideAndSeekTableModel model()
    {
        return (HideAndSeekTableModel) getModel();
    }
}
