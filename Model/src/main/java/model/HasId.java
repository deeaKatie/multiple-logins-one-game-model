package model;

import java.io.Serializable;

public interface HasId<ID> extends Serializable {
    ID getId();
    void setId(ID id);
}
