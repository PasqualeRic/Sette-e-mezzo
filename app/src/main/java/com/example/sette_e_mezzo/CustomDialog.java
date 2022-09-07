package com.example.sette_e_mezzo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
/*
        Dialog per chiedere ai client se vogliono continuare a giocare
 */
public class CustomDialog extends Dialog {

    TextView tvResultDialog, tvP1, tvP2, tvP3, tvP4;
    String result;
    String p1, p2, p3, p4;
    Button si, no;

    public CustomDialog(@NonNull Context context,String result,String p1,String p2,String p3,String p4) {
        super(context);
        this.result=result;
        this.p1=p1;
        this.p2=p2;
        this.p3=p3;
        this.p4=p4;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog);
        si = findViewById(R.id.btnSi);
        no = findViewById(R.id.btnNo);
        tvResultDialog = findViewById(R.id.tvResultDialog);
        tvP1 = findViewById(R.id.tvP1);
        tvP2 = findViewById(R.id.tvP2);
        tvP3 = findViewById(R.id.tvP3);
        tvP4 = findViewById(R.id.tvP4);

        tvResultDialog.setText(result);
        tvP1.setText(p1);
        tvP2.setText(p2);
        tvP3.setText(p3);
        tvP4.setText(p4);
    }

    public Button getBtnYes(){ return si;}
    public Button getBtnNo(){ return no;}

}
