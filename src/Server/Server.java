package Server;

import java.io.*;
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import DataTransfer.Request.*;
import DataTransfer.Response.*;
import Service.Services;
import com.sun.net.httpserver.*;

/**The Server class is the class designed to be the starting point of the Family Map Server program.
 * This class will also contain a few static methods used by all the handler classes for reading and writing to streams.
 *
 * @author Jonathan Ashton Shill Linford
 * @version 1.0 October 5, 2017
 */
public class Server {

    private static final int MAX_WAITING_CONNECTIONS = 12;
    public static Services service = new Services();
    private HttpServer server;


    private void run(String portNumber) {
        System.out.println("Initializing HTTP Server");

        try{
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch(IOException ex){
            ex.printStackTrace();
            return;
        }

        server.setExecutor(null);

        System.out.println("Creating contexts");

        server.createContext("/user/register", new RegisterUserHandler());
        server.createContext("/user/login", new LoginUserHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/", new DefaultHandler());

        System.out.println("Starting server");
        server.start();

        System.out.println("Server started");
    }

    /**
     * This method is used to read a stream coming from a connection and output that stream in the form of a string.
     * @param is    The input stream from a connection.
     * @return      The contents of the stream, in the form of the String.
     * @throws IOException  If there is a problem with the input and output, IOException is thrown.
     */
    public static String readString(InputStream is) throws IOException{
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);

        char[] buf = new char[1024];
        int len;

        while ((len = sr.read(buf)) > 0){
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /**
     * This method is used to read a string to a stream stream used by the connection.
     * @param str           The string to be written to the stream.
     * @param os            The stream used by the connection
     * @throws IOException  If there is a problem with the input and output, IOException is thrown.
     */
    public static void writeString(String str, OutputStream os) throws IOException{
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    /**
     * The Main function used to start the program
     * @param args  The arguments passed to the program, including the port number.
     */
    public static void main(String[] args){
        String portNumber = args[0];
        new Server().run(portNumber);
    }
}

/**
 * This class is used as the default handler for the server. Loads the webpage and uses the files associated with it.
 */
class DefaultHandler implements HttpHandler{
    /**
     * Handles requests for the webpage and files associated with the page.
     * @param exchange      The exchange is used to receive requests from the client and send responses.
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        OutputStream respBody = exchange.getResponseBody();
        Path filePath;

        if(exchange.getRequestURI().toString().equals("/")) {
            // Loads the default handler page
            filePath = FileSystems.getDefault().getPath("index.html");
        }else{
            // Loads the other files that the default handler calls
            String fileName = exchange.getRequestURI().toString();
            fileName = fileName.substring(1, fileName.length());
            filePath = FileSystems.getDefault().getPath(fileName);
        }

        // Loads the response body and headers and closes the connection
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        Files.copy(filePath, respBody);
        respBody.close();
    }
}

/**
 * This class is used to for the /user/register/ handler for the server.
 */
class RegisterUserHandler implements HttpHandler{

    /**
     * This handles the register request. Creates a request body from the exchange provided. That request is sent to the Service class.
     * @param exchange      Takes the exchange's data in order to create the register request and sends the register response back over the exchange.
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        boolean success = false;

        try{
            //Determine HTTP request type
            if(exchange.getRequestMethod().toLowerCase().equals("post")){
                InputStream reqBody = exchange.getRequestBody();

                String reqData = Server.readString(reqBody);

                //Create and send the request. Receive response
                RegisterRequest request = (RegisterRequest)Serializer.decoder(reqData, RegisterRequest.class);
                RegisterResponse response = Server.service.register(request);
                String respData = Serializer.encoder(response);

                //Send the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBodyStream = exchange.getResponseBody();
                Server.writeString(respData, respBodyStream);

                respBodyStream.close();

                success = true;
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

/**
 * This class is used to for the /user/login/ handler for the server.
 */
class LoginUserHandler implements HttpHandler{

    /**
     * This handles the login request. Creates a request body from the exchange provided. That request is sent to the Service class.
     * @param exchange      Takes the exchange's data in order to create the login request and sends the login response back over the exchange.
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try{
            //Determine HTTP request type
            if(exchange.getRequestMethod().toLowerCase().equals("post")){
                InputStream reqBody = exchange.getRequestBody();
                String reqData = Server.readString(reqBody);

                //Create and send the request. Receive response
                LoginRequest request = (LoginRequest)Serializer.decoder(reqData, LoginRequest.class);
                LoginResponse response = Server.service.login(request);
                String respData = Serializer.encoder(response);

                //Send the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBodyStream = exchange.getResponseBody();
                Server.writeString(respData, respBodyStream);

                respBodyStream.close();

                success = true;
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

/**
 * This class is used for the /clear/ handler for the server.
 */
class ClearHandler implements HttpHandler{

    /**
     * This handles the clear request.  That request is sent to the Service class.
     * @param exchange      No request body is included in the exchange. When this post handler is hit, clear service is started and a response is sent back over the exchange.
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try{
            //Determine HTTP request type
            if(exchange.getRequestMethod().toLowerCase().equals("post")){
                ClearResponse response = Server.service.clear();
                String respData = Serializer.encoder(response);

                //Send the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBodyStream = exchange.getResponseBody();
                Server.writeString(respData, respBodyStream);

                respBodyStream.close();

                success = true;
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

/**
 * This class is used for the /fill/ handler for the server.
 */
class FillHandler implements HttpHandler{

    /**
     * This handles the fill request.  That request is sent to the Service class.
     * @param exchange      No request body is included in the exchange. The URI indicates how many generations are to be filled. From there the fill service is started
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        boolean success = false;

        try{
            //Determine HTTP request type
            if(exchange.getRequestMethod().toLowerCase().equals("post")){
                String[] params = uri.getPath().split("/");
                String username = params[2];
                int generations = 4;        //Default to 4 generations
                if(params.length > 3)
                    generations = Integer.parseInt(params[3]);

                FillResponse response = Server.service.fill(username, generations);
                String respData = Serializer.encoder(response);

                //Send the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBodyStream = exchange.getResponseBody();
                Server.writeString(respData, respBodyStream);

                respBodyStream.close();

                success = true;
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

/**
 * This class is used for the /load/ handler for the server.
 */
class LoadHandler implements HttpHandler{

    /**
     * This handles the load request.  That request is sent to the Service class.
     * @param exchange      The load request is included in the request body containing the information to be loaded. From there, the Load request is sent to the load service
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;

        try{
            //Determine HTTP request type
            if(exchange.getRequestMethod().toLowerCase().equals("post")){
                InputStream reqBody = exchange.getRequestBody();

                String reqData = Server.readString(reqBody);

                System.out.println(reqData);

                //Create and send the request. Receive response
                LoadRequest request = (LoadRequest)Serializer.decoder(reqData, LoadRequest.class);
                LoadResponse response = Server.service.load(request);
                String respData = Serializer.encoder(response);

                //Send the response
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBodyStream = exchange.getResponseBody();
                Server.writeString(respData, respBodyStream);

                respBodyStream.close();

                success = true;
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

/**
 * This class is used for the /person/ handler for the server.
 */
class PersonHandler implements HttpHandler{

    /**
     * This handles the person request. The response is sent back over the exchange either containing the information on the person, or the information
     * on all the persons depending on if a personID was specified in the URL.
     * @param exchange      The exchange is used to respond with the person information.
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        URI uri = exchange.getRequestURI();
        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();

                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");

                    String[] params = uri.getPath().split("/");
                    String personID = null;
                    if(params.length > 2)
                        personID = params[2];

                    PersonResponse response = Server.service.person(authToken, personID);
                    String respData = Serializer.encoder(response);

                    //Send the response
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBodyStream = exchange.getResponseBody();
                    Server.writeString(respData, respBodyStream);

                    respBodyStream.close();

                    success = true;
                }
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

/**
 * This class is used for the /event/ handler for the server.
 */
class EventHandler implements HttpHandler{

    /**
     * This handles the event request. The response is sent back over the exchange either containing the information on the event, or the information
     * on all the event depending on if a eventID was specified in the URL.
     * @param exchange      The exchange is used to respond with the event information.
     * @throws IOException  Not meant to handle IOException and thus throws it.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EventResponse response = null;

        URI uri = exchange.getRequestURI();

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();

                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");

                    String[] params = uri.getPath().split("/");
                    String eventID = null;
                    if(params.length > 2)
                        eventID = params[2];

                    response = Server.service.event(authToken, eventID);

                    String respData = Serializer.encoder(response);

                    //Send the response
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBodyStream = exchange.getResponseBody();
                    Server.writeString(respData, respBodyStream);

                    respBodyStream.close();

                    success = true;
                }
            }

            if(!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }catch(IOException ex){
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
}

