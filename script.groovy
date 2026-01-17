
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import groovy.xml.XmlUtil;
import groovy.util.*;
import groovy.xml.*;
import java.util.Set;

def Message processData(Message message) {
map = message.getProperties();


def body = message.getBody(java.lang.String) as String;



 def Responses  = new XmlSlurper().parseText(body);
 def serviceManager0 = new Node(null, "root");
 


 Responses.EmpJob[0].each{per->
            if ( per.EmpJob[1].eventReason.text().toUpperCase().equals("AU05") || per.EmpJob[1].eventReason.text().toUpperCase().equals("CD31")){ 
                def serviceManager = new Node( serviceManager0, "ifsend" );
                    
                    def fecha2 = per.EmpJob[1].startDate.text().replace("T"," ").replace(".000","").replace("-","/");
    
                    new Node(serviceManager, "Registroecopetrol", per.EmpJob[0].userNav.User.username.text().toUpperCase());
                    new Node(serviceManager, "FechadeshabilitacionApps", fecha2.replace("00:00:00","05:00:00"));
                    new Node(serviceManager, "NombreCompleto", per.EmpJob[0].employmentNav.EmpEmployment.personNav.PerPerson.personalInfoNav.PerPersonal.formalName.text());
                    new Node(serviceManager, "TituloPosiciones", "Cumple las condiciones de cambio de Unidad Organizativa y de jefe");
            }
            else{
            if ( !per.EmpJob[0].userNav.User.username.text().equals("") && !per.EmpJob[1].startDate.text().equals("")){ 
                if ( per.EmpJob[1].businessUnit.text() == per.EmpJob[0].businessUnit.text() && per.EmpJob[1].division.text() == per.EmpJob[0].division.text() && per.EmpJob[1].department.text() == per.EmpJob[0].department.text() && per.EmpJob[1].customString2.text() == per.EmpJob[0].customString2.text() && per.EmpJob[1].customString3.text() == per.EmpJob[0].customString3.text() && per.EmpJob[1].customString4.text() == per.EmpJob[0].customString4.text()){ }
                else{
                    
                    if ( per.EmpJob[1].Position.text() == per.EmpJob[0].positionNav.Position.parentPosition.Position.code.text()){ }
                    else{
                    def serviceManager = new Node( serviceManager0, "ifsend" );
                    
                    def fecha2 = per.EmpJob[1].startDate.text().replace("T"," ").replace(".000","").replace("-","/");
    
                    new Node(serviceManager, "Registroecopetrol", per.EmpJob[0].userNav.User.username.text().toUpperCase());
                    new Node(serviceManager, "FechadeshabilitacionApps", fecha2.replace("00:00:00","05:00:00"));
                    new Node(serviceManager, "NombreCompleto", per.EmpJob[0].employmentNav.EmpEmployment.personNav.PerPerson.personalInfoNav.PerPersonal.formalName.text());
                    new Node(serviceManager, "TituloPosiciones", "Cumple las condiciones de cambio de Unidad Organizativa y de jefe");
                    //new Node(serviceManager, "TituloPosiciones", per.EmpJob[1].jobTitle.text()+"("+per.EmpJob[0].jobTitle.text()+")");
                    }
                    
                    
                    /*
                    new Node(serviceManager, "Nivel1", per.EmpJob[1].businessUnit.text());
                    new Node(serviceManager, "ANivel1", per.EmpJob[0].businessUnit.text());
                    new Node(serviceManager, "Nivel2", per.EmpJob[1].division.text());
                    new Node(serviceManager, "ANivel2", per.EmpJob[0].division.text());
                    new Node(serviceManager, "Nivel3", per.EmpJob[1].department.text());
                    new Node(serviceManager, "ANivel3", per.EmpJob[0].department.text());
                    new Node(serviceManager, "Nivel4", per.EmpJob[1].customString2.text());
                    new Node(serviceManager, "ANivel4", per.EmpJob[0].customString2.text());
                    new Node(serviceManager, "Nivel5", per.EmpJob[1].customString3.text());
                    new Node(serviceManager, "ANivel5", per.EmpJob[0].customString3.text());
                    new Node(serviceManager, "Nivel6", per.EmpJob[1].customString4.text());
                    new Node(serviceManager, "ANivel6", per.EmpJob[0].customString4.text());*/

            
         }}}}

body = groovy.xml.XmlUtil.serialize(serviceManager0);
message.setBody(body);

def messageLog = messageLogFactory.getMessageLog(message);
    map = message.getHeaders();
    //messageLog.addAttachmentAsString("2.5 - output log", body, "text/plain");

return message;

}