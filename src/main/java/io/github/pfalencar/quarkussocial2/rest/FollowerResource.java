package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Follower;
import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.domain.repository.FollowerRepository;
import io.github.pfalencar.quarkussocial2.domain.repository.UsuarioRepository;
import io.github.pfalencar.quarkussocial2.rest.dto.FollowerRequest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/usuarios/{usuarioId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UsuarioRepository usuarioRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UsuarioRepository usuarioRepository) {
        this.followerRepository = followerRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PUT
    @Transactional
    public Response followUsuario(@PathParam("usuarioId") Long usuarioId, FollowerRequest followerRequest) {
        if (usuarioId.equals(followerRequest.getFollowerId())){
            return Response.status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself")
                    .build();
        }

        //usuario que eu quero seguir
        Usuario usuarioDaURL_queSeraSeguido = usuarioRepository.findById(usuarioId);

        //verifica se existe o usuário com o id que passei na URL
        if (usuarioDaURL_queSeraSeguido != null) {

            //pego o usuário que tem o id que passei na requisição JSON (seguidor)
            Usuario usuarioQueVaiSeguir = usuarioRepository.findById(followerRequest.getFollowerId());

            //verifico se o seguidor já está seguindo aquele usuário
            if (!followerRepository.follows(usuarioQueVaiSeguir, usuarioDaURL_queSeraSeguido)) {
                Follower seguidor = new Follower();
                seguidor.setUsuario(usuarioDaURL_queSeraSeguido);
                seguidor.setFollower(usuarioQueVaiSeguir);

                followerRepository.persist(seguidor);
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();

    }

}
