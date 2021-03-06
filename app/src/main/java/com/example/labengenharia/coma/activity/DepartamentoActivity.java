package com.example.labengenharia.coma.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.labengenharia.coma.R;
import com.example.labengenharia.coma.config.ConfiguracaoFirebase;
import com.example.labengenharia.coma.helper.Base64Custom;
import com.example.labengenharia.coma.helper.DateCustom;
import com.example.labengenharia.coma.model.Departamento;
import com.example.labengenharia.coma.model.Movimentacao;
import com.example.labengenharia.coma.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepartamentoActivity extends AppCompatActivity {

    private EditText campoValor;
    private Departamento departamento;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    private DatabaseReference movimentacaoRef;
    private List<String> areas;
    private int posi;
    private List<Departamento> departamentos = new ArrayList<>();

    private String mesAnoSelecionado = PrincipalActivity.mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departamento);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campoValor = findViewById(R.id.editDep);

    }

    public void salvarDerpartamento(View view){

        if ( validarCamposReceita() ){

            departamento = new Departamento();
            String valorRecuperado =campoValor.getText().toString();

            departamento.setDepartemento( valorRecuperado );

            departamento.salvar();

            finish();

        }


    }

    public void spinner(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("Departamento").child(idUsuario);

        movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                areas = new ArrayList<String>();


                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    Departamento dep = areaSnapshot.getValue(Departamento.class);
                    dep.setKey(areaSnapshot.getKey());
                    departamentos.add(dep);
                    areas.add(dep.getDepartemento());
                    //areas.add(areaSnapshot.getValue(String.class));
                }



                Spinner areaSpinner = (Spinner) findViewById(R.id.spinnerOperacaoDep);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(DepartamentoActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);
                areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        posi =  position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void excluirDepartamento(View view){

        if(areas.size() > 0) {

            Departamento removerDepartamento = new Departamento();

            String areaPosicao = areas.get(posi);
            int tamanhoDepartamentos = departamentos.size();

            for (int i = 0; i < tamanhoDepartamentos; i++) {
                String igualDepartamento = departamentos.get(i).getDepartemento();
                if (areaPosicao.equals(igualDepartamento)) {
                    removerDepartamento = departamentos.get(i);
                }
            }

            alterarDepartamento(removerDepartamento);

            System.out.println("derp" + removerDepartamento.getDepartemento() + " kk " + areaPosicao);


            String emailUsuario = autenticacao.getCurrentUser().getEmail();
            String idUsuario = Base64Custom.codificarBase64(emailUsuario);
            movimentacaoRef = firebaseRef.child("Departamento")
                    .child(idUsuario);

            System.out.println("keyy" + removerDepartamento.getKey());

            movimentacaoRef.child(removerDepartamento.getKey()).removeValue();

            finish();
        } else {
            Toast.makeText(DepartamentoActivity.this,
                    "Por favor selecione um departamento",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public Boolean validarCamposReceita(){

        String textoValor = campoValor.getText().toString();

        if ( !textoValor.isEmpty() ){
            return true;
        }else {
            Toast.makeText(DepartamentoActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void alterarDepartamento(final Departamento removerDepartamento){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

         final List<String> keys = new ArrayList<>();

         movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    if(removerDepartamento.getDepartemento().equals(movimentacao.getCategoria())) {
                        keys.add(dados.getKey());
                        System.out.println("dados" + dados.getKey());
                    }

                }
                for(int i = 0; i < keys.size();i++){
                    System.out.println("dentroFor" + keys.get(0));
                    movimentacaoRef.child(keys.get(0)).removeValue();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void editarDepartamentoActivity(View view){

        if(areas.size() > 0) {

            Departamento editarDepartamento = new Departamento();

            String areaPosicao = areas.get(posi);
            int tamanhoDepartamentos = departamentos.size();

            for (int i = 0; i < tamanhoDepartamentos; i++) {
                String igualDepartamento = departamentos.get(i).getDepartemento();
                if (areaPosicao.equals(igualDepartamento)) {
                    editarDepartamento = departamentos.get(i);
                }
            }

            Intent intent = new Intent(this, EditarDepartamentoActivity.class);
            intent.putExtra("departamento", editarDepartamento.getDepartemento());
            intent.putExtra("departamentoKey", editarDepartamento.getKey());
            startActivity(intent);
        } else {
            Toast.makeText(DepartamentoActivity.this,
                    "Por favor selecione um departamento",
                    Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        spinner();
    }
}
