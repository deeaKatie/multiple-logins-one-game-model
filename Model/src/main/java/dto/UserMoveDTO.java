package dto;

import model.Card;
import model.HasId;
import model.User;

public class UserMoveDTO implements HasId<Long> {

    private User player;
    private Card selectedCard;

    public UserMoveDTO() {
    }

    public UserMoveDTO(User player, Card selectedCard) {
        this.player = player;
        this.selectedCard = selectedCard;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
