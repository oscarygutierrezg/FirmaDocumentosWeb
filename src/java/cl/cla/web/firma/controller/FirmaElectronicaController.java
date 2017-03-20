/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.controller;

/**
 *
 * @author amunozme
 */
public interface FirmaElectronicaController {

    public byte[] firmarDocumento(int numeroDePagina, String codigoDeTransaccion,String codigoSistema,String codigoAplicacion, String codigoTerminal,byte[] archivo) throws Exception;

}
