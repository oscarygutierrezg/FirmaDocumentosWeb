/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.cla.web.firma.mb;

import cl.cla.web.firma.constantes.Constantes;
import cl.cla.web.firma.controller.CheckinDocController;
import cl.cla.web.firma.controller.ConfirmFirmaController;
import cl.cla.web.firma.controller.DetalleRepController;
import cl.cla.web.firma.controller.FirmaElectronicaController;
import cl.cla.web.firma.controller.ListarDocumentosController;
import cl.cla.web.firma.controller.impl.CheckinDocControllerImpl;
import cl.cla.web.firma.controller.impl.ConfirmFirmaControllerImpl;
import cl.cla.web.firma.controller.impl.DetalleRepControllerImpl;
import cl.cla.web.firma.controller.impl.FirmaElectronicaControllerImpl;
import cl.cla.web.firma.controller.impl.ListarDocumentosControllerImpl;
import cl.cla.web.firma.vo.CheckinDocumentoVO;

import cl.cla.web.firma.vo.DetalleDocumentoVO;
import cl.cla.web.firma.vo.DocumentoVO;
import cl.cla.web.firma.vo.ListarDocumentosVO;
import cl.cla.web.firma.vo.ResponseVO;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author oscar
 */
public class MbFirma implements Serializable {

    private ListarDocumentosController documentosController;
    private DetalleRepController detalleRepController;
    private ConfirmFirmaController confirmFirmaController;
    private FirmaElectronicaController firmaElectronicaController;
    private CheckinDocController checkinDocController;

    private List<DocumentoVO> documentos;
    private boolean errorCargaDocumentos;
    private DetalleDocumentoVO detalleDocumentoVO;

    private String run;
    private String nombres;
    private String apellidos;
    private String genero;
    private String nacionalidad;
    private String fechaNacimiento;
    private String fechaVencimiento;
    private String numeroSerie;
    private String numeroTransaccion;
    private String resultadoTransaccion;
    private String ciVencida;
    private String tipoCedula;
    private String estadoInicioApplet;
    private String estadoRegistro;
    private String estadoCargaVista;
    private String ofertaEconomica;
    private String mensajeErrorFirmar;
    private String mensajeErrorListar;
    private String mensajeFirmados;
    private boolean mostrarFirmados;
    private boolean mostrarErrorListar;
    private boolean mostrarBotonOKErrorListar;
    private boolean mostrarErrorFirmar;
    private boolean mostrarErrorConsultarDetalle;

    private Properties properties;

    public MbFirma() {
        documentosController = new ListarDocumentosControllerImpl();
        detalleRepController = new DetalleRepControllerImpl();
        confirmFirmaController = new ConfirmFirmaControllerImpl();
        firmaElectronicaController = new FirmaElectronicaControllerImpl();
        checkinDocController = new CheckinDocControllerImpl();
        properties = new Properties();
        try {
            properties.load(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/properties/FirmaDocuementosWeb.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void consultarDocumentos() {
        System.out.println("consultarDocumentos");
        if (documentos != null) {
            documentos.clear();
        }
        mostrarErrorListar = false;
        mostrarBotonOKErrorListar = false;
        mostrarFirmados = false;
        mensajeErrorListar = "";
        mensajeFirmados = "";
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            return; // Skip ajax requests.
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        System.out.println("idOferta " + ofertaEconomica);
        if (ofertaEconomica == null || ofertaEconomica.isEmpty()) {
            mostrarErrorListar = true;
            mostrarBotonOKErrorListar = true;
            mensajeErrorListar = "El identificador de la oferta económica no fue ingresado como parámetro por la url. Debe ingresarla y refrescar";
            return;
        }

        
        try {

            ListarDocumentosVO listarDocumentosVO = documentosController.listDocsINOperation(ofertaEconomica);
            System.out.println("listarDocumentosVO " + listarDocumentosVO);
            if (listarDocumentosVO != null) {
                documentos = listarDocumentosVO.getDocumentos();
                System.out.println("documentos " + documentos.size());
                if (documentos.size() == 0) {
                    mensajeFirmados = "La oferta económica " + ofertaEconomica + " no tiene documentos asociados.";
                    mostrarFirmados = true;
                    return;
                }

                HttpSession session = (HttpSession) ec.getSession(true);
                String idActividad = (String) session.getAttribute("idActividad");
                System.out.println("idActividad " + idActividad);
                int contadorFirmados = 0;
                int contadorImposibleFirmar = 0;
                for (DocumentoVO documento : documentos) {
                    System.out.println("Antes getIdDocumento " + documento.getIdDocumento());
                    if (documento.getIdDocumento() == null || documento.getIdDocumento().isEmpty()) {
                        documento.setFlagFirmaDigital("I");
                    }
                    if (documento.getFlagFirmaDigital().compareTo("N") == 0) {
                        contadorFirmados++;
                    }
                    if (documento.getFlagFirmaDigital().compareTo("I") == 0) {
                        contadorImposibleFirmar++;
                    }
                    if (idActividad != null) {
                        if (documento.getIdActividad().compareTo(idActividad) == 0) {
                            documento.setSeleccionado(true);
                        }
                    }
                }
                if (contadorFirmados == documentos.size()) {
                    mensajeFirmados = "Todos los documentos asociados a la oferta económica " + ofertaEconomica + " estan firmados.";
                    mostrarFirmados = true;
                }
                if (contadorImposibleFirmar == documentos.size()) {
                    mensajeFirmados = "Ninguno de los documentos asociados a la oferta económica " + ofertaEconomica + " se pueden firmar debido a que no tienen archivo asociado.";
                    mostrarFirmados = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mensajeErrorListar = "Error interno al tratar de consultar los documentos asociados a la oferta económica " + ofertaEconomica + " \n ¿Confirma que desea realizar el reintento del proceso listar documentos?.";
            mostrarErrorListar = true;
        }
    }

    public String rollback() {
        System.out.println("rollback");
        return "index?faces-redirect=true&ofertaEconomica=" + ofertaEconomica;
    }

    public String firmar() {
        System.out.println("firmar");
        mostrarErrorFirmar = false;

        if (detalleDocumentoVO == null) {
            detalleDocumentoVO = buscarDetalleDocumento();
        }
        if (detalleDocumentoVO == null) {
            mostrarErrorFirmar = true;
            mensajeErrorFirmar = "Error interno no se ha podido detectar el docuemento que se va a firmar";
            return "index?faces-redirect=true&ofertaEconomica=" + ofertaEconomica;
        }

        boolean errorFirmar = false;
        try {
            ResponseVO responseVO = confirmFirmaController.confirmFirmaINOperation(detalleDocumentoVO);
            if (responseVO.getCodigo().compareTo(Constantes.codigoOK) != 0) {
                mensajeErrorFirmar = responseVO.getMensaje();
                errorFirmar = true;
            }
        } catch (Exception ex) {
            mensajeErrorFirmar = "Error interno el tratar de registrar la firma del documento " + detalleDocumentoVO.getNombre() + " en SIEBEL";
            errorFirmar = true;
        }

        if (errorFirmar) {
            mostrarErrorFirmar = true;
            return "index?faces-redirect=true&ofertaEconomica=" + ofertaEconomica;
        }

        errorFirmar = false;
        byte[] encoded = null;
        PDDocument doc = null;
        File pdfFile = null;
        try {
            pdfFile = new File(detalleDocumentoVO.getIdActividad() + ".pdf");
            FileUtils.writeByteArrayToFile(pdfFile, detalleDocumentoVO.getArchivo());
            doc = PDDocument.load(pdfFile);
            int count = doc.getNumberOfPages();
            encoded = firmaElectronicaController.firmarDocumento(count, numeroTransaccion, properties.getProperty("codigoSistema"), properties.getProperty("codigoAplicacion"), properties.getProperty("codigoTerminal"), detalleDocumentoVO.getArchivo());
        } catch (Exception ex) {
            mensajeErrorFirmar = "Error interno el tratar de agregar la firma electrónica al documento " + detalleDocumentoVO.getNombre() + " en SIEBEL";
            errorFirmar = true;
        } finally {
            try {
                if (doc != null) {
                    doc.close();
                }
                if (pdfFile != null) {
                    pdfFile.delete();
                }
            } catch (IOException ex) {
                Logger.getLogger(MbFirma.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (errorFirmar) {
            mostrarErrorFirmar = true;
            return "index?faces-redirect=true&ofertaEconomica=" + ofertaEconomica;
        }

        errorFirmar = false;
        try {
            CheckinDocumentoVO checkinDocumentoVO = generarCheckinDocumento(detalleDocumentoVO);
            checkinDocumentoVO.setArchivo(encoded);
            ResponseVO responseVO = checkinDocController.checkinDocINOperation(checkinDocumentoVO);
            if (responseVO.getCodigo().compareTo(Constantes.codigoOKWC) != 0) {
                mensajeErrorFirmar = responseVO.getMensaje();
                errorFirmar = true;
            }
        } catch (Exception ex) {
            mensajeErrorFirmar = "Error interno el tratar subir el documento " + detalleDocumentoVO.getNombre() + " al web content";
            errorFirmar = true;
        }

        if (errorFirmar) {
            mostrarErrorFirmar = true;
        }
        return "index?faces-redirect=true&ofertaEconomica=" + ofertaEconomica;
    }

    public void consultarDetalleDocumento(String idActividad,String flagFirma) {
        System.out.println("consultarDetalleDocumento");
        mostrarErrorConsultarDetalle = false;
        try {
            System.out.println("idActividad " + idActividad);
            System.out.println("flagFirma " + flagFirma);

            detalleDocumentoVO = detalleRepController.detalleRepINOperation(idActividad);

            if (detalleDocumentoVO.getResponse().getCodigo().compareTo(Constantes.codigoOK) != 0) {
                System.out.println(detalleDocumentoVO.getResponse().getMensaje());
                mostrarErrorConsultarDetalle = true;
                return;
            }
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            session.setAttribute("idActividad", idActividad);
            session.setAttribute("firmado", flagFirma);
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarErrorConsultarDetalle = true;
        }
    }
    
     public void consultarDetalleDocumentoI(String idActividad,String flagFirma) {
        System.out.println("consultarDetalleDocumentoI");
        mostrarErrorConsultarDetalle = false;
        try {
            System.out.println("idActividad " + idActividad);
            System.out.println("flagFirma " + flagFirma);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            session.setAttribute("idActividad", idActividad);
            session.setAttribute("flagFirma", flagFirma);
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarErrorConsultarDetalle = true;
        }
    }

    public boolean isMostrarErrorConsultarDetalle() {
        return mostrarErrorConsultarDetalle;
    }

    public void setMostrarErrorConsultarDetalle(boolean mostrarErrorConsultarDetalle) {
        this.mostrarErrorConsultarDetalle = mostrarErrorConsultarDetalle;
    }

    public boolean isMostrarFirmados() {
        return mostrarFirmados;
    }

    public void setMostrarFirmados(boolean mostrarFirmados) {
        this.mostrarFirmados = mostrarFirmados;
    }

    public String getMensajeFirmados() {
        return mensajeFirmados;
    }

    public void setMensajeFirmados(String mensajeFirmados) {
        this.mensajeFirmados = mensajeFirmados;
    }

    public ConfirmFirmaController getConfirmFirmaController() {
        return confirmFirmaController;
    }

    public void setConfirmFirmaController(ConfirmFirmaController confirmFirmaController) {
        this.confirmFirmaController = confirmFirmaController;
    }

    public String getMensajeErrorFirmar() {
        return mensajeErrorFirmar;
    }

    public void setMensajeErrorFirmar(String mensajeErrorFirmar) {
        this.mensajeErrorFirmar = mensajeErrorFirmar;
    }

    public String getMensajeErrorListar() {
        return mensajeErrorListar;
    }

    public void setMensajeErrorListar(String mensajeErrorListar) {
        this.mensajeErrorListar = mensajeErrorListar;
    }

    public boolean isMostrarErrorFirmar() {
        return mostrarErrorFirmar;
    }

    public void setMostrarErrorFirmar(boolean mostrarErrorFirmar) {
        this.mostrarErrorFirmar = mostrarErrorFirmar;
    }

    public boolean isMostrarErrorListar() {
        return mostrarErrorListar;
    }

    public void setMostrarErrorListar(boolean mostrarErrorListar) {
        this.mostrarErrorListar = mostrarErrorListar;
    }

    public boolean isMostrarBotonOKErrorListar() {
        return mostrarBotonOKErrorListar;
    }

    public void setMostrarBotonOKErrorListar(boolean mostrarBotonOKErrorListar) {
        this.mostrarBotonOKErrorListar = mostrarBotonOKErrorListar;
    }

    public boolean isErrorCargaDocumentos() {
        return errorCargaDocumentos;
    }

    public void setErrorCargaDocumentos(boolean errorCargaDocumentos) {
        this.errorCargaDocumentos = errorCargaDocumentos;
    }

    public String getOfertaEconomica() {
        return ofertaEconomica;
    }

    public void setOfertaEconomica(String ofertaEconomica) {
        this.ofertaEconomica = ofertaEconomica;
    }

    public List<DocumentoVO> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoVO> documentos) {
        this.documentos = documentos;
    }

    public DetalleDocumentoVO getDetalleDocumentoVO() {
        return detalleDocumentoVO;
    }

    public void setDetalleDocumentoVO(DetalleDocumentoVO detalleDocumentoVO) {
        this.detalleDocumentoVO = detalleDocumentoVO;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getNumeroTransaccion() {
        return numeroTransaccion;
    }

    public void setNumeroTransaccion(String numeroTransaccion) {
        this.numeroTransaccion = numeroTransaccion;
    }

    public String getResultadoTransaccion() {
        return resultadoTransaccion;
    }

    public void setResultadoTransaccion(String resultadoTransaccion) {
        this.resultadoTransaccion = resultadoTransaccion;
    }

    public String getCiVencida() {
        return ciVencida;
    }

    public void setCiVencida(String ciVencida) {
        this.ciVencida = ciVencida;
    }

    public String getTipoCedula() {
        return tipoCedula;
    }

    public void setTipoCedula(String tipoCedula) {
        this.tipoCedula = tipoCedula;
    }

    public String getEstadoInicioApplet() {
        return estadoInicioApplet;
    }

    public void setEstadoInicioApplet(String estadoInicioApplet) {
        this.estadoInicioApplet = estadoInicioApplet;
    }

    public String getEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(String estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }

    public String getEstadoCargaVista() {
        return estadoCargaVista;
    }

    public void setEstadoCargaVista(String estadoCargaVista) {
        this.estadoCargaVista = estadoCargaVista;
    }

    private DetalleDocumentoVO buscarDetalleDocumento() {
        System.out.println("buscarDetalleDocumento");
        for (DocumentoVO documento : documentos) {
            if (documento.isSeleccionado()) {
                try {
                    System.out.println("Actividad Seleccionada" + documento.getIdActividad());
                    return detalleRepController.detalleRepINOperation(documento.getIdActividad());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    private CheckinDocumentoVO generarCheckinDocumento(DetalleDocumentoVO detalleDocumentoVO) {
        CheckinDocumentoVO checkinDocumentoVO = new CheckinDocumentoVO();

        //Header
        checkinDocumentoVO.setCanal(properties.getProperty("canal"));
        checkinDocumentoVO.setUsuario(properties.getProperty("usuario"));
        checkinDocumentoVO.setIdTransaccion(detalleDocumentoVO.getIdActividad());

        //Documento>
        //InformacionCabecera>
        checkinDocumentoVO.setNombre(detalleDocumentoVO.getNombre());
        checkinDocumentoVO.setTipo(properties.getProperty("tipo"));
        checkinDocumentoVO.setGrupoSeguridad(properties.getProperty("grupoSeguridad"));
        checkinDocumentoVO.setCarpetaDestino(properties.getProperty("carpetaDestino"));
        checkinDocumentoVO.setFechaIngreso(new Date());
        //Metadata
        checkinDocumentoVO.setOfertaEconomica(ofertaEconomica);

        //Attachment    
        checkinDocumentoVO.setNombreArchivo(detalleDocumentoVO.getIdActividad() + ".pdf");
        checkinDocumentoVO.setArchivo(detalleDocumentoVO.getArchivo());
        return checkinDocumentoVO;
    }

}
