package co.com.k4soft.parqueaderouco.view.moviento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;
import co.com.k4soft.parqueaderouco.utilities.DateUtil;

public class MovimientoActivity extends AppCompatActivity {


    @BindView(R.id.txtPlaca)
    public EditText txtPlaca;
    @BindView(R.id.tipoTarifaSpinner)
    public  Spinner tipoTarifaSpinner;
    @BindView(R.id.btnIngreso)
    public Button btnIngreso;
    @BindView(R.id.btnSalida)
    public Button btnSalida;
    @BindView(R.id.layoutDatos)
    public ConstraintLayout layoutDatos;
    @BindView(R.id.txtHoras)
    public TextView txtHoras;
    @BindView(R.id.txtTotal)
    public TextView txtTotal;
    @BindView(R.id.txtFechaIngresada)
    public TextView txtFechaIngresada;
    @BindView(R.id.txtTarifa)
    public TextView txtTarifa;
    @BindView(R.id.txtFechaInicio)
    public TextView txtFechaInicio;
    @BindView(R.id.lbFechaIngresada)
    public TextView lbFechaIngresada;
    @BindView(R.id.btnFecha)
    public Button btnFecha;

    private int[] nuevaFecha;
    private DataBaseHelper db;
    private ActionBarUtil actionBarUtil;
    private List<Tarifa> listaTarifas;
    private Movimiento movimiento;
    private Tarifa tarifa;
    private String[] arrayTarifas;
    private String fechaIngresada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento);
        ButterKnife.bind(this);
        initComponents();
        hideComponents();
        cargarSpinner();
        spinnerOnItemSelected();
    }

    private void spinnerOnItemSelected() {
        tipoTarifaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tarifa = listaTarifas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarSpinner() {
       listaTarifas = db.getTarifaDAO().listar();
        if(listaTarifas.isEmpty()){
            Toast.makeText(getApplication(),R.string.sin_tarifas,Toast.LENGTH_SHORT).show();
            finish();
        }else{
           arrayTarifas = new String[listaTarifas.size()];
            for(int i = 0; i < listaTarifas.size(); i++){
                arrayTarifas[i] = listaTarifas.get(i).getNombre()+": $"+listaTarifas.get(i).getPrecio();
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,arrayTarifas);
            tipoTarifaSpinner.setAdapter(arrayAdapter);

        }
    }

    private void hideComponents() {
        tipoTarifaSpinner.setVisibility(View.GONE);
        btnIngreso.setVisibility(View.GONE);
        btnSalida.setVisibility(View.GONE);
        layoutDatos.setVisibility(View.GONE);
        lbFechaIngresada.setVisibility(View.GONE);
        txtFechaIngresada.setVisibility(View.GONE);
        btnFecha.setVisibility(View.GONE);
        actualizarFecha(DateUtil.getCurrenDate());
    }

    private void initComponents() {
        db = DataBaseHelper.getDBMainThread(this);
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.registrsr_ingreso_salida));
                            //"yyyy-MM-dd HH:mm:ss"
        nuevaFecha = new int[]{0, 0, 0, 0, 0, 0};
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void buscarPlaca(View view) {
       movimiento = db.getMovimientoDAO().findByPLaca(txtPlaca.getText().toString());
      if(movimiento == null){
          showComponentesIngreso();
      }else{
          actualizarLayoutDatos();
          showComponentesSalida();
      }
    }

    private void showComponentesSalida() {
        btnSalida.setVisibility(View.VISIBLE);
        layoutDatos.setVisibility(View.VISIBLE);
        btnFecha.setVisibility(View.VISIBLE);
        txtFechaIngresada.setVisibility(View.VISIBLE);
        lbFechaIngresada.setText(R.string.Salida);
        lbFechaIngresada.setVisibility(View.VISIBLE);
    }

    private void showComponentesIngreso() {
        tipoTarifaSpinner.setVisibility(View.VISIBLE);
        btnIngreso.setVisibility(View.VISIBLE);
        btnFecha.setVisibility(View.VISIBLE);
        txtFechaIngresada.setVisibility(View.VISIBLE);
        lbFechaIngresada.setText(R.string.Entrada);
        lbFechaIngresada.setVisibility(View.VISIBLE);
    }

    public void registrarIngreso(View view) {
        if(tarifa == null){
            Toast.makeText(getApplicationContext(),R.string.debe_seleccionar_tarifa, Toast.LENGTH_SHORT).show();
        }else if(movimiento == null){
            movimiento = new Movimiento();
            movimiento.setPlaca(txtPlaca.getText().toString());
            movimiento.setIdTarifa(tarifa.getIdTarifa());
            movimiento.setFechaEntrada(fechaIngresada);
            movimiento.setFinalizaMovimiento(false);
            new InsercionMoviento().execute(movimiento);
            movimiento = null;
            hideComponents();
        }
    }

    public void registrarSalida(View view) {
        movimiento.setFechaSalida(DateUtil.getCurrenDate());
        movimiento.setFinalizaMovimiento(true);
        new SalidaMoviento().execute(movimiento);
        movimiento = null;
        hideComponents();
    }

    public void goToAbrirCalendario(View view) {
        final int ano_actual, mes_actual, dia_actual, hora_actual, minuto_actual, segundo_actual;
        Context context=this;
        Calendar cal= Calendar.getInstance();
        ano_actual = cal.get(Calendar.YEAR);
        mes_actual= cal.get(Calendar.MONDAY);
        dia_actual= cal.get(Calendar.DAY_OF_MONTH);
        hora_actual=cal.get(Calendar.HOUR_OF_DAY);
        minuto_actual=cal.get(Calendar.MINUTE);
        segundo_actual=cal.get(Calendar.SECOND);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano_elegido, int mes_elegido, int dia_elegido) {
                nueva_fecha(ano_elegido,mes_elegido,dia_elegido);
                TimePickerDialog tpd = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hora_elegida, int minuto_elegido) {
                        nueva_hora(hora_elegida,minuto_elegido,segundo_actual);
                    }
                }, hora_actual, minuto_actual, true);

                tpd.show();
            }
        },ano_actual,mes_actual,dia_actual);
        dpd.show();
    }

    private void actualizarFecha(String fecha){
        fechaIngresada=fecha;
        txtFechaIngresada.setText(fechaIngresada);
        if(movimiento != null) actualizarLayoutDatos();
    }

    private void actualizarLayoutDatos(){
        String horas = null;
        int total= 0;
        try {
            horas = DateUtil.timeFromDates(movimiento.getFechaEntrada(),fechaIngresada);
            total = DateUtil.hoursFromDates(movimiento.getFechaEntrada(),fechaIngresada);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Tarifa tar=db.getTarifaDAO().findById(movimiento.getIdTarifa());
        double totalPecio = total * tar.getPrecio();
        txtFechaInicio.setText(movimiento.getFechaEntrada());
        txtHoras.setText(horas);
        txtTarifa.setText("$"+Double.toString(tar.getPrecio()));
        txtTotal.setText("$"+Double.toString(totalPecio));
    }

    public void nueva_fecha(int ano_objetivo, int mes_objetivo, int dia_objetivo){
        nuevaFecha[0]=ano_objetivo;
        nuevaFecha[1]=mes_objetivo;
        nuevaFecha[2]=dia_objetivo;
    }
    public void nueva_hora(int hora_objetivo, int minuto_objetivo, int segundo_objetivo){
        nuevaFecha[3]=hora_objetivo;
        nuevaFecha[4]=minuto_objetivo;
        nuevaFecha[5]=segundo_objetivo;
        //"yyyy-MM-dd HH:mm:ss"
        String fechaActualizada=nuevaFecha[0]+"-"+nuevaFecha[1]+"-"+nuevaFecha[2]+" "+nuevaFecha[3]+":"+nuevaFecha[4]+":"+nuevaFecha[5];
        actualizarFecha(fechaActualizada);
    }

    private class InsercionMoviento extends AsyncTask<Movimiento, Void,Void>{

        @Override
        protected Void doInBackground(Movimiento... movimientos) {
            DataBaseHelper.getSimpleDB(getApplicationContext()).getMovimientoDAO().insert(movimientos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),R.string.informacion_guardada_exitosamente, Toast.LENGTH_SHORT).show();
        }
    }
    private class SalidaMoviento extends AsyncTask<Movimiento, Void,Void>{

        @Override
        protected Void doInBackground(Movimiento... movimientos) {
            DataBaseHelper.getSimpleDB(getApplicationContext()).getMovimientoDAO().update(movimientos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),R.string.informacion_guardada_exitosamente, Toast.LENGTH_SHORT).show();
        }
    }

}
