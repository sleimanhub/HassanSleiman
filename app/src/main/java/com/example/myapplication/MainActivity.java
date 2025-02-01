package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnInsert , btnSelectItem, btnDelete, btnSelectAll, btnUpdate, btnLogout;
    EditText id, name, price, quantity;
    FirebaseAuth auth;
    FirebaseUser user;


//        Clear

    SQLiteDatabase myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        btnInsert = findViewById(R.id.btnInsert);
        btnSelectItem = findViewById(R.id.btnSelectItem);
        btnDelete = findViewById(R.id.btnDelete);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        id = findViewById(R.id.etID);
        name = findViewById(R.id.etName);
        price = findViewById(R.id.etPrice);
        quantity = findViewById(R.id.etQty);

//          Creating database
        myDb = openOrCreateDatabase("productDb", MODE_PRIVATE, null);
//          Creating table
        String sql = "CREATE TABLE IF NOT EXISTS products(" +
                                                         "id INT PRIMARY KEY," +
                                                         "name VARCHAR," +
                                                        "price DOUBLE," +
                                                        "quantity INT)";
        myDb.execSQL(sql);


//        Insert
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id.getText().toString().trim().isEmpty() ||
                        name.getText().toString().trim().isEmpty() ||
                        price.getText().toString().trim().isEmpty() ||
                        quantity.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                String sql = "SELECT * FROM products WHERE id = '" + id.getText() + "'";
                Cursor c = myDb.rawQuery(sql, null);

                if(c.getCount() > 0){
                    Toast.makeText(MainActivity.this, "Select a different id", Toast.LENGTH_SHORT).show();
                    return;
                }
                String sql1 = "INSERT INTO products VALUES ('"+id.getText()+ "','" + name.getText()+ "','" + price.getText()+ "','" + quantity.getText()+ "')";
                myDb.execSQL(sql1);
                Toast.makeText(MainActivity.this, "New product added succesfully", Toast.LENGTH_SHORT).show();
                clear();

            }
        });

//        Delete
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter the product id first", Toast.LENGTH_SHORT).show();
                    return;
                }
                String sql = "SELECT * FROM products WHERE id = '" + id.getText() + "'";
                Cursor c = myDb.rawQuery(sql, null);

                if(c.getCount() > 0){
                new AlertDialog.Builder(MainActivity.this).setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sql1 = "DELETE FROM products WHERE id = " + id.getText();
                                myDb.execSQL(sql1);
                                Toast.makeText(MainActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                                clear();
                            }
                        }).setNegativeButton(android.R.string.no , null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();



            }else {
                Toast.makeText(MainActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
            }
            }
        });
//        select specific product
        btnSelectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter the product id first", Toast.LENGTH_SHORT).show();
                    return;
                }
                String sql = "SELECT * FROM products WHERE id = '" + id.getText() + "'";
                Cursor c = myDb.rawQuery(sql, null);

                if(c.moveToFirst()){

                    showMessage("Product " + id.getText() + ": ", "Name: "+ c.getString(1) +
                            "\nPrice: " + c.getString(2) +
                            "\nQuantity: " + c.getString(3));
                }else {
                    Toast.makeText(MainActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        select all products
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = myDb.rawQuery("SELECT * FROM products", null);
                if(c.moveToFirst()){
                    String msg = "";
                    do{
                        msg += "\nId: " + c.getString(0) +
                                "\nName: "+ c.getString(1) +
                                "\nPrice: " + c.getString(2) +
                                "\nQuantity: " + c.getString(3)+
                                "\n----------------"  ;
                    }while (c.moveToNext());
                    showMessage("All Products: ", msg);
                }else {
                    Toast.makeText(MainActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        Update
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().trim().isEmpty() ||
                        name.getText().toString().trim().isEmpty() ||
                        price.getText().toString().trim().isEmpty() ||
                        quantity.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                String sql = "SELECT * FROM products WHERE id = " + id.getText();
                Cursor c = myDb.rawQuery(sql, null);
                if(c.moveToFirst()){
                    String sql1 = "UPDATE products SET name = '" + name.getText() + "', price = '" + price.getText() + "', quantity = '" + quantity.getText() + "' WHERE id = '" + id.getText() + "'";
                    myDb.execSQL(sql1);
                    Toast.makeText(MainActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    clear();
                }else{
                    Toast.makeText(MainActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void clear(){
        id.setText(null);
        name.setText(null);
        price.setText(null);
        quantity.setText(null);
    }

    public void showMessage(String title , String message){
        AlertDialog.Builder b = new AlertDialog.Builder((this));
        b.setCancelable(true);
        b.setIcon(R.drawable.db_icon);
        b.setTitle(title);
        b.setMessage(message);
        b.show();

    }
}