package com.example.labengenharia.coma.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.labengenharia.coma.R;
import com.example.labengenharia.coma.config.ConfiguracaoFirebase;
import com.example.labengenharia.coma.helper.Base64Custom;
import com.example.labengenharia.coma.model.Departamento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class EditarDepartamentoActivity extends AppCompatActivity {

    private EditText editCapompoDep;
    private String departamentoKey;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference movimentacaoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_departamento);

        editCapompoDep = findViewById(R.id.editCapompoDep);

        String departamento = "";
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                finish();
            } else {
                departamento = extras.getString("departamento");
            }

        editCapompoDep.setText(departamento);
        departamentoKey = extras.getString("departamentoKey");
    }

    public void salvarEdicaoDepartamento(View view){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("Departamento").child(idUsuario).child(departamentoKey);
        Departamento departamento = new Departamento();
        departamento.setDepartemento(editCapompoDep.getText().toString());
        departamento.setKey(departamentoKey);
        movimentacaoRef.setValue(departamento);

        finish();

    }
}
