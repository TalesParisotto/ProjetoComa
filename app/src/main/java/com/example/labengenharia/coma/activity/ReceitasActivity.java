package com.example.labengenharia.coma.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoDescricao;
    private String campoCategoria;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    private String key;
    private String mesAnoSelecionado;

    private DatabaseReference movimentacaoRef;
    private List<String> areas;

    private String tipoDepartamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        //campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Preenche o campo data com a date atual
        campoData.setText( DateCustom.dataAtual() );
        recuperarReceitaTotal();



        if( PrincipalActivity.movi != null){
            key = PrincipalActivity.keyReceita;
            mesAnoSelecionado = PrincipalActivity.mesReceita;
            System.out.println("key: " + key);
            System.out.println("mesAnoSelecionado: " + mesAnoSelecionado);
            System.out.println("movi: " + PrincipalActivity.movi.toString());

            campoValor.setText(String.valueOf(PrincipalActivity.movi.getValor()));
            campoData.setText(PrincipalActivity.movi.getData());
            //campoCategoria.setText(PrincipalActivity.movi.getCategoria());
            campoDescricao.setText(PrincipalActivity.movi.getDescricao());

        }


    }

    public void spinnerEdicao(){

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

                int tem = 0;
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    Departamento dep = areaSnapshot.getValue(Departamento.class);
                    areas.add(dep.getDepartemento());
                   // areas.add(areaSnapshot.getValue(String.class));
                    if(dep.getDepartemento().equals(PrincipalActivity.movi.getCategoria())){
                        tem++;
                    }
                }
                campoCategoria = PrincipalActivity.movi.getCategoria();

                int posicaoInicialSpinner = 0;
                int entrou = 0;
                if(areas.size() > 0) {
                    for (int i = 0; i < areas.size(); i++) {
                        if (campoCategoria.equals(areas.get(i))) {
                            posicaoInicialSpinner = i;
                            entrou = 1;
                            break;
                        }
                        ;
                    }
                }

                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner2);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(ReceitasActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);
                if(entrou > 0) {
                    if(tem > 0){
                        areaSpinner.setSelection(posicaoInicialSpinner);
                    }
                }
                areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        tipoDepartamento = areas.get(position);
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
                    areas.add(dep.getDepartemento());

                    // areas.add(areaSnapshot.getValue(String.class));
                }

                for(String i : areas){
                    System.out.println("aquiii"+ i);
                }

                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner2);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(ReceitasActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);
                areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(areas.size() > 0) {
                            tipoDepartamento = areas.get(position);
                        }
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

    public void editarReceita(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference movimentacaoRef;
        movimentacaoRef = firebaseRef.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

        movimentacao = new Movimentacao();
        String data = campoData.getText().toString();
        Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

        movimentacao.setValor( valorRecuperado );
        movimentacao.setCategoria( tipoDepartamento );
        movimentacao.setDescricao( campoDescricao.getText().toString() );
        movimentacao.setData( data );
        movimentacao.setTipo( "r" );

        System.out.println("movimentacao: " + movimentacao.toString());
        System.out.println("key: " + key);

        movimentacaoRef.child( key ).setValue(movimentacao);

        PrincipalActivity.movi = null;


    }

    public void salvarReceita(View view){

        if(areas.size() > 0) {

            if (key != null) {
                if (validarCamposReceita()) {
                    System.out.println("entro no editarReceita");
                    editarReceita();
                    finish();
                }
            } else {

                if (validarCamposReceita()) {
                    System.out.println("salvarReceita");
                    movimentacao = new Movimentacao();
                    String data = campoData.getText().toString();
                    Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

                    movimentacao.setValor(valorRecuperado);
                    movimentacao.setCategoria(tipoDepartamento);
                    movimentacao.setDescricao(campoDescricao.getText().toString());
                    movimentacao.setData(data);
                    movimentacao.setTipo("r");

                    Double receitaAtualizada = receitaTotal + valorRecuperado;
                    atualizarReceita(receitaAtualizada);

                    movimentacao.salvar(data);

                    finish();

                }
            }
        } else{
            Toast.makeText(ReceitasActivity.this,
                    "Por favor selecione um departamento",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public Boolean validarCamposReceita(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        //String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
                //if ( !textoCategoria.isEmpty() ){
                    if ( !textoDescricao.isEmpty() ){
                        return true;
                    }else {
                        Toast.makeText(ReceitasActivity.this,
                                "Descrição não foi preenchida!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                /*}else {
                    Toast.makeText(ReceitasActivity.this,
                            "Categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }*/
            }else {
                Toast.makeText(ReceitasActivity.this,
                        "Data não foi preenchida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(ReceitasActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void recuperarReceitaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void atualizarReceita(Double receita){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.child("receitaTotal").setValue(receita);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if( PrincipalActivity.movi != null) {
            spinnerEdicao();
        }else {
            spinner();
        }
    }

    @Override
    public void finish() {
        PrincipalActivity.keyReceita = null;
        PrincipalActivity.mesReceita =  null;
        PrincipalActivity.movi = null;

        System.out.println("PrincipalActivity.movi pra nullar: " + PrincipalActivity.movi);
        super.finish();
    }
}
