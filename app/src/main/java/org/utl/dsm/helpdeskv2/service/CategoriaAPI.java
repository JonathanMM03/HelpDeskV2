package org.utl.dsm.helpdeskv2.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

public interface CategoriaAPI {
    @GET("categoria/all")
    Call<List<String>> getAll();
}
