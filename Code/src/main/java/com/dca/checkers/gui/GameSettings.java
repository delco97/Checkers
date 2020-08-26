package com.dca.checkers.gui;

import java.io.IOException;

import com.dca.checkers.App;
import javafx.fxml.FXML;

public class GameSettings {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("main");
    }
}