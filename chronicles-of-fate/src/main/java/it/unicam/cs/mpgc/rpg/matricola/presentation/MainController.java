package it.unicam.cs.mpgc.rpg.matricola.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label statusLabel;

    // Questo metodo verrà chiamato quando premeremo il bottone nella GUI
    @FXML
    public void onStartGameClicked() {
        statusLabel.setText("Partita avviata! (In attesa di collegare il GameService...)");
        System.out.println("Bottone cliccato dall'interfaccia grafica!");
    }
}