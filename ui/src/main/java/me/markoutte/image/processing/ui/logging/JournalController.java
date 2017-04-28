package me.markoutte.image.processing.ui.logging;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.net.URL;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JournalController implements Initializable {

    @FXML
    private StyleClassedTextArea textArea;

    @FXML
    private ListProperty<JournalHandler.Note> notes = new SimpleListProperty<>();

    private Stage stage;

    private static final String INFO = "(^|\n)\\[" + Level.INFO + "\\].*";
    private static final String WARN = "(^|\n)\\[" + Level.WARNING + "\\].*";
    private static final String ERROR = "(^|\n)\\[" + Level.SEVERE + "\\].*";
    private static final String DEBUG = "(^|\n)\\[" + Level.FINE + "\\].*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<INFO>" + INFO + ")"
            + "|(?<WARN>" + WARN + ")"
            + "|(?<ERROR>" + ERROR + ")"
            + "|(?<DEBUG>" + DEBUG + ")"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Handler[] handlers = Logger.getLogger("journal").getHandlers();
        JournalHandler journal = null;
        for (Handler handler : handlers) {
            if (handler instanceof JournalHandler) {
                journal = (JournalHandler) handler;
                break;
            }
        }
        notes.setValue(Objects.requireNonNull(journal, "No logging handler found").getNotes());
        notes.addListener((ListChangeListener<? super JournalHandler.Note>) observable -> {
            if (!stage.isShowing()) {
                return;
            }
            while (observable.next()) {
                List<? extends JournalHandler.Note> added = observable.getAddedSubList();
                String text = added.stream().map(this::getJournalMessage).collect(Collectors.joining("\n"));
                textArea.replaceText(textArea.getLength(), textArea.getLength(), "\n" + text);
            }
        });
        textArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    textArea.setStyleSpans(0, computeHighlighting(textArea.getText()));
                });
    }

    public void update() {

        textArea.replaceText(notes.stream().map(this::getJournalMessage).collect(Collectors.joining("\n")));
    }

    private String getJournalMessage(JournalHandler.Note note) {
        return String.format("[%s %s", note.getLevel() + "]", note.getMessage());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.addEventHandler(WindowEvent.WINDOW_SHOWING, event -> update());
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("INFO") != null ? "info" :
                    matcher.group("WARN") != null ? "warn" :
                    matcher.group("ERROR") != null ? "error" :
                    matcher.group("DEBUG") != null ? "debug" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
