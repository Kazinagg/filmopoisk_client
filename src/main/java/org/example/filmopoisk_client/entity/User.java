package org.example.filmopoisk_client.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private String username;

    private String email;

    private String password;

    @ElementCollection
    @CollectionTable(name = "marked_films", joinColumns = @JoinColumn(name = "user_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "kinopoiskId", column = @Column(name = "kinopoisk_id")),
            @AttributeOverride(name = "typeMarked", column = @Column(name = "type_marked"))
    })
    private List<MarkedFilm> marked;

    @Data
    @Embeddable
    public static class MarkedFilm {
        private Integer kinopoiskId;
        private Integer typeMarked;
    }
}
