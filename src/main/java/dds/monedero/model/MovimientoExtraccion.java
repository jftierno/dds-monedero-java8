package dds.monedero.model;

import java.time.LocalDate;

public class MovimientoExtraccion extends Movimiento{
    public MovimientoExtraccion(LocalDate fecha, double monto) {
        this.fecha = fecha;
        this.monto = monto;
    }

    public double calcularValor(Cuenta cuenta) { return cuenta.getSaldo() - getMonto(); }

    public boolean fueExtraido(LocalDate fecha) { return esDeLaFecha(fecha); }
}
