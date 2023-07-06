package dto;

import model.Deck;
import model.HasId;
import model.User;

public class WinnerDTO implements HasId<Long> {

    User winner;
    Deck winnerDeck;

    public WinnerDTO() {
        winner = new User();
        winnerDeck = new Deck();
    }

    public WinnerDTO(User winner, Deck winnerDeck) {
        this.winner = winner;
        this.winnerDeck = winnerDeck;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public Deck getWinnerDeck() {
        return winnerDeck;
    }

    public void setWinnerDeck(Deck winnerDeck) {
        this.winnerDeck = winnerDeck;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
