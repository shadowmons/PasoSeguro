package com.tesisuc.dv.pasoseguro.Procesos;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Created by 46465442z on 24/02/16.
 */
public class Correo {

    // Propiedades del cliente de correo
    private Session session;         // Sesion de correo
    private Properties properties;   // Propiedades de la sesion
    private Transport transport;     // Envio del correo
    private MimeMessage mensaje;     // Mensaje que enviaremos

    // Credenciales de usuario
    private String direccionCorreo = "tesisucdv@gmail.com";   // Dirección de correo
    private String contrasenyaCorreo = "tesisucdv2019";       // Contraseña

    // Correo al que enviaremos el mensaje
    private String destintatarioCorreo;
    private String nombre;
    private String latitud;
    private String longitud;

    public Correo(String destino, String nombre, String latitud, String longitud) throws MessagingException {
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
        destintatarioCorreo = destino;
        // Ajustamos primero las properties

        properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        //Configuramos la sesión
        session = Session.getDefaultInstance(properties, null);


    }

    public void enviarMensaje(String subject) throws MessagingException {

        // Configuramos los valores de nuestro mensaje
        mensaje = new MimeMessage(session);
        mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destintatarioCorreo));
        mensaje.setSubject(subject);
        mensaje.setContent(contenido(nombre, latitud, longitud), "text/html");

        // Configuramos como sera el envio del correo
        transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", direccionCorreo, contrasenyaCorreo);
        transport.sendMessage(mensaje, mensaje.getAllRecipients());
        transport.close();

        // Mostramos que el mensaje se ha enviado correctamente
        System.out.println("--------------------------");
        System.out.println("Mensaje enviado");
        System.out.println("---------------------------");
    }

    public String contenido(String nombre, String latitud, String longitud) {
        String mensaje = "";
        if (latitud != null && longitud != null) {
            mensaje =
                        "<td><p style = \" margin: 0%\">Se ha detectado un uso no autorizado sobre el dispositivo. Las últimas coordenadas conocidas del dispositivo son:</p></td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td><p style = \" margin: 0%\"><strong>Latitud: " + latitud + "</strong> </p></td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td><p style = \" margin: 0%\"><strong>Longitud: " + longitud + "</strong> </p></td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td>Puede tener mas detalles de la ubicación si ingresa en:</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td> <a href=\"https://www.google.com/maps/search/?api=1&query= " + latitud + ", " + longitud + "\"><button class = \"btn procesar\">Ubicar en Google Maps</button></a></td>" +
                    "</tr>";
        } else {
            mensaje = "<tr>" +
                        "<td><p style = \" margin: 0%\">Se ha detectado un uso no autorizado sobre el dispositivo.</p></td>" +
                    "</tr>";
        }

        return "<html>" +
                "<head>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                "<meta property=\"og:title\" content=\"*|MC:SUBJECT|*\" />" +
                "<style type=\"text/css\">" +
                    "body{width:100% !important;} .ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                    "body{-webkit-text-size-adjust:none;}" +
                    "body{margin:0; padding:0;}" +
                    "img{border:0; height:auto; line-height:100%; outline:none; text-decoration:none;}" +
                    "table td{border-collapse:collapse;}" +
                    "#backgroundTable{height:100% !important; margin:0; padding:0; width:100% !important;}" +
                    "body, #backgroundTable{" +
                        "background-color:#FAFAFA; }" +
                    "#templateContainer{" +
                        "border: 1px solid #DDDDDD; }" +
                    "#headerImage{" +
                        "height:auto;" +
                        "max-width:600px; }" +
                    "#templateContainer, .bodyContent{" +
                        "background-color:#FFFFFF; }" +
                    ".bodyContent div{" +
                        "color:#505050;" +
                        "font-family:Arial;" +
                        "font-size:14px;" +
                        "line-height:150%;" +
                        "text-align:left; }" +
                    ".btn {" +
                        "border: none;" +
                        "color: white;" +
                    "padding: 14px 28px;" +
                    "font-size: 16px;" +
                    "border-radius: 4px;" +
                    "cursor: pointer; }" +
                    ".procesar {" +
                    "background-color: #0066ff; }" +
                    ".procesar:hover {" +
                        "background-color: #001f4d;}" +
                "</style>" +
                "</head>" +
                "<body leftmargin=\"0\" marginwidth=\"0\" topmargin=\"0\" marginheight=\"0\" offset=\"0\">" +
                "<center>" +
                "                     <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\" id=\"backgroundTable\">" +
                "                         <tr>" +
                "                             <td align=\"center\" valign=\"top\">" +
                "                                 <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" id=\"templateContainer\">" +
                "                                     <tr>" +
                "                                         <td align=\"center\" valign=\"top\">" +
                "                                             <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600px\" id=\"templateBody\">" +
                "                                                 <tr>" +
                "                                                     <td colspan=\"3\" valign=\"top\" class=\"bodyContent\">" +
                "                                                           <table border=\"0\" cellpadding=\"20\" cellspacing=\"0\" width=\"100%\">                                                    <tr>" +
                "                                                                   <td valign=\"top\">" +
                "                                                                       <div mc:edit=\"std_content00\" style=\"background-color: #FFFFFF;\">" +
                "                                                                       <div style=\"background-color: WHITE;\">" +
                "           <p style=\"margin-left: 10px; font-family: Optima, Segoe, Segoe UI, Candara, Calibri, Arial, sans-serif; font-size: 20px;   color: #FFFFFF;\">" +
                "             <img src=\"https://scontent.fccs3-1.fna.fbcdn.net/v/t1.0-9/16711819_1797159503882311_748410725325767967_n.jpg?_nc_cat=111&_nc_ht=scontent.fccs3-1.fna&oh=81d6bac8c9f329010c9d179306618520&oe=5D35C762\" style=\"max-width:215px; height: 160px; background-color: WHITE\" id=\"headerImage campaign-icon\" mc:label=\"header_image\" mc:edit=\"header_image\" mc:allowdesigner mc:allowtext />" +
                "           </p></div>" +
                "                                <table  border=\"0\" cellpadding=\"5\" cellspacing=\"0\" width=\"100%\">" +
                "                                <tr>" +
                "                                      <td> <h4 style = \"margin: 0%; \"><strong>Hola " + nombre + ",</strong></h4></td>" +
                                                "</tr>" +
                                                "<tr>" +
                "                                      " + mensaje +
                "                            </table>" +
                "<p><a href=\"http://www.telecom.ing.uc.edu.ve/\">www.telecom.ing.uc.edu.ve</a></p>" +
                "           </div>" +
                "                                                                   </td>" +
                "                                                               </tr>" +
                "                                                           </table>" +
                "                                                       </td>" +
                "                                                   </tr>" +
                "                                               </table>" +
                "                                           </td>" +
                "                                       </tr>" +
                "                                   </table>" +
                "                                   <br />" +
                "                               </td>" +
                "                           </tr>" +
                "                       </table>" +
                "                   </center>" +
                "               </body>" +
                "           </html>";
    }

}