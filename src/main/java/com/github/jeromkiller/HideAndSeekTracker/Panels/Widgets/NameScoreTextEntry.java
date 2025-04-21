package com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class NameScoreTextEntry extends JFormattedTextField implements FocusListener {
    public NameScoreTextEntry(String value) {
        DefaultFormatter formatter = new DefaultFormatter();
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        AbstractFormatterFactory formatterFactory = new DefaultFormatterFactory(formatter);
        setFormatterFactory(formatterFactory);

        setValue(value);
    }

    @Override
    public void focusGained(FocusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                selectAll();
            }
        });
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
