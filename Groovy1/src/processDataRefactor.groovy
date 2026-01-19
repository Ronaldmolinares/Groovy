import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.*;
import groovy.xml.*;
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

def Message processData(Message message) {

    def map = message.getProperties();

    def body = message.getBody(java.lang.String) as String;

    def responses = new XmlSlurper().parseText(body);

    def serviceManager0 = new Node(null, "root")

    def empJobs = responses.EmpJob.EmpJob

    empJobs.eachWithIndex { actual, index ->
        if (index == 0) {
            return //Termina la ejecución de la iteración actual, no sale del bucle.
        }

        def anterior = empJobs[index - 1]
        
        if (actual.eventReason.text().toUpperCase() in ["AU05", "CD31"]){
            crearNodo(serviceManager0, anterior, actual)
        } else {
            def camposNoVacios = !anterior.userNav.User.username.text().equals("") && !actual.startDate.text().equals("")
            def consistencia = actual.businessUnit.text() == anterior.businessUnit.text() && 
                actual.division.text() == anterior.division.text() &&
                actual.department.text() == anterior.department.text() &&
                actual.customString2.text() == anterior.customString2.text() &&
                actual.customString3.text() == anterior.customString3.text() &&
                actual.customString4.text() == anterior.customString4.text()
            def mismaPosition = actual.Position.text() == anterior.positionNav.Position.parentPosition.Position.code.text()
        
            if (camposNoVacios && !consistencia && !mismaPosition){
                crearNodo(serviceManager0, anterior, actual)  
            }
        }

    }

    body = groovy.xml.XmlUtil.serialize(serviceManager0);
    message.setBody(body);

    def messageLog = messageLogFactory.getMessageLog(message);
        map = message.getHeaders();
    
    return message;
}

def crearNodo(serviceManager0, anterior, actual){
    def serviceManager = new Node(serviceManager0, "ifsend");
    def ldt = LocalDateTime.parse (
        actual.startDate.text(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    )
            
    ldt = ldt.plusHours(5); 

    def fecha = ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    def hora = ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    def nombreCompleto = anterior.employmentNav.EmpEmployment.personNav.PerPerson.personalInfoNav.PerPersonal.formalName.text().split(" ");
    def primerNombre = nombreCompleto.size() > 0 ? nombreCompleto[0] : ""
    def segundoNombre = nombreCompleto.size() > 1 ? nombreCompleto[1] : ""
    def primerApellido = nombreCompleto.size() > 2 ? nombreCompleto[2] : ""
    def segundoApellido = nombreCompleto.size() > 3 ? nombreCompleto[3] : ""

    new Node(serviceManager, "Registroecopetrol", anterior.userNav.User.username.text().toUpperCase());
    new Node(serviceManager, "FechadeshabilitacionApps", fecha);
    new Node(serviceManager, "HoradeshabilitacionApps", hora);
    new Node(serviceManager, "PrimerNombre", primerNombre);
    new Node(serviceManager, "segundoNombre", segundoNombre);
    new Node(serviceManager, "PrimerApellido", primerApellido);
    new Node(serviceManager, "SegundoApellido", segundoApellido);
    new Node(serviceManager, "TituloPosiciones", "Cumple las condiciones de cambio de Unidad Organizativa y de jefe");
    
}