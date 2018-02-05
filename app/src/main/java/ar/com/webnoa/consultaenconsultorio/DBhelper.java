package ar.com.webnoa.consultaenconsultorio;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper{
    // Contenido de la tabla
    static final String TABLE_TX = "transacciones";
    static final String TX_ID = "_id";
    static final String TX_REFERENCIA = "referencia";
    static final String TX_IDMSJ = "idMsj";
    static final String TX_FECHAYHORA = "fechaHora";
    static final String TX_FECHA = "fechaTx";
    static final String TX_NOMBRE = "nombre";
    static final String TX_NROAFILIADO = "nroAfiliado";
    static final String TX_NROTX = "nroTx";
    // información del a base de datos
    private static final String DB_NAME = "DATA-DB";
    private static final int DB_VERSION = 1;
    // Información de la tabla
    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_TX + "(" + TX_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TX_REFERENCIA + " TEXT, "
            + TX_IDMSJ + " TEXT, "
            + TX_FECHAYHORA + " TEXT, "
            + TX_FECHA + " TEXT, "
            + TX_NOMBRE + " TEXT, "
            + TX_NROAFILIADO + " TEXT,"
            + TX_NROTX + " TEXT); ";

    public DBhelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TX);
        onCreate(db);
    }
}
