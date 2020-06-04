package com.example.labengenharia.coma.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labengenharia.coma.R;
import com.example.labengenharia.coma.adapter.AdapterMovimentacao;
import com.example.labengenharia.coma.config.ConfiguracaoFirebase;
import com.example.labengenharia.coma.helper.Base64Custom;
import com.example.labengenharia.coma.model.Departamento;
import com.example.labengenharia.coma.model.Movimentacao;
import com.example.labengenharia.coma.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialSearchView searchView;

    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUsuario = 0.0;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoRef;
    public static String mesAnoSelecionado;

    private List<String> areas;
    private int posi;

     static String  keyReceita;
     static String mesReceita;

    static String  keyDespesa;
    static String mesDespesa;

    static Movimentacao movi;

    private String[] arrayPesquisa = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        searchView = findViewById(R.id.searchView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Coma");
        setSupportActionBar(toolbar);

        setSearchView();

        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerMovimentos);
        configuraCalendarView();
        swipe();

        //Configurar adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes,this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterMovimentacao );




    }

    public void setSearchView(){

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                arrayPesquisa = query.split(" ");

                recuperarMovimentacoes2(posi);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                recuperarMovimentacoes2(posi);
            }
        });

    }

    public void spinner(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("Departamento").child(idUsuario);
        // movimentacaoRef = firebaseRef.child("Departamento");

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
                    //areas.add(areaSnapshot.getValue(String.class));
                }

                for(String i : areas){
                    System.out.println("aquiii"+ i);
                }

                Spinner areaSpinner = (Spinner) findViewById(R.id.spinner);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(PrincipalActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                 areaSpinner.setAdapter(areasAdapter);
                areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        posi =  position;

                        recuperarMovimentacoes2(position);

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


    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao( viewHolder );
            }

        };

        new ItemTouchHelper( itemTouch ).attachToRecyclerView( recyclerView );

    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Configura AlertDialog
        alertDialog.setTitle("Editar ou excluir");
        alertDialog.setMessage("Você pode escolher entre editar ou excluir este registro");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get( position );

                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64( emailUsuario );
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child( idUsuario )
                        .child( mesAnoSelecionado );

                movimentacaoRef.child( movimentacao.getKey() ).removeValue();
                adapterMovimentacao.notifyItemRemoved( position );
                atualizarSaldo();

            }
        });

        alertDialog.setNeutralButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = viewHolder.getAdapterPosition();
                        movimentacao = movimentacoes.get( position );

                        movi = movimentacao;

                        int podeProseguir = 0;
                        for(String u: areas){
                            if(movi.getCategoria().equals(u)){
                                podeProseguir++;
                            }
                        }

                        if(podeProseguir > 0) {
                            if (movi.getTipo().equals("r")) {
                                adicionarReceita(viewHolder.itemView);
                            } else {
                                adicionarDespesa(viewHolder.itemView);
                            }
                        }else {
                            Toast.makeText(PrincipalActivity.this,
                                    "Não é possivel editar um registro sem departamento cadastrado",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PrincipalActivity.this,
                        "Cancelado",
                        Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();


    }

    public void atualizarSaldo(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        if ( movimentacao.getTipo().equals("r") ){
            receitaTotal = receitaTotal - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }

        if ( movimentacao.getTipo().equals("d") ){
            despesaTotal = despesaTotal - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue( despesaTotal );
        }

    }

    public void recuperarMovimentacoes2(final int position){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();
                double temResutado = 0.5;

                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    movimentacao.setKey( dados.getKey() );

                    String departamento = movimentacao.getCategoria();
                    String[] arrayDescricao = movimentacao.getDescricao().split(" ");


                    if(arrayPesquisa != null){
                        outerloop:
                        for(int i = 0; i < arrayPesquisa.length; i++){
                            for(int j = 0; j < arrayDescricao.length; j++){
                                if(arrayPesquisa[i].equalsIgnoreCase(arrayDescricao[j])){
                                    movimentacoes.add(movimentacao);
                                    temResutado++;
                                    System.out.println("outer"+temResutado);
                                    System.out.println("movimentacoes adiconado "+movimentacoes.get(i).getDescricao());
                                    break outerloop;
                                }
                            }
                        }
                    }else {
                        if (areas.get(position).equals(departamento)) {
                            movimentacoes.add(movimentacao);
                            System.out.println("Departamento: " + movimentacao.getCategoria());
                        }
                    }

                }
                if(arrayPesquisa != null) {
                    if (temResutado == 0.5) {
                        System.out.println("inner" + temResutado);
                        Toast.makeText(PrincipalActivity.this,
                                "Nem um registro encontrado no mês selecionado",
                                Toast.LENGTH_LONG).show();
                    }
                }

                arrayPesquisa = null;
                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarTodasMovimentacoes(View view){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child( idUsuario )
                                     .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    movimentacao.setKey( dados.getKey() );
                    movimentacoes.add( movimentacao );

                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue( Usuario.class );

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format( resumoUsuario );

                textoSaudacao.setText("Olá, " + usuario.getNome() );
                textoSaldo.setText( "R$ " + resultadoFormatado );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem( item );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarDespesa(View view){
        if(movi != null){
                System.out.println("Tipo: " + movi.getTipo());
                Intent intent = new Intent(this, DespesasActivity.class);
                keyDespesa = movi.getKey();
                mesDespesa = mesAnoSelecionado;
                startActivity(intent);
        }else{
            startActivity(new Intent(this, DespesasActivity.class));
        }
    }

    public void adicionarReceita(View view){
        if(movi != null){
                System.out.println("Tipo: " + movi.getTipo());
                Intent intent = new Intent(this, ReceitasActivity.class);
                keyReceita = movi.getKey();
                mesReceita = mesAnoSelecionado;
                startActivity(intent);
        }else {
            startActivity(new Intent(this, ReceitasActivity.class));
        }
    }

    public void adicionarDepartamento(View view){
        startActivity(new Intent(this, DepartamentoActivity.class));
    }

    public void configuraCalendarView(){

        CharSequence meses[] = {"Janeiro","Fevereiro", "Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths( meses );

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1) );
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear() );

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1) );
                mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear() );

                movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );

                    recuperarMovimentacoes2(posi);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarTodasMovimentacoes(textoSaudacao);
        spinner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener( valueEventListenerUsuario );
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
    }
}
