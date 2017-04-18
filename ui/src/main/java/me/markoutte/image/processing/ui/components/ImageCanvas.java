package me.markoutte.image.processing.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class ImageCanvas extends Pane {

    private final Image image;
    private final Canvas canvas;

    public ImageCanvas(Image image) {
        this.image = image;
        this.canvas = new Canvas(getWidth(), getHeight());
        getChildren().add(canvas);
        widthProperty().addListener(e -> canvas.setWidth(getWidth()));
        heightProperty().addListener(e -> canvas.setHeight(getHeight()));

        setPrefWidth(image.getWidth());
        setPrefHeight(image.getHeight());
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double ratio = image.getWidth() / image.getHeight();

        double x = 0;
        double y = 0;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (ratio > 1) {
            height = height / ratio;
            y = (canvas.getHeight() - height) / 2;
        } else {
            width = width * ratio;
            x = (canvas.getWidth() - width) / 2;
        }

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.drawImage(image, x, y, width, height);
    }
}
