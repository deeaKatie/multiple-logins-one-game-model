package dto;

import model.HasId;

public class UpdateDTO implements HasId<Long> {

    private ListItemsDTO entities;

    public UpdateDTO() {
    }

    public ListItemsDTO getEntities() {
        return entities;
    }

    public void setEntities(ListItemsDTO entities) {
        this.entities = entities;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }


}
