private void showPopup(String content) {
        Platform.runLater(() -> {
        Popup popup = new Popup();
        HBox box = new HBox();
        Text text = new Text(content);
        box.getChildren().add(text);

        box.setBackground(new Background(new BackgroundFill(Color.AZURE, CornerRadii.EMPTY, Insets.EMPTY)));
        box.setPrefWidth(stage.getWidth());
        box.setPrefHeight(50);

        box.setAlignment(Pos.CENTER);

        popup.setX(stage.getX());
        popup.setY(stage.getY() + stage.getScene().getY());
        popup.getContent().add(box);
        popup.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> popup.hide());

        Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.millis(2000));
        timeline.getKeyFrames().add(key);
        timeline.setOnFinished((ae) -> popup.hide());

        popup.show(stage);
        timeline.play();
        });
}