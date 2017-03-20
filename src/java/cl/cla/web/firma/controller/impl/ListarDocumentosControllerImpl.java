package cl.cla.web.firma.controller.impl;

import cl.cla.web.firma.integracion.ListarDocumentosOPIntegracion;
import cl.cla.web.firma.controller.ListarDocumentosController;

import cl.cla.web.firma.vo.ListarDocumentosVO;

public class ListarDocumentosControllerImpl implements ListarDocumentosController{

    ListarDocumentosOPIntegracion integracion;

    public ListarDocumentosControllerImpl() {
        integracion= new ListarDocumentosOPIntegracion();
    }
    
    

    @Override 
    public ListarDocumentosVO listDocsINOperation(String idOferta) throws Exception{
        try {
            return integracion.listDocsINOperation(idOferta);
        } catch (Exception e) {
            throw new Exception(e);
        }

    }
}
