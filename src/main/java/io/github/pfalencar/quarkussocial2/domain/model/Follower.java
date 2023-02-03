package io.github.pfalencar.quarkussocial2.domain.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="followers")
@Data
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="usuario_id")
    private Usuario user;

    @ManyToOne
    @JoinColumn(name="follower_id")
    private Usuario follower;

}
