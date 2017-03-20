package cl.cla.web.firma.controller.impl;

import cl.cla.web.firma.integracion.DetalleRepOpIntegracion;
import cl.cla.web.firma.controller.DetalleRepController;

import cl.cla.web.firma.vo.DetalleDocumentoVO;

public class DetalleRepControllerImpl implements DetalleRepController{
    
    DetalleRepOpIntegracion integracion;

    public DetalleRepControllerImpl() {
        integracion= new DetalleRepOpIntegracion();
    }

    public DetalleRepControllerImpl(String endPoint) {
        integracion= new DetalleRepOpIntegracion(endPoint);
    }
    
    

    @Override 
    public DetalleDocumentoVO detalleRepINOperation(String idActividad) throws Exception{
        try {
            return integracion.detalleRepINOperation(idActividad);
        } catch (Exception e) {
            throw new Exception(e);
        }

    }
}
