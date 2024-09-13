import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private static final int PORT = 5000;
    private static final String SAVE_DIR = "uploads/";
    private static volatile boolean running = true;

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
            //// Log the exception using a logging framework
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
                dir.mkdirs();
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
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
