package com.librigo.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.librigo.Model.AdherentDAO;
import com.librigo.Model.PasswordUtils;

public class LostPasswordController {

    @FXML private Button backToLoginButton;
    @FXML private Button sendButton;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    @FXML
    private void handleSendButton() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            messageLabel.setText("Veuillez entrer votre adresse email.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-label");
            return;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            messageLabel.setText("Veuillez entrer une adresse email valide.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-label");
            return;
        }

        AdherentDAO adherentDAO = new AdherentDAO();

        if (!adherentDAO.emailExists(email)) {
            messageLabel.setText("Cette adresse email n'est pas enregistrée.");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-label");
            return;
        }

        String newPassword = "password";
        String hash = PasswordUtils.hashPassword(newPassword);
        adherentDAO.updatePasswordByEmail(email, hash);

        messageLabel.setText("Le nouveau mot de passe est 'password'. Veuillez le changer après votre connexion.");
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("success-label");
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
