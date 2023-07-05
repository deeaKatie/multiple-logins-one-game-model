package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cards")
public class Card implements HasId<Long> {
    @Id
    private Long id;
    private String value;

    public Card(String value) {
        this.value = value;
    }

    public Card(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
