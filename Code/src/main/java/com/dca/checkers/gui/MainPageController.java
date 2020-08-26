package com.dca.checkers.gui;

import java.io.IOException;

import com.dca.checkers.App;
import javafx.fxml.FXML;

public class MainPageController {
    
    @FXML
    private void btn2Players_onAction() throws IOException {
        App.setRoot("gameSettings");
    }
}
