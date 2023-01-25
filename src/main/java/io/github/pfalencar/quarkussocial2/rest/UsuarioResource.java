package io.github.pfalencar.quarkussocial2.rest;

import io.github.pfalencar.quarkussocial2.rest.dto.CreateUsuarioRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @POST
    public Response createUsuario(CreateUsuarioRequest usuarioRequest) {
        return Response.ok(usuarioRequest).build();
    }
    @GET
    public Response listAllUsuarios() {
        return Response.ok().build();
    }

}
