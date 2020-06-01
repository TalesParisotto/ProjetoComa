package com.example.labengenharia.coma.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.labengenharia.coma.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RedefinirSenhaActivity extends AppCompatActivity {

    private EditText editEmailRedefinir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinir_senha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editEmailRedefinir = findViewById(R.id.editEmailRedefinir);
    }

    public void enviarEmailRedefinirSenha(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(editEmailRedefinir.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RedefinirSenhaActivity.this,
                                    "Email emviado para redefinição de senha.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RedefinirSenhaActivity.this,
                                    "Erro ao enviar email.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
