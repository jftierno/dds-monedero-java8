package dds.monedero.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;

public class MonederoTest {
  private Cuenta cuenta;

  @Before
  public void init() {
    cuenta = new Cuenta();
  }

  @Test
  public void Depositar() {
    cuenta.depositar(1500);
    Assert.assertEquals(1500, cuenta.getSaldo(), 0.001);
  }

  @Test(expected = MontoNegativoException.class)
  public void DepositarMontoNegativo() {
    cuenta.depositar(-1500);
  }

  @Test
  public void TresDepositos() {
    cuenta.depositar(1500);
    cuenta.depositar(456);
    cuenta.depositar(1900);
    Assert.assertEquals(3856, cuenta.getSaldo(), 0.001);
  }

  @Test(expected = MaximaCantidadDepositosException.class)
  public void MasDeTresDepositos() {
    cuenta.depositar(1500);
    cuenta.depositar(456);
    cuenta.depositar(1900);
    cuenta.depositar(245);
    Assert.assertEquals(4101, cuenta.getSaldo(), 0.001);
  }

  @Test(expected = SaldoMenorException.class)
  public void ExtraerMasQueElSaldo() {
    cuenta.setSaldo(90);
    cuenta.extraer(1001);
  }

  @Test(expected = MaximoExtraccionDiarioException.class)
  public void ExtraerMasDe1000() {
    cuenta.setSaldo(5000);
    cuenta.extraer(1001);
  }

  @Test(expected = MontoNegativoException.class)
  public void ExtraerMontoNegativo() {
    cuenta.extraer(-500);
  }

  @Test
  public void TresExtracciones()
  {
    cuenta.setSaldo(5000);
    cuenta.extraer(50);
    cuenta.extraer(456);
    cuenta.extraer(26);
    Assert.assertEquals(4468, cuenta.getSaldo(), 0.001);
  }

  @Test
  public void obtenerMontoEnFecha()
  {
    cuenta.setSaldo(5000);
    cuenta.extraer(50);
    cuenta.extraer(456);
    cuenta.extraer(26);
    Assert.assertEquals(532, cuenta.getMontoExtraidoA(LocalDate.now()), 0.001);
  }

}