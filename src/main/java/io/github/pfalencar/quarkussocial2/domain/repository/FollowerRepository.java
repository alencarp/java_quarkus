package io.github.pfalencar.quarkussocial2.domain.repository;

import io.github.pfalencar.quarkussocial2.domain.model.Follower;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {
}
