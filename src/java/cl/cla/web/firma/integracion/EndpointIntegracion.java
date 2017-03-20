/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.integracion;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author amunozme
 */
public class EndpointIntegracion {

    protected Properties properties;

    public EndpointIntegracion() {
        properties = new Properties();
        try {
            properties.load(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/properties/wsdlEndpoint.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
