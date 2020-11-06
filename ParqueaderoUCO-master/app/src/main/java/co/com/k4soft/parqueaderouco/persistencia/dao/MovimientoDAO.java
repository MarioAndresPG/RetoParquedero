package co.com.k4soft.parqueaderouco.persistencia.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;

@Dao
public interface MovimientoDAO {

    @Query("SELECT * FROM MOVIMIENTO Where placa=:placa AND finalizaMovimiento = 0")
    Movimiento   findByPLaca(String placa);

    @Query("SELECT * FROM MOVIMIENTO WHERE finalizaMovimiento = 1 ORDER BY fechaSalida DESC")
    List<Movimiento> listarSalidas();

    @Insert
    void insert(Movimiento movimiento);

    @Update
    void update(Movimiento movimiento);
}
