package org.utl.dsm.helpdeskv2.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UsuarioAPI {
    @GET("usuario/all")
    Call<List<String>> getAll();
}
