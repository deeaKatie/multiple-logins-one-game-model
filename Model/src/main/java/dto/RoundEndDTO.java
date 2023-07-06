package dto;

import model.Card;
import model.HasId;

import java.util.ArrayList;

public class RoundEndDTO implements HasId<Long> {

    ArrayList<Card> roundSelectedCards;
    Long roundWinnerId;
    Boolean playerWon; // 0 - lost or 1 - won
    Card playedCard;

    public RoundEndDTO() {
    }

    public RoundEndDTO(ArrayList<Card> roundSelectedCards, Long roundWinnerId) {
        this.roundSelectedCards = roundSelectedCards;
        this.roundWinnerId = roundWinnerId;
    }

    public ArrayList<Card> getRoundSelectedCards() {
        return roundSelectedCards;
    }

    public void setRoundSelectedCards(ArrayList<Card> roundSelectedCards) {
        this.roundSelectedCards = roundSelectedCards;
    }

    public Long getRoundWinnerId() {
        return roundWinnerId;
    }

    public void setRoundWinnerId(Long roundWinnerId) {
        this.roundWinnerId = roundWinnerId;
    }

    public Boolean getPlayerWon() {
        return playerWon;
    }

    public void setPlayerWon(Boolean playerWon) {
        this.playerWon = playerWon;
    }

    public Card getPlayedCard() {
        return playedCard;
    }

    public void setPlayedCard(Card playedCard) {
        this.playedCard = playedCard;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
