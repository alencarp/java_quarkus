package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.domain.model.Usuario;
import io.github.pfalencar.quarkussocial2.rest.dto.CreateUsuarioRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @POST
    @Transactional
    public Response createUsuario(CreateUsuarioRequest usuarioRequest) {

        Usuario usuario = new Usuario();
        usuario.setName(usuarioRequest.getName());
        usuario.setAge(usuarioRequest.getAge());

        usuario.persist();

        return Response.ok(usuario).build();
    }
    @GET
    public Response listAllUsuarios() {
        PanacheQuery<Usuario> query = Usuario.findAll();
        return Response.ok(query.list()).build();
    }
    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUsuario(@PathParam("id") Long id){
        Usuario usuarioEncontrado = Usuario.findById(id);

        if (usuarioEncontrado != null) {
            usuarioEncontrado.delete();
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }
    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUsuario(@PathParam("id") Long id, CreateUsuarioRequest usuarioRequest) {

        return Response.ok().build();
    }

}
