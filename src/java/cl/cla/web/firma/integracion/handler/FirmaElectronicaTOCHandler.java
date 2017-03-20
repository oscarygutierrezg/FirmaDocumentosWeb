/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.integracion.handler;

import cl.cla.esb.enterpriseobjects.cclamwclients.v1.CCLAMWClientsType;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author amunozme
 */
public class FirmaElectronicaTOCHandler implements SOAPHandler<SOAPMessageContext> {

    private String codigoDelSistema;
    private String codigoDeLaAplicacion;
    private String codigoDelTerminal;

    public FirmaElectronicaTOCHandler(String codigoDelSistema, String codigoDeLaAplicacion, String codigoDelTerminal) {
        this.codigoDelSistema = codigoDelSistema;
        this.codigoDeLaAplicacion = codigoDeLaAplicacion;
        this.codigoDelTerminal = codigoDelTerminal;
    }

    private String getMessageEncoding(SOAPMessage msg) throws SOAPException {
        String encoding = "utf-8";
        if (msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING) != null) {
            encoding = msg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING).toString();
        }
        return encoding;

    }

    private void dumpSOAPMessage(SOAPMessage msg) {
        if (msg == null) {
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            msg.writeTo(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<QName> getHeaders() {
        return new HashSet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        try {
            SOAPMessage message = context.getMessage();
            SOAPHeader header = message.getSOAPHeader();
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            if (header == null) {
                header = envelope.addHeader();
            }
            SOAPElement el = header.addChildElement(envelope.createName("CCLAMWClients", "v1", "http://esb.cla.cl/EnterpriseObjects/CCLAMWClients/V1"));

            SOAPElement codigoDelSistema = el.addChildElement("CodigoDelSistema","v1");
            codigoDelSistema.addTextNode(this.codigoDelSistema);
            SOAPElement codigoDeLaAplicacion = el.addChildElement("CodigoDeLaAplicacion","v1");
            codigoDeLaAplicacion.addTextNode(this.codigoDeLaAplicacion);
            SOAPElement codigoDelTerminal = el.addChildElement("CodigoDelTerminal","v1");
            codigoDelTerminal.addTextNode(this.codigoDelTerminal);

            message.saveChanges();
            dumpSOAPMessage(message);

        } catch (SOAPException  e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }
}
