package io.github.pfalencar.quarkussocial2.domain.repository;

import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
}
