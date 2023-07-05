package dto;

import model.Deck;
import model.HasId;
import model.User;

import java.util.ArrayList;

public class PlayersDTO implements HasId<Long> {

    ArrayList<User> players;
    Deck deck;


    public PlayersDTO() {
        players = new ArrayList<>();
    }

    public void addPlayer(User u) {
        players.add(u);
    }

    public ArrayList<User> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<User> players) {
        this.players = players;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }
}
