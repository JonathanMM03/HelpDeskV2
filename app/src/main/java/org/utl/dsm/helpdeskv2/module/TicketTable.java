package org.utl.dsm.helpdeskv2.module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import org.utl.dsm.helpdeskv2.R;
import org.utl.dsm.helpdeskv2.entity.Ticket;
import org.utl.dsm.helpdeskv2.service.CategoriaAPI;
import org.utl.dsm.helpdeskv2.service.TicketAPI;
import org.utl.dsm.helpdeskv2.service.UsuarioAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketTable extends AppCompatActivity {
    private TableLayout tbTickets;
    private Retrofit retrofit;
    private TicketAPI ticketAPI;
    private CategoriaAPI categoriaAPI;
    private UsuarioAPI usuarioAPI;
    private Spinner slcCategoria, slcUsuario;
    private String categoriaSeleccionada, usuarioSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tickets);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://d3f7-201-163-190-4.ngrok-free.app/HelpDeskV2/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        categoriaAPI = retrofit.create(CategoriaAPI.class);
        usuarioAPI = retrofit.create(UsuarioAPI.class);
        ticketAPI = retrofit.create(TicketAPI.class);

        tbTickets = findViewById(R.id.tbTickets);
        slcCategoria = findViewById(R.id.slcCategoriaTable);
        slcUsuario = findViewById(R.id.slcUsuarioTable);

        // Obtener las categorías y usuarios desde el servidor
        obtenerCategoriasDesdeURL();
        obtenerUsuariosDesdeURL();

        // Establecer listeners para los spinners
        slcCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = parent.getItemAtPosition(position).toString();
                showTickets();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó ninguna categoría
            }
        });

        slcUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usuarioSeleccionado = parent.getItemAtPosition(position).toString();
                showTickets();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó ningún usuario
            }
        });
    }

    private void showTickets() {
        String categoriaSeleccionada = slcCategoria.getSelectedItem().toString();
        String usuarioSeleccionado = slcUsuario.getSelectedItem().toString();

        Call<List<Ticket>> ticketsCall = ticketAPI.getAll();
        ticketsCall.enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful()) {
                    List<Ticket> tickets = response.body();
                    tbTickets.removeAllViews(); // Limpiar la tabla antes de agregar nuevas filas
                    if (categoriaSeleccionada.equals("All") && usuarioSeleccionado.equals("All")) {
                        // Mostrar todos los tickets sin filtrar
                        for (Ticket ticket : tickets) {
                            addTableRow(ticket);
                        }
                    } else {
                        // Filtrar y mostrar los tickets según la categoría y el usuario seleccionados
                        List<Ticket> filteredTickets = filterTickets(tickets, categoriaSeleccionada, usuarioSeleccionado);
                        for (Ticket ticket : filteredTickets) {
                            addTableRow(ticket);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Ticket>> call, Throwable t) {
                // Manejar error
            }
        });
    }

    private List<Ticket> filterTickets(List<Ticket> tickets, String categoria, String usuario) {
        List<Ticket> filteredList = new ArrayList<>();
        for (Ticket ticket : tickets) {
            // Verificar si la categoría es "All" o si coincide con la categoría del ticket
            boolean categoriaMatch = categoria.equals("All") || ticket.getCategoria().getCategoria().equals(categoria);
            // Verificar si el usuario es "All" o si coincide con el usuario del ticket
            boolean usuarioMatch = usuario.equals("All") || ticket.getEmpleado().getNombre().equals(usuario);
            // Agregar el ticket si tanto la categoría como el usuario coinciden
            if (categoriaMatch && usuarioMatch) {
                filteredList.add(ticket);
            }
        }
        return filteredList;
    }

    private void obtenerCategoriasDesdeURL() {
        Call<List<String>> call = categoriaAPI.getAll();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> categorias = response.body();
                    // Agregar opción "All" al principio de la lista de categorías
                    categorias.add(0, "All");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TicketTable.this, android.R.layout.simple_spinner_item, categorias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    slcCategoria.setAdapter(adapter);
                    // Seleccionar "All" por defecto
                    slcCategoria.setSelection(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void obtenerUsuariosDesdeURL() {
        Call<List<String>> call = usuarioAPI.getAll();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> usuarios = response.body();
                    // Agregar opción "All" al principio de la lista de usuarios
                    usuarios.add(0, "All");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TicketTable.this, android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    slcUsuario.setAdapter(adapter);
                    // Seleccionar "All" por defecto
                    slcUsuario.setSelection(0);
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void addTableRow(Ticket ticket) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        int paddingPx = 8; // Definir el tamaño del padding en píxeles

        // Array de textos
        String[] textos = {
                String.valueOf(ticket.getIdTicket()),
                ticket.getEmpleado().getNombre(),
                ticket.getCategoria().getCategoria(),
                ticket.getDescripcion(),
                ticket.getDispositivo(),
                ticket.getFecha(),
                String.valueOf(ticket.getEstatus()),
                ticket.getFechaAtencion()
        };

        for (String texto : textos) {
            TextView textView = new TextView(this);
            textView.setText(texto);
            textView.setBackgroundColor(Color.WHITE); // Fondo blanco para simular las celdas
            textView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx); // Establecer el padding
            textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            // Añadir borde
            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.TRANSPARENT); // Color de fondo transparente
            border.setStroke(2, Color.BLACK); // Ancho y color del borde
            textView.setBackground(border);

            row.addView(textView);

            TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
            params.setMargins(0, 0, 5, 0); // Agregar márgenes entre columnas
            textView.setLayoutParams(params);
        }

        // Agregar botón "Atender"
        Button btnAtender = new Button(this);
        btnAtender.setText("Atender");
        btnAtender.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.addView(btnAtender);

        // Agregar evento de clic al botón "Atender"
        btnAtender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción al hacer clic en el botón "Atender"
                Toast.makeText(TicketTable.this, "Botón 'Atender' presionado", Toast.LENGTH_SHORT).show();
            }
        });

        // Agregar evento de clic a la fila
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una alerta al hacer clic en la fila
                AlertDialog.Builder builder = new AlertDialog.Builder(TicketTable.this);
                builder.setTitle("Detalles del Ticket")
                        .setMessage("ID del Ticket: " + ticket.getIdTicket() + "\n"
                                + "Empleado: " + ticket.getEmpleado().getNombre() + "\n"
                                + "Categoría: " + ticket.getCategoria().getCategoria() + "\n"
                                + "Descripción: " + ticket.getDescripcion() + "\n"
                                + "Dispositivo: " + ticket.getDispositivo() + "\n"
                                + "Fecha: " + ticket.getFecha() + "\n"
                                + "Estatus: " + ticket.getEstatus() + "\n"
                                + "Fecha de Atención: " + ticket.getFechaAtencion())
                        /*.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción al hacer clic en el botón "Aceptar"
                                dialog.dismiss(); // Cerrar la alerta
                            }
                        })*/.setPositiveButton("Atender", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        tbTickets.addView(row);
    }
}
