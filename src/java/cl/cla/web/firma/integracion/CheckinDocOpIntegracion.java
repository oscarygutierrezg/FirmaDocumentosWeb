package cl.cla.web.firma.integracion;

import cl.cla.osb.CheckinDocOp;
import cl.cla.osb.CheckinDocOp_Service;
import cl.cla.osb.RequestEAI118CheckinDocumento;
import cl.cla.osb.ResponseEAI118CheckinDocumento;
import cl.cla.web.firma.util.DateUtil;
import cl.cla.web.firma.vo.CheckinDocumentoVO;
import cl.cla.web.firma.vo.ResponseVO;
import java.util.Date;
import java.util.Map;
import javax.xml.ws.BindingProvider;

public class CheckinDocOpIntegracion extends EndpointIntegracion{    

    public ResponseVO checkinDocINOperation(CheckinDocumentoVO checkinDocumento) throws Exception {
        System.out.println("checkinDocINOperation Inicio");
        ResponseVO responseVO = new ResponseVO();
        try {
            CheckinDocOp_Service service = new CheckinDocOp_Service();
            CheckinDocOp port = service.getCheckinDocOpPort();

            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> rc = bp.getRequestContext();
            rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("checkin"));

            RequestEAI118CheckinDocumento request = new RequestEAI118CheckinDocumento();
           
            RequestEAI118CheckinDocumento.Header header = new RequestEAI118CheckinDocumento.Header();
            header.setCanal(checkinDocumento.getCanal());
            header.setFechaTransaccion(DateUtil.dateToxmlGregorianCalendar(new Date()));
            header.setIdTransaccion(checkinDocumento.getIdTransaccion());
            header.setUsuario(checkinDocumento.getUsuario());
            
            RequestEAI118CheckinDocumento.Documento.InformacionCabecera informacionCabecera = new RequestEAI118CheckinDocumento.Documento.InformacionCabecera();
            informacionCabecera.setCarpetaDestino(checkinDocumento.getCarpetaDestino() != null ? checkinDocumento.getCarpetaDestino() : "");
            informacionCabecera.setFechaIngreso(DateUtil.dateToxmlGregorianCalendar(checkinDocumento.getFechaIngreso() != null ? checkinDocumento.getFechaIngreso() : new Date()));
            informacionCabecera.setGrupoSeguridad(checkinDocumento.getGrupoSeguridad() != null ? checkinDocumento.getGrupoSeguridad() : "");
            informacionCabecera.setNombre(checkinDocumento.getNombre() != null ? checkinDocumento.getNombre() : "");
            informacionCabecera.setTipo(checkinDocumento.getTipo() != null ? checkinDocumento.getTipo() : "");
           
            
            RequestEAI118CheckinDocumento.Documento.Metadata metadata = new RequestEAI118CheckinDocumento.Documento.Metadata();
            metadata.setOfertaEconomica(checkinDocumento.getOfertaEconomica());            
           
            RequestEAI118CheckinDocumento.Documento.Attachment attachment = new RequestEAI118CheckinDocumento.Documento.Attachment();
            attachment.setArchivo(checkinDocumento.getArchivo());
            attachment.setNombre(checkinDocumento.getNombreArchivo());
            
            RequestEAI118CheckinDocumento.Documento documento = new RequestEAI118CheckinDocumento.Documento();
            documento.setAttachment(attachment);
            documento.setInformacionCabecera(informacionCabecera);
            documento.setMetadata(metadata);
            
            request.setHeader(header);
            request.setDocumento(documento);
            ResponseEAI118CheckinDocumento response = port.checkinDocINOperation(request);

            if (response != null && response.getEstado() != null) {
                System.out.println("Codigo " + response.getEstado().getCodigo());
                System.out.println("Mensaje " + response.getEstado().getMensaje());
                responseVO = new ResponseVO(response.getEstado().getCodigo(), response.getEstado().getMensaje());

            } else {
                throw new Exception("Error invocando el servicio guardar documento");
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error procesando el servicio guardar documento");
        }
        System.out.println("checkinDocINOperation Fin");
        return responseVO;
    }

    public static void main(String[] args) throws Exception {
        CheckinDocumentoVO checkinDocumentoVO = new CheckinDocumentoVO();

        checkinDocumentoVO.setNombre("Prueba_31");
        checkinDocumentoVO.setTipo("Otros");
        checkinDocumentoVO.setGrupoSeguridad("Oferta");
        checkinDocumentoVO.setCarpetaDestino("Oferta");
        checkinDocumentoVO.setFechaIngreso(new Date());

        checkinDocumentoVO.setOfertaEconomica("1-1NYEM");
        checkinDocumentoVO.setRutPersona(13746583);
        checkinDocumentoVO.setDvPersona("5");
        checkinDocumentoVO.setSucursalCodigo("009");
        checkinDocumentoVO.setFolio("003");
        checkinDocumentoVO.setNumeroDocumento("001");
        checkinDocumentoVO.setNombreArchivo("Reporte_Prueba_31.txt");
        checkinDocumentoVO.setArchivo(new byte[1000]);

        CheckinDocOpIntegracion cdoi = new CheckinDocOpIntegracion();
        cdoi.checkinDocINOperation(checkinDocumentoVO);

    }

}
