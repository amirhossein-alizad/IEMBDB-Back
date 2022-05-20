package com.iemdb.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Actor {
    @Id
    private int id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd,yyyy")
    private Date birthDate;

    private String nationality;
    private String image;

    public Actor(int id, String name, Date birthDate, String nationality, String image) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.image = image;
    }

    public boolean checkNull() throws IllegalAccessException {
        for (Field f : getClass().getDeclaredFields())
            if (f.get(this) == null)
                return true;
        return false;
    }

    public ObjectNode serialize() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode serialized_actor = mapper.createObjectNode();
        serialized_actor.put("actorId", getId());
        serialized_actor.put("name", getName());
        return serialized_actor;
    }
}
