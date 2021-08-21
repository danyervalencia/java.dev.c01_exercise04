package com.java.dev01.exercise04.pojos;

import java.io.Serializable;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class Persona implements Serializable {
    private int id;
    private String paternal;
    private String maternal;
    private String name;
}