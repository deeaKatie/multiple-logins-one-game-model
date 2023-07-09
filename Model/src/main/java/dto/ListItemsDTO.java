package dto;

import model.HasId;

import java.util.ArrayList;
import java.util.List;

public class ListItemsDTO implements HasId<Long> {

    private List<ListItemDTO> items;

    public ListItemsDTO() {
        items = new ArrayList<>();
    }

    public ListItemsDTO(List<ListItemDTO> items) {
        this.items = items;
    }

    public void addItem(ListItemDTO item) {
        items.add(item);
    }

    public List<ListItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ListItemDTO> items) {
        this.items = items;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long aLong) {

    }

}
