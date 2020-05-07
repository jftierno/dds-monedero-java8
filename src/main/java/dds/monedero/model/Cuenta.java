package dds.monedero.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();
  private List<MovimientoDeposito> movimientosDepositos = new ArrayList<>();
  private List<MovimientoExtraccion> movimientosExtracciones = new ArrayList<>();

  public Cuenta() { saldo = 0; }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) { this.movimientos = movimientos; }

  public void setMovimientosDepositos(List<MovimientoDeposito> movimientos) { this.movimientosDepositos = movimientos; }

  public void setMovimientosExtracciones(List<MovimientoExtraccion> movimientos) { this.movimientosExtracciones = movimientos; }

  public void setSaldo(double saldo) { this.saldo = saldo;}

  public void depositar(double monto) {
    chequearMontoNegativo(monto);
    chequearCantidadDepositos();

    MovimientoDeposito movimientoDeposito = new MovimientoDeposito(LocalDate.now(), monto);
    agregarMovimiento(movimientoDeposito);
    efectuarDeposito(movimientoDeposito);
  }

  public void extraer(double monto) {
    chequearMontoNegativo(monto);
    puedeSacar(monto);
    chequearLimiteExtraccion(monto);

    MovimientoExtraccion movimientoExtraccion = new MovimientoExtraccion(LocalDate.now(), monto);
    agregarMovimiento(movimientoExtraccion);
    efectuarExtraccion(movimientoExtraccion);
  }

  public void agregarMovimiento(Movimiento movimiento) { movimientos.add(movimiento); }

  public void efectuarDeposito(MovimientoDeposito movimientoDeposito) {
    setSaldo(movimientoDeposito.calcularValor(this));
    movimientosDepositos.add(movimientoDeposito);
  }

  public void efectuarExtraccion(MovimientoExtraccion movimientoExtraccion) {
    setSaldo(movimientoExtraccion.calcularValor(this));
    movimientosExtracciones.add(movimientoExtraccion);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return extraccionesDe(fecha)
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public List<MovimientoDeposito> getMovimientosDepositos() { return movimientosDepositos;}

  public List<MovimientoExtraccion> getMovimientosExtracciones() { return movimientosExtracciones; }

  public double getLimiteExtraccion() { return 1000-getMontoExtraidoA(LocalDate.now()); }

  public double getSaldo() { return saldo; }

  public void chequearMontoNegativo(double monto) {
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void chequearCantidadDepositos()
  {
    if (depositosDe(LocalDate.now()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void chequearLimiteExtraccion(double monto) {
    if (monto > getLimiteExtraccion()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
              + " diarios, límite: " + getLimiteExtraccion());
    }
  }

  public void puedeSacar(double monto)
  {
    if (getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public Stream<MovimientoDeposito> depositosDe(LocalDate fecha) {
    Stream<MovimientoDeposito> movimientos = getMovimientosDepositos().stream();
    return movimientos.filter(movimiento -> movimiento.esDeLaFecha(fecha));
  }

  public Stream<MovimientoExtraccion> extraccionesDe(LocalDate fecha) {
    Stream<MovimientoExtraccion> movimientos = getMovimientosExtracciones().stream();
    return movimientos.filter(movimiento -> movimiento.esDeLaFecha(fecha));
  }

}