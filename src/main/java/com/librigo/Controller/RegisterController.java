package com.librigo.Controller;

import com.librigo.Model.PasswordUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.librigo.Model.AdherentDAO;
import java.sql.Date;

public class RegisterController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField adresseField;
    @FXML private TextField cpField;
    @FXML private TextField villeField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;
    @FXML private Label errorLabel;

    @FXML
    private void handleRegisterButton() {

        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() || passwordField.getText().isEmpty() ||
            birthDatePicker.getValue() == null || adresseField.getText().isEmpty() ||
            cpField.getText().isEmpty() || villeField.getText().isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!emailField.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            errorLabel.setText("Veuillez entrer une adresse email valide.");
            return;
        }

        // le mot de passe doit contenir au moins 8 caractères
        if (passwordField.getText().length() < 8) {
            errorLabel.setText("Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        AdherentDAO adherentDAO = new AdherentDAO();

        if (adherentDAO.emailExists(emailField.getText())) {
            errorLabel.setText("Cette adresse email est déjà utilisée.");
            return;
        }

        String hash = PasswordUtils.hashPassword(passwordField.getText());

        String adherentNumber = adherentDAO.generateUniqueAdherentNumber();

        adherentDAO.registerAdherent(
                nomField.getText(),
                prenomField.getText(),
                emailField.getText(),
                hash,
                Date.valueOf(birthDatePicker.getValue()),
                adresseField.getText(),
                cpField.getText(),
                villeField.getText(),
                adherentNumber
        );

        errorLabel.setText("Inscription réussie.");
        errorLabel.setId(null);
        errorLabel.getStyleClass().setAll("success-label");
    }


    @FXML
    private void handleBackToLoginButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setTitle("LibriGo - Connexion");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
