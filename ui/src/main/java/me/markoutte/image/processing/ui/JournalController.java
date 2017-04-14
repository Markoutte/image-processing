package me.markoutte.image.processing.ui;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JournalController implements Initializable {

    @FXML
    private StyleClassedTextArea textArea;

    @FXML
    private ListProperty<Journal.Note> notes = new SimpleListProperty<>();

    private Stage stage;

    private static final String INFO = "(^|\n)\\[" + Journal.Level.INFO.name() + "\\].*";
    private static final String WARN = "(^|\n)\\[" + Journal.Level.WARN.name() + "\\].*";
    private static final String ERROR = "(^|\n)\\[" + Journal.Level.ERROR.name() + "\\].*";
    private static final String DEBUG = "(^|\n)\\[" + Journal.Level.DEBUG.name() + "\\].*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<INFO>" + INFO + ")"
            + "|(?<WARN>" + WARN + ")"
            + "|(?<ERROR>" + ERROR + ")"
            + "|(?<DEBUG>" + DEBUG + ")"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notes.setValue(Journal.get().getNotes());
        notes.addListener((ListChangeListener<? super Journal.Note>) observable -> {
            if (!stage.isShowing()) {
                return;
            }
            while (observable.next()) {
                List<? extends Journal.Note> added = observable.getAddedSubList();
                final StringBuilder text = new StringBuilder();
                for (Journal.Note note : added) {
                    text.append(getJournalMessage(note));
                }
                textArea.replaceText(textArea.getLength(), textArea.getLength(), text.toString());
            }
        });
        textArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    textArea.setStyleSpans(0, computeHighlighting(textArea.getText()));
                });
    }

    public void update() {
        final StringBuilder text = new StringBuilder();
        for (Journal.Note note : notes.get()) {
            text.append(getJournalMessage(note));
        }
        textArea.replaceText(text.toString());
    }

    private String getJournalMessage(Journal.Note note) {
        return String.format("[%-6s %s\n", note.getLevel().name() + "]", note.getMessage());
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