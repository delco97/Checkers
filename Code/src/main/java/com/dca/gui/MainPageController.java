package com.dca.gui;

import java.awt.*;
import java.io.IOException;

import com.dca.App;
import javafx.fxml.FXML;

public class MainPageController {
    
    @FXML
    private void btn2Players_onAction() throws IOException {
        App.setRoot("gameSettings");
    }
}
