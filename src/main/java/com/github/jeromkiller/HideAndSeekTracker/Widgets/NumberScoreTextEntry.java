package com.github.jeromkiller.HideAndSeekTracker.Widgets;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

public class NumberScoreTextEntry extends JFormattedTextField implements FocusListener {
    //private int value;
    private final int minValue;
    private final AbstractFormatterFactory formatterFactory;

    public NumberScoreTextEntry(int value, int minValue) {
        this.minValue = minValue;
        setValue(value);
        setText(toPresentation());

        // formating rules for the setting box
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(minValue);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        formatterFactory = new DefaultFormatterFactory(formatter);
        
        super.addFocusListener(this);
    }

    private String toPresentation() {
        StringBuilder presentation = new StringBuilder();
        final int value = (int)getValue();
        if (value == minValue) {
            presentation.append(value);
        } else {
            presentation.append(minValue);
            presentation.append(" - ");
            presentation.append(value);
        }
        return presentation.toString();
    }

    @Override
    public void focusGained(FocusEvent e) {
        setFormatterFactory(formatterFactory);
        setText(String.valueOf(getValue()));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                selectAll();
            }
        });
    }

    @Override
    public void focusLost(FocusEvent e) {
        setFormatterFactory(null);
        setText(toPresentation());
    }
}
