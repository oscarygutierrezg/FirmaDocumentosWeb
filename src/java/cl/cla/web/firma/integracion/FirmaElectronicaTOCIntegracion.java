package cl.cla.web.firma.integracion;

import cl.cla.esb.enterpriseobjects.coordenadascartesianas.v1.CoordenadasCartesianasType;
import cl.cla.esb.enterpriseobjects.ubicacionendocumento.v1.UbicacionEnDocumentoMedianteCoordenadasType;
import cl.cla.esb.enterpriseservices.businessfirmaelectronicatocexternalservice.firmardocumento.v1.FirmarDocumentoInputType;
import cl.cla.esb.enterpriseservices.businessfirmaelectronicatocexternalservice.firmardocumento.v1.FirmarDocumentoInputType.EstampaList.Estampa;
import cl.cla.esb.enterpriseservices.businessfirmaelectronicatocexternalservice.firmardocumento.v1.FirmarDocumentoOutputType;
import cl.cla.esb.enterpriseservices.businessfirmaelectronicatocexternalservice.v1.BusinessFirmaElectronicaTOCExternalService;
import cl.cla.esb.enterpriseservices.businessfirmaelectronicatocexternalservice.v1.BusinessFirmaElectronicaTOCExternalServicePortType;
import cl.cla.web.firma.integracion.handler.FirmaElectronicaTOCHandler;
import cl.cla.web.firma.servlet.ServletDetalleDocumentoPdf;
import cl.cla.web.firma.vo.FirmaElectronicaVO;
import cl.cla.web.firma.vo.UbicacionVO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import org.apache.commons.codec.binary.Base64;

public class FirmaElectronicaTOCIntegracion extends EndpointIntegracion {

    public byte[] firmarDocumento(FirmaElectronicaVO firmaElectronica) throws Exception {
        System.out.println("firmarDocumento Inicio");
        try {
            BusinessFirmaElectronicaTOCExternalService service = new BusinessFirmaElectronicaTOCExternalService();
            BusinessFirmaElectronicaTOCExternalServicePortType port = service.getBusinessFirmaElectronicaTOCExternalServiceSOAP11InLineAttachmentPort();

            Binding binding = ((BindingProvider) port).getBinding();
            List<javax.xml.ws.handler.Handler> handlerChain = binding.getHandlerChain();
            handlerChain.add(new FirmaElectronicaTOCHandler(firmaElectronica.getCodigoSistema(), firmaElectronica.getCodigoAplicacion(), firmaElectronica.getCodigoTerminal()));
            binding.setHandlerChain(handlerChain);

            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> rc = bp.getRequestContext();
            rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("firmaElectronica"));

            FirmarDocumentoInputType request = new FirmarDocumentoInputType();

            UbicacionEnDocumentoMedianteCoordenadasType ubicacionDelSello = new UbicacionEnDocumentoMedianteCoordenadasType();
            CoordenadasCartesianasType cartesianasSello = new CoordenadasCartesianasType();
            cartesianasSello.setX(BigInteger.valueOf(firmaElectronica.getUbicacionSello().getX()));
            cartesianasSello.setY(BigInteger.valueOf(firmaElectronica.getUbicacionSello().getY()));
            ubicacionDelSello.setCoordenadasCartesianas(cartesianasSello);
            ubicacionDelSello.setNumeroDePagina(BigInteger.valueOf(firmaElectronica.getUbicacionSello().getNumeroDePagina()));

            FirmarDocumentoInputType.EstampaList estampaList = new FirmarDocumentoInputType.EstampaList();
            Estampa estampa = new Estampa();
            UbicacionEnDocumentoMedianteCoordenadasType ubicacionEstampa = new UbicacionEnDocumentoMedianteCoordenadasType();
            CoordenadasCartesianasType cartesianasEstampa = new CoordenadasCartesianasType();
            cartesianasEstampa.setX(BigInteger.valueOf(firmaElectronica.getUbicacionEstampa().getX()));
            cartesianasEstampa.setY(BigInteger.valueOf(firmaElectronica.getUbicacionEstampa().getY()));
            ubicacionEstampa.setCoordenadasCartesianas(cartesianasEstampa);
            ubicacionEstampa.setNumeroDePagina(BigInteger.valueOf(firmaElectronica.getUbicacionEstampa().getNumeroDePagina()));
            estampa.setUbicacion(ubicacionEstampa);
            System.out.println("codigoDeTransaccion "+firmaElectronica.getCodigoDeTransaccion());
            estampa.setCodigoDeTransaccionDeVerificacionDeIdentidadTOC(firmaElectronica.getCodigoDeTransaccion());
            estampaList.getEstampa().add(estampa);

            request.setDocumento(firmaElectronica.getArchivo());
            request.setEstampaList(estampaList);
            request.setUbicacionDelSelloCriptograficoMedianteCoordenadas(ubicacionDelSello);

            FirmarDocumentoOutputType response = port.firmarDocumento(request);
            if (response != null && response.getDocumento() != null) {
                System.out.println("firmarDocumento Fin");
                return response.getDocumento();
            } else {
                throw new Exception("Error invocando el servicio de firma electrònica de documento");
            }

        } catch (Exception exception) {
            System.out.println("firmarDocumento Fin Error");
            exception.printStackTrace();
            throw new Exception("Error invocando el servicio de firma electrònica de documento");
        }
    }

    private static String generarBase64(String nombreArchivo) {

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(nombreArchivo);
            br = new BufferedReader(fr);
            br = new BufferedReader(new FileReader(nombreArchivo));
            return br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ServletDetalleDocumentoPdf.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServletDetalleDocumentoPdf.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        String base64File = generarBase64("C:\\pdf\\c.txt");

        byte[] encoded = Base64.decodeBase64(base64File);

        UbicacionVO ubicacionSello = new UbicacionVO();
        ubicacionSello.setNumeroDePagina(1);
        ubicacionSello.setX(100);
        ubicacionSello.setY(100);
        UbicacionVO ubicacionEstampa = new UbicacionVO();
        ubicacionEstampa.setNumeroDePagina(1);
        ubicacionEstampa.setX(300);
        ubicacionEstampa.setY(100);

        FirmaElectronicaVO firmaElectronica = new FirmaElectronicaVO();
        firmaElectronica.setArchivo(encoded);
        firmaElectronica.setUbicacionEstampa(ubicacionEstampa);
        firmaElectronica.setUbicacionSello(ubicacionSello);
        firmaElectronica.setCodigoDeTransaccion("2409728-C");
        FirmaElectronicaTOCIntegracion docOpIntegracion = new FirmaElectronicaTOCIntegracion();
        docOpIntegracion.firmarDocumento(firmaElectronica);
    }

}
