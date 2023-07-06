package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "game")
public class Game implements HasId<Long> {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    @OneToMany//(fetch = FetchType.EAGER)
    @JoinTable(name = "players",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    @MapKeyJoinColumn(name = "user_deck1")
    private Map<User, Deck> players;
    @OneToMany
    @JoinTable(name = "winners",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    @MapKeyJoinColumn(name = "user_deck2")
    private Map<model.User, Deck> winners;

    public Game() {
        players = new HashMap<>();
        winners = new HashMap<>();
    }

    public void addPlayer(model.User player, Deck deck){
        players.put(player, deck);
    }

    public void addWinner(User player, Deck deck){
        winners.put(player, deck);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<model.User, Deck> getPlayers() {
        return players;
    }

    public void setPlayers(Map<User, Deck> players) {
        this.players = players;
    }

    public Map<User, Deck> getWinners() {
        return winners;
    }

    public void setWinners(Map<User, Deck> winners) {
        this.winners = winners;
    }

    @Override
    public String toString() {
        String out = "";
        for (var el : players.entrySet()) {
            out += "k: " + el.getKey() + " v:" + el.getValue() + "; ";
        }
        return "Game{" +
                "id=" + id +
                ", players=" + out +
                ", winners=" + winners +
                '}';
    }
}
