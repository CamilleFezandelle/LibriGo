package com.librigo.Model;

public class LivreReserved {

    private Livre livre;
    private Reservation reservation;

    public LivreReserved(Livre livre, Reservation reservation) {
        this.livre = livre;
        this.reservation = reservation;
    }

    // Getters
    public Livre getLivre() { return livre; }
    public Reservation getReservation() { return reservation; }

    // Setters
    public void setLivre(Livre livre) { this.livre = livre; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

}
