package ar.com.webnoa.consultaenconsultorio;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class PrestacionXml extends AsyncTask{

    /* --------------------------------------------------Argumentos --------------------------------------------------*/

    Context context;
    TextView ResultadoTxt,AvisoTxt;
    EditText NroAfiliado,CodSeg;
    SQLControlador dbconeccion;
    URL url;


    private static final String dToken="32dbf220f1ab2303592b4a076162c221600ef704";
    private static final String dCuitPrestador="30708402911";
    private static final String dCodPrestacion = "420101";
    private static final String dCodigoFinanciador = "11";
    private static final String dCuitFinanciador = "30546741253";

    private String dNumeroCredencial;
    private String dVersionCredencial;
    private String resultado;
    private String respuesta;
    private String aviso;
    private String colorAviso;

    //-----------------Random NRo IDMSJ-------------------
    Random rand = new Random();
    int n = rand.nextInt(99999999);

    private String dIdMsj=String.valueOf(n);

    //------------------ FECHA ------------------
    Date todayDate = Calendar.getInstance().getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatter2 = new SimpleDateFormat("HHmmss");
    SimpleDateFormat formatter3 = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private String dFechaTrx=formatter.format(todayDate);
    private String dHoraTrx=formatter2.format(todayDate);
    private String fecha=formatter3.format(todayDate);

    /* -------------------------------------------------- Constructor --------------------------------------------------*/
    public PrestacionXml(Context dcontext, EditText dNroAfiliado, EditText dCodSeg, TextView dResultado, TextView davisoTxt){
        context = dcontext;
        ResultadoTxt = dResultado;
        AvisoTxt=davisoTxt;
        NroAfiliado=dNroAfiliado;
        CodSeg=dCodSeg;
        dNumeroCredencial = dNroAfiliado.getText().toString();
        dVersionCredencial = dCodSeg.getText().toString();
    }

    /*-----------------------------------------------Metodos PRE / POST---------------------------------------------------------------*/

    @Override
    protected void onPreExecute() {
        ResultadoTxt.setText("Espere por favor...");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {

        if (respuesta.equals("00")) {
            NroAfiliado.setText("");
            CodSeg.setText("");
            NroAfiliado.requestFocus();
            dbconeccion.cerrar();
        }else{}
        AvisoTxt.setText(aviso);
        AvisoTxt.setTextColor(Color.parseColor("#"+colorAviso+""));
        ResultadoTxt.setText(resultado);
        resultado="";

        super.onPostExecute(o);
    }


    @Override
    protected Object doInBackground(Object[] objects) {
              ProcesarXML(obtenerResultado());
         return null;
    }


    /* - -------------------------------------------------- Parsear XML -------------------------------------------------- -*/
    private void ProcesarXML(Document data) {

        if (data != null) {

            Element root = data.getDocumentElement();
            Node EncabezadoMensaje = root.getChildNodes().item(0);
            Node EncabezadoAtencion = root.getChildNodes().item(1);

            NodeList items = EncabezadoMensaje.getChildNodes();
            NodeList items2 = EncabezadoAtencion.getChildNodes();

            //Separa tx del resto del string
            String transaccion = items.item(1).getChildNodes().item(3).getTextContent();//Fecha y TX
            String[] txParts = transaccion.split("IT");
            String txResult = txParts[1];//TX

            respuesta=items.item(1).getChildNodes().item(0).getTextContent();// Codigo Resultado  (00)OK
            resultado= items.item(1).getChildNodes().item(2).getTextContent();// Ticket

            if (respuesta.equals("00")) {
                colorAviso="ff669900";
                aviso="¡ Ok - Prestacion Registrada !";

                dbconeccion = new SQLControlador(context);
                dbconeccion.abrirBaseDeDatos();
                dbconeccion.insertarDatos(items.item(3).getChildNodes().item(0).getTextContent(), items.item(6).getChildNodes().item(0).getTextContent(), fecha, dFechaTrx, items2.item(0).getChildNodes().item(0).getTextContent(), items2.item(1).getChildNodes().item(0).getTextContent(), txResult);

            }else{
                aviso="¡ Solicitud Rechazada !";
                colorAviso="ffcc0000";
            }

        }else {
            respuesta="SN";
            resultado="Compruebe su conexión a internet y vuelva a intentar";
            aviso="Error, Sin Conexión";
            colorAviso="ffcc0000";
        }
    }

    /*-------------------------------------------------- Direccion Http ------------------------------------------------------------*/
    private String DireXml(){
        return "http://ws.itcsoluciones.com:48080/jSitelServlet/Do?pas="
                +dToken+"&msj=<Mensaje><EncabezadoMensaje><VersionMsj>1.0</VersionMsj><TipoTransaccion>02A</TipoTransaccion><IdMsj>"
                +dIdMsj+"</IdMsj><InicioTrx><FechaTrx>"
                +dFechaTrx+"</FechaTrx><HoraTrx>"
                +dHoraTrx+"</HoraTrx></InicioTrx><Financiador><CodigoFinanciador>"
                +dCodigoFinanciador+"</CodigoFinanciador><CuitFinanciador>"
                +dCuitFinanciador+"</CuitFinanciador></Financiador><Prestador><CuitPrestador>"
                +dCuitPrestador+"</CuitPrestador></Prestador></EncabezadoMensaje><EncabezadoAtencion><Credencial><NumeroCredencial>"
                +dNumeroCredencial+"</NumeroCredencial><VersionCredencial>"
                +dVersionCredencial+"</VersionCredencial><ModoIngreso>M</ModoIngreso></Credencial></EncabezadoAtencion><DetalleProcedimientos><NroItem>1</NroItem><CodPrestacion>"
                +dCodPrestacion+"</CodPrestacion><CodAlternativo></CodAlternativo><TipoPrestacion>1</TipoPrestacion>"
                +"<ArancelPrestacion>0</ArancelPrestacion><CantidadSolicitada>1</CantidadSolicitada></DetalleProcedimientos></Mensaje>";
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

