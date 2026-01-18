//LEER UN XML
println "\nLeer XML con XmlSlurper "
import groovy.xml.XmlSlurper 
// otra alternativa que da más performance es .XmlParser

String xml = ''' 
<note> 
    <to>Tove</to> 
    <from>Jani</from> 
    <country code='57'>
        <name>Colombia</name>
     </country>
     <country code='58'>
         <name>Chile</name>
     </country>
    <heading>Reminder</heading> 
    <body>Don't forget me this weekend!</body> 
</note> 
''' 
def object = new XmlSlurper().parseText(xml) 
println object.country[0].name

//Para acceder a un atributo del tag
println object.country[0].@code

//Si quiero traer todos los datos del nodo country
println object.country*.name
println object.country*.@code


//ESCRIBIR UN XML
println "\nEscribir XML con MarkupBuilder"
import groovy.xml.MarkupBuilder

def xml2 = new StringWriter() // donde vamos a escribir el XML
def builder = new MarkupBuilder(xml2)

builder.people(){
    person(lastName:"Mol", age:20){
        name("Sam")
        country("Finlandia") 
    }
    person(lastName:"San", age:14){
        name("Lau")
        country("Marruecos") 
    }
}

print xml2

println "\nEscribir XML con StreamingMarkupBuilder"
import groovy.xml.StreamingMarkupBuilder

def builder1 = new StreamingMarkupBuilder()

def xml3 = builder1.bind{
people(title:"People List"){
    person(lastName:"Mol", age:20){
        name("Sam")
        country("Finlandia") 
    }
    person(lastName:"San", age:14){
        name("Lau")
        country("Marruecos") 
    }
  }
}

print xml3

println "\nEjemplo de lectura de un XML que viene de un Link"

import groovy.util.*
import groovy.xml.*


URL feedXML = "https://www.w3schools.com/xml/cd_catalog.xml".toURL()

def feed = new XmlSlurper().parseText(feedXML.text)

feed.CD.each { entry ->
    println """
    Title:    ${entry.TITLE}
    Year:     ${entry.YEAR}
    Price:    ${entry.PRICE}
    """
}
