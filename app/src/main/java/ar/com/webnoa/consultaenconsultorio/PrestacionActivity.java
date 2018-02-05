package ar.com.webnoa.consultaenconsultorio;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PrestacionActivity extends AppCompatActivity {
    Button validarBtn;
    EditText nroAfiliadoTxt,cdsTxt;
    TextView resultadoTxt,avisoTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestacion);

        nroAfiliadoTxt =findViewById(R.id.nroAfiliadoTxt);
        cdsTxt = findViewById(R.id.cdsTxt);
        validarBtn = findViewById(R.id.validarBtn);
        resultadoTxt = findViewById(R.id.ResultadoTxt);
        avisoTxt = findViewById(R.id.AvisoTxt);

        validarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = nroAfiliadoTxt.getText().toString();
                avisoTxt.setText("");
                resultadoTxt.setText("");
                if(TextUtils.isEmpty(pass) || pass.length() < 11)
                {
                    avisoTxt.setText("Nro Afiliado, No puede ser menor o mayor, a 11 digitos");
                    avisoTxt.setTextColor(Color.parseColor("#ffcc0000"));
                    //return;
                }else{
                    //oculto el teclado
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(cdsTxt.getWindowToken(), 0);

                    PrestacionXml prestacionXML = new PrestacionXml(PrestacionActivity.this,nroAfiliadoTxt,cdsTxt,resultadoTxt,avisoTxt);

                    prestacionXML.execute();
                }
            }
        });
    }
}
