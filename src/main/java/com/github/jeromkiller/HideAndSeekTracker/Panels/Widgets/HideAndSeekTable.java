package com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets;

import com.github.jeromkiller.HideAndSeekTracker.game.HideAndSeekPlayer;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.HashMap;

public class HideAndSeekTable extends JTable {
    final TableRowSorter<HideAndSeekTableModel> sorter;

    public HideAndSeekTable(HashMap<String, HideAndSeekPlayer> data)
    {
        super(new HideAndSeekTableModel(data));
        sorter = new TableRowSorter<>(model());
        setRowSorter(sorter);
        setDefaultRenderer(String.class, new HideAndSeekPlacementRenderer() );
        setFillsViewportHeight(true);
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

    public void enableHidenPlayerFilter(boolean enable) {
        if(enable) {
            RowFilter<HideAndSeekTableModel, Object> filter = RowFilter.numberFilter(RowFilter.ComparisonType.NOT_EQUAL, Integer.MAX_VALUE, 0, 1);
            sorter.setRowFilter(filter);
        } else {
            sorter.setRowFilter(null);
        }
        update();
    }
}
