package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Follower;
import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.domain.repository.FollowerRepository;
import io.github.pfalencar.quarkussocial2.domain.repository.UsuarioRepository;
import io.github.pfalencar.quarkussocial2.rest.dto.request.FollowerRequest;
import io.github.pfalencar.quarkussocial2.rest.dto.response.FollowerResponse;
import io.github.pfalencar.quarkussocial2.rest.dto.response.FollowersPerUsuarioResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFollowers(@PathParam("usuarioId") Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Follower> listaDeFollowersPorUsuario = followerRepository.findByUsuario(usuarioId);

        var followersPerUsuarioResponse = new FollowersPerUsuarioResponse();

        followersPerUsuarioResponse.setFollowersCount(listaDeFollowersPorUsuario.size());

        //o map() vai mapear um objeto desta listaDeFollowersPorUsuario do tipo Follower para um FollowerResponse
        List<FollowerResponse> followerList = listaDeFollowersPorUsuario.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        //mapear a lista de follower para uma lista de FollowerResponse
        followersPerUsuarioResponse.setContent(followerList);
        return Response.ok(followersPerUsuarioResponse).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUsuario(@PathParam("usuarioId") Long usuarioId, @QueryParam("followerId") Long followerId) {
        //verificar se esse usuario existe:
        Usuario usuarioById = usuarioRepository.findById(usuarioId);
        if (usuarioById == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        followerRepository.deleteByFollowerAndUsuario(followerId, usuarioId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
