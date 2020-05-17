package com.example.labengenharia.coma.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.labengenharia.coma.R;
import com.example.labengenharia.coma.config.ConfiguracaoFirebase;
import com.example.labengenharia.coma.helper.Base64Custom;
import com.example.labengenharia.coma.model.Departamento;
import com.example.labengenharia.coma.model.Movimentacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditarDepartamentoActivity extends AppCompatActivity {

    private EditText editCapompoDep;
    private String departamentoKey;

    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference movimentacaoRef;
    private ValueEventListener valueEventListenerMovimentacoes;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private  String departamento = "";

    String mesAnoSelecionado = PrincipalActivity.mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_departamento);

        editCapompoDep = findViewById(R.id.editCapompoDep);


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
        movimentacaoRef.setValue(departamento);

        alterarDepartamento();

        finish();

    }

    public void alterarDepartamento(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();

                List<String> keys = new ArrayList<>();
                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    if(departamento.equals(movimentacao.getCategoria())) {
                        keys.add(dados.getKey());
                        movimentacao.setCategoria(editCapompoDep.getText().toString());
                        movimentacoes.add(movimentacao);
                    }

                }
                for(int i = 0; i < keys.size();i++){
                    movimentacaoRef.child(keys.get(i)).setValue(movimentacoes.get(i));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
