package com.librigo.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public static boolean emprunterLivre(int adherentId, int livreId) {
        String insertReservation = "INSERT INTO RESERVATION (adherent_id, livre_id, date_reservation) " +
                       "VALUES (?, ?, CURRENT_DATE)";
        String updateLivre = "UPDATE LIVRE SET disponible = 0 WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            // Début transaction
            conn.setAutoCommit(false);

            PreparedStatement insertStmt = conn.prepareStatement(insertReservation);
            insertStmt.setInt(1, adherentId);
            insertStmt.setInt(2, livreId);
            insertStmt.executeUpdate();

            PreparedStatement updateStmt = conn.prepareStatement(updateLivre);
            updateStmt.setInt(1, livreId);
            updateStmt.executeUpdate();

            // Fin transaction
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rendreLivre(int adherentId, int livreId) {
        String endReservation = "UPDATE RESERVATION SET date_retour = CURRENT_DATE " +
                                "WHERE adherent_id = ? AND livre_id = ? AND date_retour IS NULL";
        String updateLivre = "UPDATE LIVRE SET disponible = 1 WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            // Début transaction
            conn.setAutoCommit(false);

            PreparedStatement endStmt = conn.prepareStatement(endReservation);
            endStmt.setInt(1, adherentId);
            endStmt.setInt(2, livreId);
            endStmt.executeUpdate();

            PreparedStatement updateStmt = conn.prepareStatement(updateLivre);
            updateStmt.setInt(1, livreId);
            updateStmt.executeUpdate();

            // Fin transaction
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<LivreReserved> getAllReservationsWithBooks() {
        List<LivreReserved> reservations = new ArrayList<>();

        String query = "SELECT r.id, " +
                "r.livre_id, " +
                "r.adherent_id, " +
                "r.date_reservation, " +
                "r.date_retour, " +
                "l.ISBN, " +
                "l.titre, " +
                "l.auteur_id, " +
                "a.nom, " +
                "a.prenom, " +
                "l.genre_id, " +
                "g.nom, " +
                "l.date_parution, " +
                "l.description, " +
                "l.disponible, " +
                "ad.nom, " +
                "ad.prenom, " +
                "ad.numero_adherent " +
                "FROM RESERVATION r " +
                "JOIN LIVRE l ON r.livre_id = l.id " +
                "JOIN AUTEUR a ON l.auteur_id = a.id " +
                "JOIN GENRE g ON l.genre_id = g.id " +
                "LEFT JOIN ADHERENT ad ON r.adherent_id = ad.id";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt(1),
                        rs.getInt(3),
                        rs.getInt(2),
                        rs.getDate(4),
                        rs.getDate(5)
                );

                String adherentNom = rs.getString(16);
                String adherentPrenom = rs.getString(17);
                String numeroAdherent = rs.getString(18);
                reservation.setAdherentNom(adherentNom != null ? adherentNom : "Inconnu");
                reservation.setAdherentPrenom(adherentPrenom != null ? adherentPrenom : "");
                reservation.setAdherentNumber(numeroAdherent != null ? numeroAdherent : "");

                Livre livre = new Livre(
                        rs.getInt(2),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getInt(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14),
                        rs.getInt(15)
                );

                reservations.add(new LivreReserved(livre, reservation));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }

}