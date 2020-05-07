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

  public void poner(double cuanto) {
    chequearMontoNegativo(cuanto);
    chequearCantidadDepositos();

    MovimientoDeposito movimientoDeposito = new MovimientoDeposito(LocalDate.now(), cuanto);
    agregarMovimiento(movimientoDeposito);
    efectuarDeposito(movimientoDeposito);
  }

  public void sacar(double cuanto) {
    chequearMontoNegativo(cuanto);
    puedeSacar(cuanto);
    chequearLimiteExtraccion(cuanto);

    MovimientoExtraccion movimientoExtraccion = new MovimientoExtraccion(LocalDate.now(), cuanto);
    agregarMovimiento(movimientoExtraccion);
    efectuarExtraccion(movimientoExtraccion);
  }

  public void agregarMovimiento(Movimiento movimiento) { movimientos.add(movimiento); }

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

  public double getSaldo() { return saldo; }
  public void setSaldo(double saldo) { this.saldo = saldo;}

  public void chequearMontoNegativo(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void chequearCantidadDepositos()
  {
    if (depositosDe(LocalDate.now()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void chequearLimiteExtraccion(double cuanto) {
    if (cuanto > getLimiteExtraccion()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
              + " diarios, límite: " + getLimiteExtraccion());
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

  public void puedeSacar(double cuanto)
  {
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public double getLimiteExtraccion() { return 1000-getMontoExtraidoA(LocalDate.now()); }

  public void efectuarDeposito(MovimientoDeposito movimientoDeposito) {
    setSaldo(movimientoDeposito.calcularValor(this));
    movimientosDepositos.add(movimientoDeposito);
  }

  public void efectuarExtraccion(MovimientoExtraccion movimientoExtraccion) {
    setSaldo(movimientoExtraccion.calcularValor(this));
    movimientosExtracciones.add(movimientoExtraccion);
  }

}