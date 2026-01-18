import groovy.xml.XmlSlurper 
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