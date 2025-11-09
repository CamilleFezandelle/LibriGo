package com.librigo.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class LibraireDashboardController {

    @FXML private Button ajouterButton;
    @FXML private Button livresButton;
    @FXML private Button empruntsButton;
    @FXML private Button adherentsButton;
    @FXML private Hyperlink retourAccueil;

    @FXML
    private void handleAjouterButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/libraireCreateElements.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ajouterButton.getScene().getWindow();
            stage.setTitle("LibriGo - Ajouter un élément");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLivresButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/libraireListeLivres.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) livresButton.getScene().getWindow();
            stage.setTitle("LibriGo - Gestion des livres");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEmpruntsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/libraireListeReservations.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) empruntsButton.getScene().getWindow();
            stage.setTitle("LibriGo - Gestion des emprunts");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdherentsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/libraireListeAdherents.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) adherentsButton.getScene().getWindow();
            stage.setTitle("LibriGo - Gestion des adhérents");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) retourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        stage.setTitle("LibriGo - Accueil");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
