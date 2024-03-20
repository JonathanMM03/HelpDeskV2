package org.utl.dsm.helpdeskv2.module;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.utl.dsm.helpdeskv2.R;
import org.utl.dsm.helpdeskv2.entity.Categoria;
import org.utl.dsm.helpdeskv2.entity.Empleado;
import org.utl.dsm.helpdeskv2.entity.ServerResponse;
import org.utl.dsm.helpdeskv2.entity.Ticket;
import org.utl.dsm.helpdeskv2.entity.Usuario;
import org.utl.dsm.helpdeskv2.service.CategoriaAPI;
import org.utl.dsm.helpdeskv2.service.TicketAPI;
import org.utl.dsm.helpdeskv2.service.UsuarioAPI;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TicketController extends AppCompatActivity {

    private Spinner slcCategoria, slcUsuario;
    private ImageButton btnSend, btnLista;
    private TextInputEditText txtIdTicket, txtDescripcion, txtDispositivo, txtDate;
    private Retrofit retrofit;
    private TicketAPI ticketAPI;
    private CategoriaAPI categoriaAPI;
    private UsuarioAPI usuarioAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_view);

        slcCategoria = findViewById(R.id.slcCategoria);
        slcUsuario = findViewById(R.id.slcUsuario);
        btnSend = findViewById(R.id.btnSend);
        btnLista = findViewById(R.id.btnTable);
        txtIdTicket = findViewById(R.id.txtIdTicket);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtDispositivo = findViewById(R.id.txtDispositivo);
        txtDate = findViewById(R.id.txtDate);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://d3f7-201-163-190-4.ngrok-free.app/HelpDeskV2/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        categoriaAPI = retrofit.create(CategoriaAPI.class);
        usuarioAPI = retrofit.create(UsuarioAPI.class);
        ticketAPI = retrofit.create(TicketAPI.class);

        // Set onClickListener for txtDate to open dialog
        txtDate.setOnClickListener(v -> openDialog());
        btnSend.setOnClickListener(v -> generarTicket());
        btnLista.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TicketTable.class));
        });

        // Get categories and users
        obtenerCategoriasDesdeURL();
        obtenerUsuariosDesdeURL();
    }

    private void obtenerCategoriasDesdeURL() {
        Call<List<String>> call = categoriaAPI.getAll();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> categorias = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TicketController.this, android.R.layout.simple_spinner_item, categorias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    slcCategoria.setAdapter(adapter);
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TicketController.this, android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    slcUsuario.setAdapter(adapter);
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

    private void generarTicket() {
        // Obtener los valores de los campos de texto y spines
        String descripcion = txtDescripcion.getText().toString().trim();
        String dispositivo = txtDispositivo.getText().toString().trim();
        String fecha = txtDate.getText().toString().trim();

        // Verificar que los campos de texto no estén vacíos
        if (descripcion.isEmpty() || dispositivo.isEmpty() || fecha.isEmpty()) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un objeto Ticket con los datos necesarios
        Ticket t = new Ticket();
        t.setDescripcion(descripcion);
        t.setDispositivo(dispositivo);
        t.setFecha(fecha);

        // Obtener la categoría y el usuario del empleado
        Categoria c = new Categoria();
        c.setCategoria(slcCategoria.getSelectedItem().toString());

        Usuario u = new Usuario();
        u.setUser(slcUsuario.getSelectedItem().toString());

        Empleado e = new Empleado();
        e.setUsuario(u);

        t.setCategoria(c);
        t.setEmpleado(e);

        // Realizar la solicitud para registrar el nuevo ticket
        Call<ServerResponse> call = ticketAPI.generarTicket(t);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    // El ticket se registró correctamente
                    ServerResponse serverResponse = response.body();
                    if (serverResponse != null && serverResponse.getMsg().contains("exitosamente")) {
                        // Mostrar mensaje de éxito
                        Toast.makeText(getApplicationContext(), serverResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Mostrar mensaje de error si no contiene "exitosamente"
                        Toast.makeText(getApplicationContext(), "Error al registrar el ticket", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mostrar mensaje de error si la solicitud no fue exitosa
                    Toast.makeText(getApplicationContext(), "Error al registrar el ticket", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                // Ocurrió un error de red o de otro tipo
                t.printStackTrace();
                // Mostrar mensaje de error
                Toast.makeText(getApplicationContext(), "Error al registrar el ticket", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // month is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String fecha = "%s-%s-%s";
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String strMonth = month > 9 ? String.valueOf(month) : "0" + month;
                String strDay = day > 9 ? String.valueOf(day) : "0" + day;
                String selectedDate = String.format(fecha, year, strMonth, strDay);

                // Check if selected date is greater than today
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, day);
                if (selectedCalendar.after(calendar)) {
                    // Show Google Alert (Toast)
                    Toast.makeText(getApplicationContext(), "Fecha futura seleccionada", Toast.LENGTH_SHORT).show();
                    return; // Exit the listener if future date is selected
                }

                // Set the date if it's not a future date
                txtDate.setText(selectedDate);
            }
        }, year, month, day); // Use current year, month, and day
        dialog.show();
    }
}