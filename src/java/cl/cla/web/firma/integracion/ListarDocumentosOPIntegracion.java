package cl.cla.web.firma.integracion;

import cl.cla.web.firma.vo.DocumentoVO;
import cl.cla.web.firma.vo.ListarDocumentosVO;
import cl.cla.web.firma.vo.ResponseVO;
import com.siebel.fins.OCSListadoDocumentosWS;
import com.siebel.fins.OcSListadoDeDocumentos;
import com.siebel.xml.ocs_20listado_20de_20reportes_20io.Action;
import com.siebel.xml.ocs_20listado_20de_20reportes_20io.ListOfOcsListadoDeReportesIoTopElmt;
import com.siebel.xml.ocs_20listado_20de_20reportes_20io.QuoteItem;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

public class ListarDocumentosOPIntegracion extends EndpointIntegracion {

    public ListarDocumentosVO listDocsINOperation(String idOferta) throws Exception {
        System.out.println("listDocsINOperation Inicio");
        System.out.println("idOferta " + idOferta);
        ListarDocumentosVO documentosVO = null;
        try {
            OcSListadoDeDocumentos service = new OcSListadoDeDocumentos();
            OCSListadoDocumentosWS port = service.getOCSListadoDocumentosWS();

            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> rc = bp.getRequestContext();
            rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("listar"));

            String quoteId = idOferta;
            Holder<String> ocsTemplateFirmaDigId = new Holder<String>();
            ocsTemplateFirmaDigId.value = "";
            Holder<String> errorSpcCode = new Holder<String>();

            Holder<String> errorSpcMessage = new Holder<String>();
            Holder<ListOfOcsListadoDeReportesIoTopElmt> responseSpcIO = new Holder<ListOfOcsListadoDeReportesIoTopElmt>();
            port.ocsSpcListadoSpcdeSpcDocumentosSpcWF(quoteId, ocsTemplateFirmaDigId, errorSpcCode, errorSpcMessage, responseSpcIO);

            documentosVO = new ListarDocumentosVO();
            System.out.println("Codigo " + errorSpcCode.value);
            System.out.println("Mensaje " + errorSpcMessage.value);
            System.out.println("Documentos " + responseSpcIO.value.getListOfOcsListadoDeReportesIo().getQuote().size());
            ResponseVO responseVO = new ResponseVO(errorSpcCode.value, errorSpcMessage.value);
            documentosVO.setResponse(responseVO);
            if (responseSpcIO.value.getListOfOcsListadoDeReportesIo().getQuote().size() > 0) {
                QuoteItem quoteItem = responseSpcIO.value.getListOfOcsListadoDeReportesIo().getQuote().get(0).getListOfQuoteItem().getQuoteItem().get(0);
                for (Action action : quoteItem.getListOfAction().getAction()) {
                    DocumentoVO documentoVO = new DocumentoVO();
                    documentoVO.setIdItemOfertaEconomica(quoteItem.getId());
                    documentoVO.setIdOfertaEconomicaPadre(quoteItem.getQuoteId());
                    documentoVO.setFlagFirmaDigital("Y");
                    //documentoVO.setFlagFirmaDigital(action.getOCSFirmaDigitalFlag());
                    documentoVO.setIdActividad(action.getId());
                    documentoVO.setIdDocumento(action.getOCSIdDocumento());
                    documentoVO.setIdItemOfertaEconomPadre(action.getParentActivityId());
                    documentoVO.setNombreDocumento(action.getDescription());
                    documentosVO.getDocumentos().add(documentoVO);
                    documentoVO.setId(documentosVO.getDocumentos().size());
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error procesando el servicio listar documentos");
        }
        System.out.println("listDocsINOperation Fin");
        return documentosVO;
    }

}
