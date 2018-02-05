package ar.com.webnoa.consultaenconsultorio;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class SQLControlador {
    private DBhelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLControlador(Context c) {
        ourcontext = c;
    }

    public SQLControlador abrirBaseDeDatos() throws SQLException {
        dbhelper = new DBhelper(ourcontext);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    public void insertarDatos(String referencia,String idMsj,String fechaHora, String nombre, String nroAfiliado, String nroTx) {

        ContentValues cv = new ContentValues();
        cv.put(DBhelper.TX_REFERENCIA, referencia);
        cv.put(DBhelper.TX_IDMSJ, idMsj);
        cv.put(DBhelper.TX_FECHAYHORA, fechaHora);
        cv.put(DBhelper.TX_NOMBRE, nombre);
        cv.put(DBhelper.TX_NROAFILIADO, nroAfiliado);
        cv.put(DBhelper.TX_NROTX, nroTx);

        database.insert(DBhelper.TABLE_TX, null, cv);
    }

    public Cursor leerDatos() {
        String[] todasLasColumnas = new String[] {
                DBhelper.TX_ID,
                DBhelper.TX_REFERENCIA,
                DBhelper.TX_IDMSJ,
                DBhelper.TX_FECHAYHORA,
                DBhelper.TX_NOMBRE,
                DBhelper.TX_NROAFILIADO,
                DBhelper.TX_NROTX
        };
        Cursor c = database.query(DBhelper.TABLE_TX, todasLasColumnas, null,
                null, null, null, "_id DESC");
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


        public int actualizarDatos(long vID, String VnroTx) {
            //public int actualizarDatos(long vID, String Vreferencia,String VidMsj,String VfechaHora, String Vnombre, String VnroAfiliado, String VnroTx) {
        ContentValues cvActualizar = new ContentValues();

        cvActualizar.put(DBhelper.TX_NROTX, VnroTx);
        int i = database.update(DBhelper.TABLE_TX, cvActualizar,
                DBhelper.TX_ID + " = " + vID, null);
        return i;
    }

    public void deleteData(Long dID) {
        database.delete(DBhelper.TABLE_TX, DBhelper.TX_ID + "="
                + dID, null);
    }

    public void cerrar() {
        dbhelper.close();
    }
}
