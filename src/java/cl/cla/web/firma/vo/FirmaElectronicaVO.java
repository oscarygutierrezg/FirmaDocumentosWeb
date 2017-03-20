/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.vo;

import java.io.Serializable;

/**
 *
 * @author oscar
 */
public class FirmaElectronicaVO implements Serializable {

    private String codigoSistema;
    private String codigoAplicacion;
    private String codigoTerminal;
    private String codigoDeTransaccion;
    private byte[] archivo;
    private UbicacionVO ubicacionSello;
    private UbicacionVO ubicacionEstampa;
    private ResponseVO response;

    public String getCodigoSistema() {
        return codigoSistema;
    }

    public void setCodigoSistema(String codigoSistema) {
        this.codigoSistema = codigoSistema;
    }

    public String getCodigoAplicacion() {
        return codigoAplicacion;
    }

    public void setCodigoAplicacion(String codigoAplicacion) {
        this.codigoAplicacion = codigoAplicacion;
    }

    public String getCodigoTerminal() {
        return codigoTerminal;
    }

    public void setCodigoTerminal(String codigoTerminal) {
        this.codigoTerminal = codigoTerminal;
    }
    
    

    public String getCodigoDeTransaccion() {
        return codigoDeTransaccion;
    }

    public void setCodigoDeTransaccion(String codigoDeTransaccion) {
        this.codigoDeTransaccion = codigoDeTransaccion;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public UbicacionVO getUbicacionSello() {
        return ubicacionSello;
    }

    public void setUbicacionSello(UbicacionVO ubicacionSello) {
        this.ubicacionSello = ubicacionSello;
    }

    public UbicacionVO getUbicacionEstampa() {
        return ubicacionEstampa;
    }

    public void setUbicacionEstampa(UbicacionVO ubicacionEstampa) {
        this.ubicacionEstampa = ubicacionEstampa;
    }

    public ResponseVO getResponse() {
        return response;
    }

    public void setResponse(ResponseVO response) {
        this.response = response;
    }

}
