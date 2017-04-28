package me.markoutte.image.processing.ui.logging;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JournalHandler extends Handler {

    private ObservableList<Note> notes = FXCollections.observableList(new ArrayList<>());

    @Override
    public void publish(LogRecord record) {
        if (Level.INFO.intValue() <= record.getLevel().intValue()) {
            notes.add(new Note(record.getMessage(), record.getLevel()));
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public static class Note {
        private final Level level;
        private final String message;

        public Note(String message, Level level) {
            this.level = level;
            this.message = message;
        }

        public Level getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }
    }
}
