package io.github.pfalencar.quarkussocial2.domain.repository;

import io.github.pfalencar.quarkussocial2.domain.model.Follower;
import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    //verifica se o dado usuario já é seguidor de outro usuario
    //follower = usuarioQueVaiSeguir               usuario = usuarioDaURL_queSeraSeguido
    public boolean follows(Usuario follower, Usuario usuario) {
        //quero a entidade Follower que tem esse registro

        //maneira 1 de pedir para o BD um registro com um usuario_id e follower_id específicos:
//        Map<String, Object> params = new HashMap<>();
//        params.put("follower", usuarioQueVaiSeguir);
//        params.put("usuario", usuarioDaURL_queSeraSeguido);
//        find("follower =:usuarioQueVaiSeguir and usuario =: usuarioDaURL_queSeraSeguido", params);

        //maneira 2 de pedir para o BD um registro com um usuario_id e follower_id específicos:
        var params = Parameters.with("follower", follower)
                .and("usuario", usuario)
                .map(); //transforma esses parâmetros no mesmo Map<String, Object> params da maneira 1 de fazer
        PanacheQuery<Follower> query =
                find("follower =:follower and usuario =:usuario", params);

        //Procurando se encontrou algum registro:
        //Maneira 1: procuro pelo primeiro resultado que aparecer
        //Follower result = query.firstResult();

        //Maneira 2: pode ser que tenha encontrado ou não
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long usuarioId) {
        find("usuario.id", usuarioId);
        return null;
    }


}
