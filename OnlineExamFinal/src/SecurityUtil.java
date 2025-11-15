import java.security.MessageDigest;
import java.util.Formatter;

public class SecurityUtil { // <-- Naam check karein

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            byte[] digest = md.digest();
            Formatter formatter = new Formatter();
            for (byte b : digest) {
                formatter.format("%02x", b);
            }
            String hash = formatter.toString();
            formatter.close();
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }
}