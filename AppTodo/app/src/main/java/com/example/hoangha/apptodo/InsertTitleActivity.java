package com.example.hoangha.apptodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InsertTitleActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnTitle;
    EditText edtTitle, edtDetail;
    String title, detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_title);
        Bundle bnd = getIntent().getExtras();
        if (bnd == null)
            return;

        edtTitle = (EditText)findViewById(R.id.edtTitle);
        edtDetail = (EditText)findViewById(R.id.edtDetail);

        btnTitle = (Button)findViewById(R.id.btnSave);
        btnTitle.setOnClickListener(this);

        title = bnd.getString("TitleName");
        detail = bnd.getString("Detail");
        edtTitle.setText(title);
        edtDetail.setText(detail);
    }

    @Override
    public void onClick(View view) {
        Intent dta = new Intent();
        dta.putExtra("OldTitleR",title);
        dta.putExtra("TitleNameR",edtTitle.getText().toString());
        dta.putExtra("DetailR",edtDetail.getText().toString());
        setResult(RESULT_OK,dta);

        finish();
    }
}
