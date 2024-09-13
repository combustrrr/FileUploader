import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileServer {
    private static final int PORT = 5000;
    private static final String SAVE_DIR = "uploads/";
    private static volatile boolean running = true;
    private static final Logger logger = Logger.getLogger(FileServer.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");

            while (running) {
                // Wait for client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle file upload from client
                handleFileUpload(clientSocket);
            }
        } catch (IOException e) {
            //Log the exception using a logging framework
            logger.log(Level.SEVERE,"Error accepting client connection",e);
        }
    }

    public static void shutdown(){
        running = false;
    }

    private static void handleFileUpload(Socket clientSocket) {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream())) {
            // Read file name from client
            String fileName = dis.readUTF();
            System.out.println("Receiving file: " + fileName);

            // Create directory if it doesn't exist
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    logger.log(Level.SEVERE, "Failed to create directory: " + SAVE_DIR);
                    // Consider throwing an exception or taking alternative action
                }
            }

            // Create a file output stream to save the file
            File file = new File(SAVE_DIR + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                // Read file content from client and write to disk
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("File " + fileName + " saved successfully.");
            }
        } catch (IOException e) {
            //Log the exception using a logging framework
            logger.log(Level.SEVERE,"Error handling file upload",e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                //Log the exception using a logging framework
                logger.log(Level.SEVERE,"Error closing client socket",e);
            }
        }
    }
}
