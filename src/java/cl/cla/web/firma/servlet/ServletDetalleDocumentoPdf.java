/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.servlet;

/**
 *
 * @author amunozme
 */
import cl.cla.web.firma.controller.impl.DetalleRepControllerImpl;
import cl.cla.web.firma.controller.DetalleRepController;
import cl.cla.web.firma.vo.DetalleDocumentoVO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class ServletDetalleDocumentoPdf extends HttpServlet {

    private DetalleRepController detalleRepController;
    private Properties properties;

    public void init() {
        properties = new Properties();
        try {
            properties.load(getServletContext().getResourceAsStream("/WEB-INF/properties/wsdlEndpoint.properties"));
        } catch (IOException ex) {
            Logger.getLogger(ServletDetalleDocumentoPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        detalleRepController = new DetalleRepControllerImpl(properties.getProperty("detalle"));

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        generarPdf(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        generarPdf(request, response);
    }

    private void generarPdf(HttpServletRequest request, HttpServletResponse response) {
        File pdfFile = null;
        FileInputStream fileInputStream = null;
        OutputStream responseOutputStream = null;
        try {
            String actividad = request.getParameter("actividad") != null ? request.getParameter("actividad") : "";
            System.out.println("idActividad " + actividad);
            byte[] encoded = null;
            if (actividad != null && !actividad.isEmpty()) {
                try {
                    DetalleDocumentoVO detalleDocumentoVO = detalleRepController.detalleRepINOperation(actividad);
                    encoded = detalleDocumentoVO.getArchivo();
                } catch (Exception ex) {
                    encoded = Base64.decodeBase64(generarBase64Vacio());
                }
            } else {
                encoded = Base64.decodeBase64(generarBase64Vacio());
            }

            pdfFile = new File(actividad + ".pdf");
            FileUtils.writeByteArrayToFile(pdfFile, encoded);
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "inline; filename="+actividad+".pdf");
            response.setContentLength((int) pdfFile.length());

            fileInputStream = new FileInputStream(pdfFile);
            responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (responseOutputStream != null) {
                    responseOutputStream.close();
                }
                if (pdfFile != null) {
                    pdfFile.delete();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private String generarPdfVacio() {
        Properties prop = new Properties();
        try {
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/properties/servlet.properties"));
            return prop.getProperty("pdfVacio");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    private String generarBase64Vacio() {
        Properties prop = new Properties();
        try {
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/properties/servlet.properties"));
            return prop.getProperty("pdfVacio");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
