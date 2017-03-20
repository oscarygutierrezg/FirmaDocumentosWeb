/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.integracion;


import cl.cla.web.firma.vo.DetalleDocumentoVO;
import cl.cla.web.firma.vo.ResponseVO;
import com.siebel.fins.OCSSpcConfirmacionSpcFirmaSpcDigitalSpcWF;
import com.siebel.fins.OcsConfirmarFirma;
import com.siebel.xml.ocs_20detalle_20documento_20io.Action;
import com.siebel.xml.ocs_20detalle_20documento_20io.ListOfAction;
import com.siebel.xml.ocs_20detalle_20documento_20io.ListOfOcsDetalleDocumentoIo;
import com.siebel.xml.ocs_20detalle_20documento_20io.ListOfOcsDetalleDocumentoIoTopElmt;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

/**
 *
 * @author oscar
 */
public class ConfirmFirmaOPIntegracion extends EndpointIntegracion{

    public ResponseVO confirmFirmaINOperation(DetalleDocumentoVO detalleDocumento) throws Exception {
        System.out.println("confirmFirmaINOperation Inicio");
        ResponseVO responseVO = new ResponseVO();
        try {
            OcsConfirmarFirma service = new OcsConfirmarFirma();
            OCSSpcConfirmacionSpcFirmaSpcDigitalSpcWF port = service.getOCSSpcConfirmacionSpcFirmaSpcDigitalSpcWF();

            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> rc = bp.getRequestContext();
            rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("firma"));

            ListOfOcsDetalleDocumentoIoTopElmt requestIO = new ListOfOcsDetalleDocumentoIoTopElmt();
            ListOfOcsDetalleDocumentoIo value = new ListOfOcsDetalleDocumentoIo();
            ListOfAction listOfAction = new ListOfAction();
            Action action = new Action();
            System.out.println("Actividad "+detalleDocumento.getIdActividad());
            action.setId(detalleDocumento.getIdActividad());
            action.setOCSIdDocumento(detalleDocumento.getIdDocumento());
            listOfAction.getAction().add(action);
            value.setListOfAction(listOfAction);
            requestIO.setListOfOcsDetalleDocumentoIo(value);
            Holder<String> errorSpcCode = new Holder<String>();
            Holder<String> errorSpcMessage = new Holder<String>();
            Holder<String> executionStatus = new Holder<String>();

            port.ocsSpcConfirmacionSpcFirmaSpcDigitalSpcWF(requestIO, errorSpcCode, errorSpcMessage, executionStatus);
            System.out.println("Codigo " + errorSpcCode.value);
            System.out.println("Mensaje " + errorSpcMessage.value);
            responseVO = new ResponseVO(errorSpcCode.value, errorSpcMessage.value);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error procesando el servicio firmar documento");
        }
        System.out.println("confirmFirmaINOperation Fin");
        return responseVO;
    }

}
