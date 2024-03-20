package org.utl.dsm.helpdeskv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.utl.dsm.helpdeskv2.module.TicketController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadTicketModule();
    }

    public void loadTicketModule(){
        startActivity(new Intent(getApplicationContext(), TicketController.class));
    }
}