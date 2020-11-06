package co.com.k4soft.parqueaderouco.view.reporte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;
import co.com.k4soft.parqueaderouco.utilities.DateUtil;

public class ReporteActivity extends AppCompatActivity {

    private ActionBarUtil actionBarUtil;
    @BindView(R.id.listViewSalidas)
    public ListView listViewSalidas;
    @BindView(R.id.txtRecaudo)
    public TextView txtRecaudo;
    public int recaudado;
    public List<Movimiento> listaSalidas;
    DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        ButterKnife.bind(this);
        initComponents();
        loadSalidas();
    }
    private void initComponents() {
        txtRecaudo.setText("nada");
        db = DataBaseHelper.getDBMainThread(this);
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.Salidas));
    }
    private void loadSalidas() {
            recaudado=0;
            listaSalidas = db.getMovimientoDAO().listarSalidas();
            if (listaSalidas.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.sin_tarifas, Toast.LENGTH_SHORT).show();
            } else {
                String[] SalidasArray = new String[listaSalidas.size()];
                for (int i = 0; i < listaSalidas.size(); i++) {
                    Movimiento movimiento = listaSalidas.get(i);
                    int tiempo=0;
                    try {
                        tiempo = DateUtil.hoursFromDates(movimiento.getFechaEntrada(), movimiento.getFechaSalida());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Tarifa tar = db.getTarifaDAO().findById(movimiento.getIdTarifa());
                    int pago = (int) (tiempo * tar.getPrecio());
                    recaudado+=pago;
                    SalidasArray[i] = "Placa: "+movimiento.getPlaca()+"  Salida: "+movimiento.getFechaSalida()+" Valor: "+pago;
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, SalidasArray);
                listViewSalidas.setAdapter(arrayAdapter);
            }
            txtRecaudo.setText(Integer.toString(recaudado));
    }
    @Override
    public void onRestart() {
        super.onRestart();
        loadSalidas();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}