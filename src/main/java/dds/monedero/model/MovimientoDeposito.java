package dds.monedero.model;

import java.time.LocalDate;

public class MovimientoDeposito extends Movimiento{
    public MovimientoDeposito(LocalDate fecha, double monto) {
        this.fecha = fecha;
        this.monto = monto;
    }

    public double calcularValor(Cuenta cuenta) { return cuenta.getSaldo() + getMonto(); }

    public boolean fueDepositado(LocalDate fecha) { return esDeLaFecha(fecha); }
}
