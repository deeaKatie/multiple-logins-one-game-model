package dto;

import model.HasId;
import model.User;

public class ListItemDTO implements HasId<Long> {

    private User user;

    public ListItemDTO() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }

    @Override
    public String toString() {
        return "user= " + user;
    }
}
