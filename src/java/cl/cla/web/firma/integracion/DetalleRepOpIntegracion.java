package cl.cla.web.firma.integracion;


import cl.cla.web.firma.vo.DetalleDocumentoVO;
import cl.cla.web.firma.vo.ResponseVO;
import com.siebel.fins.OCSDetalledeDocumentosWS;
import com.siebel.fins.OcsDetalleDocumento;
import com.siebel.xml.ocs_20detalle_20documento_20io.Action;
import com.siebel.xml.ocs_20detalle_20documento_20io.ListOfOcsDetalleDocumentoIoTopElmt;
import com.siebel.xml.ocs_20detalle_20documento_20io.ReportOutputBC;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.faces.context.FacesContext;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

public class DetalleRepOpIntegracion{

      private String endPoint;

    public DetalleRepOpIntegracion() {
        Properties properties = new Properties();
        try {
            properties.load(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/properties/wsdlEndpoint.properties"));
            endPoint=properties.getProperty("detalle");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public DetalleRepOpIntegracion(String endPoint) {
        this.endPoint=endPoint;
    
    }

    public DetalleDocumentoVO detalleRepINOperation(String idActividad) throws Exception, Exception {
        System.out.println("detalleRepINOperation Inicio");
        DetalleDocumentoVO detalleDocumento = null;
        try {
            OcsDetalleDocumento service = new OcsDetalleDocumento();
            OCSDetalledeDocumentosWS port = service.getOCSDetalledeDocumentosWS();

            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> rc = bp.getRequestContext();
            rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint);
            System.out.println("idActividad " + idActividad);
            String actionId = idActividad;
            Holder<String> errorCode = new Holder<String>();

            Holder<String> errorMessage = new Holder<String>();
            Holder<ListOfOcsDetalleDocumentoIoTopElmt> responseSpcIO = new Holder<ListOfOcsDetalleDocumentoIoTopElmt>();
            port.ocsSpcDetalleSpcdeSpcDocumentosSpcWF(actionId, errorCode, errorMessage, responseSpcIO);

            detalleDocumento = new DetalleDocumentoVO();
            Action action = responseSpcIO.value.getListOfOcsDetalleDocumentoIo().getListOfAction().getAction().get(0);
            ReportOutputBC reportOutputBC = action.getListOfReportOutputBC().getReportOutputBC().get(0);
            detalleDocumento.setArchivo(reportOutputBC.getReportOutputFileBuffer());
            detalleDocumento.setExtension(reportOutputBC.getReportOutputFileExt());

            detalleDocumento.setId(reportOutputBC.getId());
            detalleDocumento.setIdActividad(action.getId());
            detalleDocumento.setIdDocumento(action.getOCSIdDocumento());
            detalleDocumento.setNombre(reportOutputBC.getReportName());
            detalleDocumento.setTamano(reportOutputBC.getReportOutputFileSize());
            System.out.println("Codigo " + errorCode.value);
            System.out.println("Mensaje " + errorMessage.value);
            ResponseVO responseVO = new ResponseVO(errorCode.value, errorMessage.value);
            detalleDocumento.setResponse(responseVO);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error procesando el servicio consultar detalle documento");
        }
        System.out.println("detalleRepINOperation Fin");
        return detalleDocumento;

    }
}
