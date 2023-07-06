package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deck")
public class Deck implements HasId<Long> {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    @OneToMany//(fetch = FetchType.EAGER)
    private List<Card> cards;

    public Deck(List<Card> cards) {
        cards = new ArrayList<>();
        this.cards = cards;
    }
    public Deck() {
        cards = new ArrayList<>();
    }

    public void addCard(Card d) {
        cards.add(d);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + id +
                ", cards=" + cards +
                '}';
    }
}
