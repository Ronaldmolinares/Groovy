import groovy.xml.XmlSlurper 
import groovy.xml.MarkupBuilder

//Leer el XML
String xmlPath = "c:/Users/Samir/Documents/practica/Groovy1/in/input.xml"

def data = new XmlSlurper().parse(new File(xmlPath))
def out = new Node(null,"root") //xml de salida

println data.EmpJob.EmpJob[0].userNav.User.username.text()
println data.EmpJob.EmpJob[1].username.text()


// data.EmpJob.[0].each{ per ->
    

// }

// //Escribir el XML
// def output = new StringWriter()
// def builder = new MarkupBuilder(output)

// builder.root(){
//     ifsend(){
//         Registroecopetrol("")
//         FechadeshabilitacionApps()
//         NombreCompleto("")
//         TituloPosiciones("")
//     }
// }

// print output