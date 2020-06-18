package com.example.labengenharia.coma.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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
import java.util.Calendar;
import java.util.List;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoDescricao;
    private String campoCategoria;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;

    private String key;
    private String mesAnoSelecionado;

    private DatabaseReference movimentacaoRef;
    private List<String> areas;

    private String tipoDepartamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        String diaAtual = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        String mesAtual = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        if (Integer.parseInt(mesAtual) <= 9 ){
            mesAtual = "0"+mesAtual;
        }
        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        //campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Preenche o campo data com a date atual
        campoData.setText( diaAtual+"/"+mesAtual );
        recuperarDespesaTotal();

        if( PrincipalActivity.movi != null){
            key = PrincipalActivity.keyDespesa;
            mesAnoSelecionado = PrincipalActivity.mesDespesa;
            System.out.println("key: " + key);
            System.out.println("mesAnoSelecionado: " + mesAnoSelecionado);
            System.out.println("movi: " + PrincipalActivity.movi.toString());

            campoValor.setText(String.valueOf(PrincipalActivity.movi.getValor()));
            campoData.setText(PrincipalActivity.movi.getData());
           // campoCategoria.setText(PrincipalActivity.movi.getCategoria());
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


                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    Departamento dep = areaSnapshot.getValue(Departamento.class);
                    areas.add(dep.getDepartemento());
                    // areas.add(areaSnapshot.getValue(String.class));
                }
                campoCategoria = PrincipalActivity.movi.getCategoria();

                int posicaoInicialSpinner = 0;
                for(int i = 0; i <= areas.size(); i++){
                    if(campoCategoria.equals(areas.get(i))){
                        posicaoInicialSpinner = i;
                        break;
                    };
                }

                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner3);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(DespesasActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);
                areaSpinner.setSelection(posicaoInicialSpinner);
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

                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner3);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(DespesasActivity.this, android.R.layout.simple_spinner_item, areas);
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

    public void editarDespesa(){

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
        movimentacao.setTipo( "d" );

        System.out.println("movimentacao: " + movimentacao.toString());
        System.out.println("key do metodo editardepesa: " + key);

        movimentacaoRef.child( key ).setValue(movimentacao);

        PrincipalActivity.movi = null;

    }

    public void salvarDespesa(View view){

        if(areas.size() > 0) {
                if (key != null) {
                    if (validarCamposDespesa()) {
                        editarDespesa();
                        finish();
                    }
                } else {
                    if (validarCamposDespesa()) {

                        System.out.println("chegouuuuu aquiii");
                        String anoAtual = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        movimentacao = new Movimentacao();
                        String data = campoData.getText().toString();
                        Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                        movimentacao.setValor(valorRecuperado);
                        movimentacao.setCategoria(tipoDepartamento);
                        movimentacao.setDescricao(campoDescricao.getText().toString());
                        System.out.println(data+"/"+anoAtual);
                        movimentacao.setData(data+"/"+anoAtual);
                        movimentacao.setTipo("d");

                        Double despesaAtualizada = despesaTotal + valorRecuperado;
                        atualizarDespesa(despesaAtualizada);

                        movimentacao.salvar(data+"/"+anoAtual);

                        finish();

                    }
                }
            } else {
            Toast.makeText(DespesasActivity.this,
                    "Por favor selecione um departamento",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean validarCamposDespesa(){

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        //String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        System.out.println(textoData.length());
        if ( !textoValor.isEmpty() ){
            if ( !textoData.isEmpty() ){
               // if ( !textoCategoria.isEmpty() ){
                    if(textoData.length() <= 4) {
                        Toast.makeText(DespesasActivity.this,
                                "Inserir dia e mes no formato: 04/05",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }else if (textoData.length() > 5) {
                        Toast.makeText(DespesasActivity.this,
                                "Inserir dia e mes no formato: 04/05",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if ( !textoDescricao.isEmpty() ){
                        return true;
                    }else {
                        Toast.makeText(DespesasActivity.this,
                                "Descrição não foi preenchida!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
               /* }else {
                    Toast.makeText(DespesasActivity.this,
                            "Categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }*/
            }else {
                Toast.makeText(DespesasActivity.this,
                        "Data não foi preenchida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(DespesasActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void atualizarDespesa(Double despesa){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        usuarioRef.child("despesaTotal").setValue(despesa);

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
