package edu.escuelaing.arep.securesparkapplive;

import spark.Request;
import spark.Response;
import static spark.Spark.*;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Diego23p
 */
public class SecureSparkServices {

    public static void main(String[] args) {

        port(getPort());

        //API: secure(keystoreFilePath, keystorePassword, truststoreFilePath,truststorePassword);
        secure("keystores/ecikeystore.p12", "123456", null, null);

        get("/login", (req, res) -> inputDataPage(req, res));
        post("/results", (req, res) -> loginService(req, res));
    }

    /**
     * Formulario de ingreso de datos 
     * @param req requerientos de la solicitud
     * @param res respuesta de la solicitud
     * @return respuesta en la validacion
     */
    private static String inputDataPage(Request req, Response res) {
        String pageContent
                = "<!DOCTYPE html>"
                + "<html>"
                + "<body>"
                + "<h2>Login</h2>"
                + "<form method=\"post\" action=\"/results\">"
                + "  <h4>User:</h4>"
                + "  <input type=\"text\" name=\"user\" >"
                + "  <br>"
                + "  <h4>Password:</h4>"
                + "  <input type=\"password\" name=\"password\" >"
                + "  <br><br>"
                + "  <input type=\"submit\" value=\"Submit\">"
                + "</form>"
                + "</body>"
                + "</html>";
        return pageContent;
    }

    /**
     * Validación de user y password conparando con una contraseña en SHA1
     * @param req requerientos de la solicitud
     * @param res respuesta de la solicitud
     * @return 
     */
    private static String loginService(Request req, Response res) {

        String user = req.queryParams("user");
        String password = req.queryParams("password");
        
        //System.out.println("usuario: "+user);
        //System.out.println("contra: "+password);
        //System.out.println("contra encrip: "+DigestUtils.sha1Hex(password));
        
        String pageContent;
        String rta;
        String encryptedPassword=DigestUtils.sha1Hex("test");
        
        req.session(true);
        req.session().attribute("Autenticado","False");
        
        if ((user.equals("Diego") || user.equals("Juan")) && encryptedPassword.equals(DigestUtils.sha1Hex(password)) ){ 
            req.session().attribute("Autenticado","True");
            //System.out.println((String) req.session().attribute("Autenticado") );
            rta = "Bienvenido "+user;
        } else { 
            rta="Usuario o contraseña invalidas";
        }
        

        pageContent
                = "<!DOCTYPE html>"
                + "<html>"
                + "<body>"
                + "<h3>"+ rta +"</h3>"
                + "</body>"
                + "</html>";

        return pageContent;
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }
}
