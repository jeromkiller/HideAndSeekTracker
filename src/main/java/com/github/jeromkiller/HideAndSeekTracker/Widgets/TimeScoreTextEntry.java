package com.github.jeromkiller.HideAndSeekTracker.Widgets;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeScoreTextEntry extends JFormattedTextField implements FocusListener {
    private final AbstractFormatterFactory formatterFactory;

    public TimeScoreTextEntry(LocalTime value, LocalTime minValue) {
        super.setValue(value);
        super.setText("< " + toPresentation());

        DateFormatter formatter = new DateFormatter(new SimpleDateFormat("HH:mm:ss"));
        formatter.setMinimum(minValue);
        formatter.setMaximum(LocalTime.of(5, 59, 59));
        formatter.setFormat(DateTimeFormatter.ofPattern("HH:mm:ss").toFormat(LocalTime::from));
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        formatterFactory = new DefaultFormatterFactory(formatter);

        super.addFocusListener(this);
    }

    private String toPresentation() {
        return getValue().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public void focusGained(FocusEvent e) {
        setFormatterFactory(formatterFactory);
        setText(toPresentation());
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

    @Override
    public LocalTime getValue() {
        return (LocalTime) super.getValue();
    }
}
