package ar.com.webnoa.consultaenconsultorio;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



public class AnularXml extends AsyncTask{

/* --------------------------------------------------Argumentos --------------------------------------------------*/

    Context context;
    Toast toast;
    SQLControlador dbconeccion2;
    URL url;

    private static final String dToken="32dbf220f1ab2303592b4a076162c221600ef704";
    private static final String dCuitPrestador="30708402911";
    private static final String dCodigoFinanciador = "11";
    private static final String dCuitFinanciador = "30546741253";

    private String NroReferenciaCancel;
    private String dNumeroCredencial;
    private String dFechaAtencion;
    private String dTextoId;
    private String dTextoTx;
    private String respuesta;
    private Long ide;

    //------------------------------------
    Random rand = new Random();
    private int n = rand.nextInt(99999999);
    private String dIdMsj=String.valueOf(n);

    /* -------------------------------------------------- Constructor --------------------------------------------------*/

    public AnularXml(Context dContext, String dNroreferenciaCancel, String dNroCredencial, String dTextoId, String dTextoTx){
        context=dContext;
        this.NroReferenciaCancel=dNroreferenciaCancel;
        this.dNumeroCredencial=dNroCredencial;
        this.dFechaAtencion="";
        this.dTextoId=dTextoId;
        this.dTextoTx=dTextoTx;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        ProcesarXML (obtenerResultado());
        return null;
    }

    @Override
    protected void onPreExecute() {
        ide=Long.valueOf(dTextoId);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {

        switch (respuesta) {
            case "00":
                toast = Toast.makeText(context, "Transacción " + dTextoTx + " Anulada", Toast.LENGTH_LONG);
                toast.show();
                break;

            case "SN":
                toast = Toast.makeText(context, "Compruebe su conexión a internet y vuelva a intentar", Toast.LENGTH_SHORT);
                toast.show();
                break;

            default:
                toast = Toast.makeText(context, "Transacción ya anulada!", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
        ((MainActivity) context).onRestart();

        super.onPostExecute(o);
    }

    /* - -------------------------------------------------- Parsear XML -------------------------------------------------- -*/
    private void ProcesarXML(Document data) {

        if (data != null) {

            Element root = data.getDocumentElement();
            Node EncabezadoMensaje = root.getChildNodes().item(0);
            NodeList items = EncabezadoMensaje.getChildNodes();

            respuesta=items.item(1).getChildNodes().item(0).getTextContent();// Codigo Resultado  (00)OK

            if (respuesta.equals("00")) {
                dbconeccion2 = new SQLControlador(context);
                dbconeccion2.abrirBaseDeDatos();
                dbconeccion2.actualizarDatos(ide,dTextoTx+" - Anulada");
                dbconeccion2.cerrar();
            }
        }else{
            respuesta="SN";
        }
    }

    /* - -------------------------------------------------- Direccion Http + XML -------------------------------------------------- -*/
    private String DireXml(){
        return "http://ws.itcsoluciones.com:48080/jSitelServlet/Do?pas="
                +dToken+"&msj=<Mensaje><EncabezadoMensaje><VersionMsj>1.0</VersionMsj><NroReferenciaCancel>"
                +NroReferenciaCancel+"</NroReferenciaCancel><TipoTransaccion>04A</TipoTransaccion><IdMsj>"
                +dIdMsj+"</IdMsj><InicioTrx><FechaTrx></FechaTrx><HoraTrx></HoraTrx></InicioTrx><Financiador><CodigoFinanciador>"
                +dCodigoFinanciador+"</CodigoFinanciador><CuitFinanciador>"
                +dCuitFinanciador+"</CuitFinanciador></Financiador><Prestador><CuitPrestador>"
                +dCuitPrestador+"</CuitPrestador></Prestador></EncabezadoMensaje><EncabezadoAtencion><Credencial><NumeroCredencial>"
                +dNumeroCredencial+"</NumeroCredencial></Credencial><Atencion><FechaAtencion>"
                +dFechaAtencion+"</FechaAtencion></Atencion></EncabezadoAtencion></Mensaje>";
    }

    /* - -------------------------------------------------- Generar  XML -------------------------------------------------- -*/
    public Document obtenerResultado(){
        try {
            url= new URL(DireXml());
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream=connection.getInputStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc=builder.parse(inputStream);
            return xmlDoc;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
