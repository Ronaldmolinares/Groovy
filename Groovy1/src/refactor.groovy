import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.*;
import groovy.xml.*;
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

"""
1. Declaramos la firma del método, retorna un objeto Message, recibe como parametro un objeto del mismo tipo.
Es el punto de entrada del Groovy Script en SAP CPI, y es invocado automáticamente por la plataforma durante el procesamiento del mensaje.
"""

def Message processData(Message message) {

    """
    Se obtienen las propiedades del mensaje (Exchange Properties).
    Se declara map que es de tipo java.util.Map<String, Object>.
    """
    def map = message.getProperties();

    """
    Se obtiene el cuerpo del message y se fuerza a ser de tipo String
    para que XmlSlurper pueda parsearlo.
    De manera que body ya contiene todo el XML como String.
    """
    def body = message.getBody(java.lang.String) as String;

    """
    Se crea la variable para manipular el XML, teniendo un árbol de nodos navegable. (GPathResult)
    """
    def responses = new XmlSlurper().parseText(body);

    """
    Se define el XML de salida, inicialmente vacío.
        null -> no tiene nodo padre.
        root -> etiqueta raíz del XML de salida.
    """
    def serviceManager0 = new Node(null, "root")

    """
    Se definen los estados anterior y nuevo, ya que el XML
    contiene exactamente dos nodos EmpJob"""
    def anterior = responses.EmpJob.EmpJob[0]
    def nuevo = responses.EmpJob.EmpJob[1]

    /*
    Aplicamos reglas del negocio para generar el XML de salida.
    1. Si en el estado nuevo en el tag eventReason se encuentra el codigo "AU05" o "CD31" 
    entonces va a agregar un nodo hijo con el nombre ifsend al XML de salida.

    2. Toma la fecha y modifica el formato.

    3. Añade data al nodo hijo.

    4. Si no entonces Si el nombre y la fecha no estan vacios entonces
    si hay consistencia entre los estados anterior y nuevo. Si la 
    posición es diferente entonces. 
    */
    if (nuevo.eventReason.text().toUpperCase() in ["AU05", "CD31"]){
        crearNodo(serviceManager0, anterior, nuevo)
    } else {
        def camposNoVacios = !anterior.userNav.User.username.text().equals("") && !nuevo.startDate.text().equals("")
        def consistencia = nuevo.businessUnit.text() == anterior.businessUnit.text() && 
                nuevo.division.text() == anterior.division.text() &&
                nuevo.department.text() == anterior.department.text() &&
                nuevo.customString2.text() == anterior.customString2.text() &&
                nuevo.customString3.text() == anterior.customString3.text() &&
                nuevo.customString4.text() == anterior.customString4.text()
        def mismaPosition = nuevo.Position.text() == anterior.positionNav.Position.parentPosition.Position.code.text()
        
        if (camposNoVacios && !consistencia && !mismaPosition){
            crearNodo(serviceManager0, anterior, nuevo)  
        }
    }
    
    """
    1. Convierte el XML construido en memoria a texto.
    El cuerpo se serializa en el XML de salida.
    
    2. Reemplaza el payload del mensaje.
    El mensaje toma el valor del XML construido en el script
    """
    body = groovy.xml.XmlUtil.serialize(serviceManager0);
    message.setBody(body);

    """
    3. Permite loguear el resultado.
    Permite escribir información en el Message Processing Log de SAP CPI
    Despues obtiene las cabeceras del mensaje (no influye en el script)
    """

    def messageLog = messageLogFactory.getMessageLog(message);
        map = message.getHeaders();
    
    """
    4. Se retorna el Message a CPI para continuar el flujo.
    Este contiene body modificado, headers y properties intactas.
    """
    return message;
}

def crearNodo(serviceManager0, anterior, nuevo){
    def serviceManager = new Node(serviceManager0, "ifsend");
    def ldt = LocalDateTime.parse (
        nuevo.startDate.text(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    )
            
    ldt = ldt.plusHours(5); 

    def fecha = ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    def hora = ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    def nombreCompleto = anterior.employmentNav.EmpEmployment.personNav.PerPerson.personalInfoNav.PerPersonal.formalName.text().split(" ");
    def primerNombre = nombreCompleto[0];
    def segundoNombre = nombreCompleto[1];
    def primerApellido = nombreCompleto[2];
    def segundoApellido = nombreCompleto[3];

    new Node(serviceManager, "Registroecopetrol", anterior.userNav.User.username.text().toUpperCase());
    new Node(serviceManager, "FechadeshabilitacionApps", fecha);
    new Node(serviceManager, "HoradeshabilitacionApps", hora);
    new Node(serviceManager, "PrimerNombre", primerNombre);
    new Node(serviceManager, "segundoNombre", segundoNombre);
    new Node(serviceManager, "PrimerApellido", primerApellido);
    new Node(serviceManager, "SegundoApellido", segundoApellido);
    new Node(serviceManager, "TituloPosiciones", "Cumple las condiciones de cambio de Unidad Organizativa y de jefe");
    
}