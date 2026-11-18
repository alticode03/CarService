
package koneksidbc;

import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiSupabase {

    private final String supabaseUrl;
    private final String supabaseKey;
    private final String dbUrl;
    private final HttpClient client;

    public KoneksiSupabase() {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();

        supabaseUrl = dotenv.get("SUPABASE_URL");
        supabaseKey = dotenv.get("SUPABASE_KEY");
        dbUrl = dotenv.get("SUPABASE_DB_URL");

        client = HttpClient.newHttpClient();
    }

    public Connection conn() throws SQLException {
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalStateException(
                "SUPABASE_DB_URL tidak ditemukan di .env"
            );
        }
        return DriverManager.getConnection(dbUrl);
    }

    public void testKoneksi() {
        if (supabaseUrl == null || supabaseKey == null) {
            System.out.println(
                "SUPABASE_URL atau SUPABASE_KEY tidak ditemukan di .env"
            );
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/auth/v1/health"))
                    .header("apikey", supabaseKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Status: " + response.statusCode());
            System.out.println("Response: " + response.body());

        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
    }
}
