package dto;

import model.Card;
import model.HasId;

import java.util.ArrayList;

public class RoundEndDTO implements HasId<Long> {

    ArrayList<Card> roundSelectedCards;
    Long roundWinnerId;

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

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }
}
