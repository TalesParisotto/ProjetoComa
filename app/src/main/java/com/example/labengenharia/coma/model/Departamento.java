package com.example.labengenharia.coma.model;

import com.example.labengenharia.coma.config.ConfiguracaoFirebase;
import com.example.labengenharia.coma.helper.Base64Custom;
import com.example.labengenharia.coma.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Departamento {

    private  String departemento;

    public void salvar(String dataEscolhida){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64( autenticacao.getCurrentUser().getEmail() );
        String mesAno = DateCustom.mesAnoDataEscolhida( dataEscolhida );

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("departemento")
                .child( idUsuario )
                .child( mesAno )
                .push()
                .setValue(this);

    }

    public String getDepartemento() {
        return departemento;
    }

    public void setDepartemento(String departemento) {
        this.departemento = departemento;
    }
}
