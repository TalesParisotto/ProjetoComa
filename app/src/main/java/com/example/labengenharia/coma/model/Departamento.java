package com.example.labengenharia.coma.model;

import com.example.labengenharia.coma.config.ConfiguracaoFirebase;
import com.example.labengenharia.coma.helper.Base64Custom;
import com.example.labengenharia.coma.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Departamento {

    private  String departemento;
    private String key;
    public void salvar(){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64( autenticacao.getCurrentUser().getEmail() );

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("Departamento")
                .child( idUsuario )
                .push()
                .setValue(this);

    }

    public String getDepartemento() {
        return departemento;
    }

    public void setDepartemento(String departemento) {
        this.departemento = departemento;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
