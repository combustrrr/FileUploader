import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class FileClientGUI {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        // Create the GUI for file selection
        JFrame frame = new JFrame("File Uploader");
        JButton uploadButton = new JButton("Upload File");

        uploadButton.addActionListener(e -> {
            // Open file chooser dialog
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                // Get the selected file
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                // Upload the file
                uploadFile(filePath);
            } else {
                JOptionPane.showMessageDialog(null, "No file selected.");
            }
        });

        // Set up the JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.add(uploadButton);
        frame.setVisible(true);
    }

    private static void uploadFile(String filePath) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(filePath)) {

            // Send file name to server
            File file = new File(filePath);
            dos.writeUTF(file.getName());

            // Send file content to server
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            JOptionPane.showMessageDialog(null, "File " + file.getName() + " uploaded successfully.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
