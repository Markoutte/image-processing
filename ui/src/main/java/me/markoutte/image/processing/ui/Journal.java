package me.markoutte.image.processing.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Journal {

    private ObservableList<Note> notes = FXCollections.observableList(new ArrayList<>());

    public static Journal get() {
        return JournalHolder.INSTANCE;
    }

    public void debug(String message) {
        log(message, Level.DEBUG);
    }

    public void info(String message) {
        log(message, Level.INFO);
    }

    public void warn(String message) {
        log(message, Level.WARN);
    }

    public void error(String message) {
        log(message, Level.ERROR);
    }

    public void log(String message, Level level) {
        notes.add(new Note(message, level));
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public enum Level {
        ERROR, WARN, INFO, DEBUG
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

    private Journal() {
    }

    private final static class JournalHolder {
        private static final Journal INSTANCE = new Journal();
    }
}
