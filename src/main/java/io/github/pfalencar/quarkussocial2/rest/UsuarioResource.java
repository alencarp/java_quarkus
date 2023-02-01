package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.domain.repository.UsuarioRepository;
import io.github.pfalencar.quarkussocial2.rest.dto.CreateUsuarioRequest;
import io.github.pfalencar.quarkussocial2.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    private UsuarioRepository usuarioRepository;
    private Validator validator;

    @Inject
    public UsuarioResource(UsuarioRepository usuarioRepository, Validator validator){
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
    }
    @POST
    @Transactional
    public Response createUsuario(CreateUsuarioRequest usuarioRequest) {

        Set<ConstraintViolation<CreateUsuarioRequest>> violations = validator.validate(usuarioRequest);
        if (!violations.isEmpty()) {
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        Usuario usuario = new Usuario();
        usuario.setName(usuarioRequest.getName());
        usuario.setAge(usuarioRequest.getAge());

        usuarioRepository.persist(usuario);

        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(usuario)
                .build();
    }
    @GET
    public Response listAllUsuarios() {
        PanacheQuery<Usuario> query = usuarioRepository.findAll();
        return Response.ok(query.list()).build();
    }
    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUsuario(@PathParam("id") Long id){
        Usuario usuarioEncontrado = usuarioRepository.findById(id);

        if (usuarioEncontrado != null) {
            usuarioRepository.delete(usuarioEncontrado);
            return Response.noContent().build(); //204
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUsuario(@PathParam("id") Long id, CreateUsuarioRequest usuarioNovo) {
        Usuario usuarioEncontrado = usuarioRepository.findById(id);

        if (usuarioEncontrado != null) {
            usuarioEncontrado.setName(usuarioNovo.getName());
            usuarioEncontrado.setAge(usuarioNovo.getAge());
//            usuarioRepository.persist(usuarioEncontrado);//atualiza automaticamente, por causa do transactional
            return Response.noContent().build(); //204
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
