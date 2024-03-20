package org.utl.dsm.helpdeskv2.service;

import org.utl.dsm.helpdeskv2.entity.ServerResponse;
import org.utl.dsm.helpdeskv2.entity.Ticket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TicketAPI {
    @GET("ticket/all")
    Call<List<Ticket>> getAll();

    @POST("ticket/registrar")
    Call<ServerResponse> generarTicket(@Body Ticket t);
}
