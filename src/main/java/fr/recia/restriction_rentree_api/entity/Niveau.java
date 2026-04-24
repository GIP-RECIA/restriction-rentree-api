package fr.recia.restriction_rentree_api.entity;

import javax.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"etablissement_id", "nom"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Niveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TIMESTAMP")
    private ZonedDateTime dateRentree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etablissement_id", nullable = false)
    private Etablissement etablissement;
}