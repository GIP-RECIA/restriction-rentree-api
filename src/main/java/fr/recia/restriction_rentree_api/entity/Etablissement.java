package fr.recia.restriction_rentree_api.entity;

import javax.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Etablissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uai;

    @Column
    private boolean enabled;

    @Column(columnDefinition = "TIMESTAMP")
    private ZonedDateTime dateRentree;
}
