package ar.com.webnoa.consultaenconsultorio;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button prestacionBtn;
    ListView listaPrestaciones;
    SQLControlador dbconeccion;
    Toast toast;
    TextView textoId,textoReferencia,textoNroAfiliado,textoTx;

    public static String dTextoId,dTextoReferencia,dTextoNroAfiliado,dTextoTx;
    int posision;
   // private Long ide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prestacionBtn = findViewById(R.id.prestacionBtn);

        listaPrestaciones= findViewById(R.id.listaPrestaciones);

        prestacionBtn.setOnClickListener(new View.OnClickListener() {
            //Boton prestacion
            @Override
            public void onClick(View view) {
                Intent IntPrestacion = new Intent(MainActivity.this, PrestacionActivity.class);
                startActivity(IntPrestacion);

            }
        });
        //clic en item listview
        listaPrestaciones.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                posision=i;

                textoId = view.findViewById(R.id.textoID);
                textoReferencia=view.findViewById(R.id.textoReferencia);
                textoNroAfiliado= view.findViewById(R.id.textoNumero);
                textoTx=view.findViewById(R.id.textoNroTx);

                dTextoId=textoId.getText().toString();
                dTextoReferencia=textoReferencia.getText().toString();
                dTextoNroAfiliado=textoNroAfiliado.getText().toString();
                dTextoTx=textoTx.getText().toString();

                alert();
                LecturaDeDatos();
                return false;
            }
        });
        LecturaDeDatos();
    }

    //Alert
    private void alert() {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Anular Transacción")
                .setMessage("Seguro, desea Anular la Transacción?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AnularXml anularXml = new AnularXml(MainActivity.this,dTextoReferencia,dTextoNroAfiliado,dTextoId,dTextoTx);
                        anularXml.execute();
                        dialogInterface.cancel();
                        toast=Toast.makeText(MainActivity.this,"Espere por favor...", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .show();
    }

    @Override
    protected void onRestart() {
        LecturaDeDatos();
        super.onRestart();
    }
    //Crear menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Clic icono Configuracion
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent IntConfiguracion= new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(IntConfiguracion);
                Toast toast = Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT);
                toast.show();
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void LecturaDeDatos() {
        dbconeccion = new SQLControlador(this);
        dbconeccion.abrirBaseDeDatos();

        try{
            Cursor cursor = dbconeccion.leerDatos();
            String[] from = new String[]{
                    DBhelper.TX_ID,
                    DBhelper.TX_REFERENCIA,
                    DBhelper.TX_NROTX,
                    DBhelper.TX_FECHAYHORA,
                    DBhelper.TX_NOMBRE,
                    DBhelper.TX_NROAFILIADO
            };

            int[] to = new int[] {
                    R.id.textoID,
                    R.id.textoReferencia,
                    R.id.textoNroTx,
                    R.id.textoFecha,
                    R.id.textoNombre,
                    R.id.textoNumero
            };

            final SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.lista_detalle, cursor, from, to,0);

            listaPrestaciones.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            dbconeccion.cerrar();

        }catch (Exception e){
            e.printStackTrace();

        }

    }

}

