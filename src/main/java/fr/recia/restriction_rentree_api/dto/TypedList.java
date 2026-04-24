package fr.recia.restriction_rentree_api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TypedList<T> {

    private String type;
    private List<T> values;

    @JsonCreator
    public TypedList(List<Object> data) {
        this.type = (String) data.get(0);
        this.values = (List<T>) data.get(1);
    }
}
