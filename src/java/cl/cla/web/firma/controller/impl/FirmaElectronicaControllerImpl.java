/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.controller.impl;

import cl.cla.web.firma.controller.FirmaElectronicaController;

import cl.cla.web.firma.integracion.FirmaElectronicaTOCIntegracion;
import cl.cla.web.firma.vo.FirmaElectronicaVO;
import cl.cla.web.firma.vo.UbicacionVO;

/**
 *
 * @author amunozme
 */
public class FirmaElectronicaControllerImpl implements FirmaElectronicaController {

    private FirmaElectronicaTOCIntegracion integracion;

    public FirmaElectronicaControllerImpl() {
        integracion = new FirmaElectronicaTOCIntegracion();
    }

    @Override
    public byte[] firmarDocumento(int numeroDePagina, String codigoDeTransaccion,String codigoSistema,String codigoAplicacion, String codigoTerminal,byte[] archivo) throws Exception {
        try {
            UbicacionVO ubicacionSello = new UbicacionVO();
            ubicacionSello.setNumeroDePagina(numeroDePagina);
            ubicacionSello.setX(100);
            ubicacionSello.setY(100);
            UbicacionVO ubicacionEstampa = new UbicacionVO();
            ubicacionEstampa.setNumeroDePagina(numeroDePagina);
            ubicacionEstampa.setX(300);
            ubicacionEstampa.setY(100);

            FirmaElectronicaVO firmaElectronica = new FirmaElectronicaVO();
            firmaElectronica.setCodigoAplicacion(codigoAplicacion);
            firmaElectronica.setCodigoSistema(codigoSistema);
            firmaElectronica.setCodigoTerminal(codigoTerminal);
            firmaElectronica.setArchivo(archivo);
            firmaElectronica.setUbicacionEstampa(ubicacionEstampa);
            firmaElectronica.setUbicacionSello(ubicacionSello);
            firmaElectronica.setCodigoDeTransaccion(codigoDeTransaccion);
            FirmaElectronicaTOCIntegracion docOpIntegracion = new FirmaElectronicaTOCIntegracion();
            docOpIntegracion.firmarDocumento(firmaElectronica);
            return integracion.firmarDocumento(firmaElectronica);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}
