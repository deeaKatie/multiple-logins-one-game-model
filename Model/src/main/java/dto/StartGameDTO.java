package dto;

import model.HasId;
import model.User;

public class StartGameDTO implements HasId<Long> {

    private User user;
    private String data;

    public StartGameDTO() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
}
