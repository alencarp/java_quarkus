package io.github.pfalencar.quarkussocial2.domain.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_text")
    private String text;
    @Column(name = "data_hora")
    private LocalDateTime dataTime;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    //Esta tabela tem uma chave estrangeira de outra tabela = PK = usuario
    // = Tabela de postagem tem chave estrangeira para a tabela de usuario
    //@ManyToOne indica que eu tenho muitas postagens para 1 usuário
    @PrePersist
    public void prePersist() {
        setDataTime(LocalDateTime.now());
    }
}
