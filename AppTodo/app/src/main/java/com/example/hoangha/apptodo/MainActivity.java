package com.example.hoangha.apptodo;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    public SQLiteDatabase myDB=null;
    static final String DATABASE_NAME = "MyFirstDB.db";
    static final String TABLE_NAME = "MyFirstTable";
    ListView lstNote;
    Button btnAdd;
    EditText edtAdd;
    public ArrayList<String> lstName;
    public ArrayAdapter<String> adp;
    boolean afterdel= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //database
        createDB();
        createTable();

        edtAdd = (EditText)findViewById(R.id.edtAdd);

        lstNote = (ListView)findViewById(R.id.lstNote);
        lstName = new ArrayList<>();

        loadAllTitle();

        adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, lstName);

        lstNote.setAdapter(adp);
        lstNote.setOnItemClickListener(this);
        lstNote.setOnItemLongClickListener(this);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String title;
        title = edtAdd.getText().toString();
        if(title.equalsIgnoreCase("")) {
            title = "NoTitle " + String.valueOf(lstName.size());
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        }
        InsertNote(title);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i < lstName.size()-1)
            afterdel = true;

        deleteNote(lstName.get(i));
        loadAllTitle();
        adp.notifyDataSetChanged();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (afterdel == true)
        {
            afterdel = false;
            return;
        }

        Intent intt = new Intent();
        String title = lstName.get(i);
        ComponentName cpn;
        cpn = new ComponentName(this,InsertTitleActivity.class);
        intt.setComponent(cpn);
        intt.putExtra("TitleName",title);
        intt.putExtra("Detail",getContent(title));

        startActivityForResult(intt,13);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String oldtitle_r="", title_r = "", detail_r="";
        if (resultCode == RESULT_OK && requestCode == 13) {
            if (data.hasExtra("OldTitleR"))
                oldtitle_r = data.getExtras().getString("OldTitleR");
            if (data.hasExtra("TitleNameR"))
                title_r = data.getExtras().getString("TitleNameR");
            if (data.hasExtra("DetailR"))
                detail_r = data.getExtras().getString("DetailR");

            updateTable(oldtitle_r, title_r, detail_r);
            //lstName.set(6,"hihi");
            loadAllTitle();
            adp.notifyDataSetChanged();
            Toast.makeText(this, oldtitle_r + " + " + title_r + " + " + detail_r, Toast.LENGTH_SHORT).show();
        }
    }

    public void createDB () {
        myDB = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    }
    public void createTable()
    {
        String sql="CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (";
        //String sql="CREATE TABLE IF NOT EXISTS MyFirstTable (";
        sql+=" Title TEXT primary key, ";
        sql += "Detail TEXT);";
        myDB.execSQL(sql);
    }


    public void loadAllTitle()
    {
        lstName.clear();
        Cursor csr = myDB.query(TABLE_NAME, null, null, null, null, null, null);
        if (csr.moveToFirst() == false)
        {
            //lstName.add("SampleTitle");
            return;
        }
        String data="";
        while(csr.isAfterLast()==false)
        {
            data = csr.getString(0);
            lstName.add(data);
            csr.moveToNext();
        }
        /*if (lstName.isEmpty() == true)
            //Toast.makeText(this, "null: ", Toast.LENGTH_LONG).show();
            lstName.add("SampleTitle");*/

        //Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        csr.close();
    }
    public String getContent(String ttl) {
        Cursor csr = myDB.rawQuery("SELECT * FROM "
                + TABLE_NAME +" WHERE title = \"" + ttl + "\"", null);

        if (csr.moveToFirst() == false)
        {
            return null;
        }
        String data = csr.getString(1);
        csr.close();
        return data;
    }

    public void InsertNote(String title)
    {
        ContentValues values=new ContentValues();
        values.put("Title", title);
        values.put("Detail", "");


        String msg="";
        if(myDB.insert(TABLE_NAME, null, values)==-1){
            //msg="Failed to insert record";
            msg="please insert another title name.";
        }
        else{
            msg="insert title name is successful";
            //adapter.add(editText.getText().toString());
            lstName.add(title);
            adp.notifyDataSetChanged();
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void updateTable(String oldTitle, String title, String detail)
    {
        ContentValues values = new ContentValues();
        if(!oldTitle.equalsIgnoreCase(title))
            values.put("Title",title);
        values.put("Detail",detail);

        String[] arg = new String[] {oldTitle};
        String msg="";
        if (myDB.update(TABLE_NAME, values, "Title = ?", arg) == 0){
            msg="Failed to update record";
        }
        else{
            msg= title + " : " + detail;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void deleteNote(String title)
    {
        String msg = "";
        if(myDB.delete(TABLE_NAME, " Title = \"" + title + "\"", null ) == 0)
        {
            msg="Delete is failed";
        }
        else
        {
            msg="Delete is successful";
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


}
