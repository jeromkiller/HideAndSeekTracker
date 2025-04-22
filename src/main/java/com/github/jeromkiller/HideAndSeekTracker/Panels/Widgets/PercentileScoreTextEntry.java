package com.github.jeromkiller.HideAndSeekTracker.Panels.Widgets;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

public class PercentileScoreTextEntry extends JFormattedTextField implements FocusListener {
    private final int maxValue;
    private final JFormattedTextField.AbstractFormatterFactory formatterFactory;

    public PercentileScoreTextEntry(int value, int maxValue) {
        this.maxValue = maxValue;
        setValue(value);
        setText(toPresentation());

        // formating rules for the setting box
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(maxValue);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        formatterFactory = new DefaultFormatterFactory(formatter);

        super.addFocusListener(this);
    }

    private String toPresentation() {
        StringBuilder presentation = new StringBuilder();
        final int value = (int)getValue();
        if (value == maxValue) {
            presentation.append(value);
        } else {
            presentation.append(maxValue);
            presentation.append(" - ");
            presentation.append(value);
        }
        presentation.append("%");
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
