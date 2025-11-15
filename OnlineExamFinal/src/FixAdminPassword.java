import java.sql.Connection;
import java.sql.PreparedStatement;

public class FixAdminPassword {

    public static void main(String[] args) {
        System.out.println("Starting admin password fix...");

        // 1. "admin123" ka sahi hash generate karo
        // Yeh wahi function call karega jo login screen karti hai
        String correctHash = SecurityUtil.hashPassword("admin123");

        System.out.println("Correct hash for 'admin123' is: " + correctHash);

        // 2. Database mein is hash ko update karo
        String sql = "UPDATE users SET password_hash = ? WHERE username = 'admin'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, correctHash);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("----------------------------------------");
                System.out.println("ADMIN PASSWORD UPDATED SUCCESSFULLY!");
                System.out.println("----------------------------------------");
            } else {
                System.out.println("****************************************");
                System.out.println("ADMIN USER NOT FOUND! (kya 'admin' user database mein hai?)");
                System.out.println("****************************************");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}