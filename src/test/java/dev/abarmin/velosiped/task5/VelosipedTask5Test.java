package dev.abarmin.velosiped.task5;

import dev.abarmin.velosiped.helper.VelosipedHelper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Aleksandr Barmin
 */
class VelosipedTask5Test {
  private VelosipedTask5 uut = VelosipedHelper.getInstance(VelosipedTask5.class);
  private VelosipedTask5Server server;

  @BeforeEach
  void setUp() {
    uut.init();

    server = uut.getBean(VelosipedTask5Server.class);
    server.startServer(1234);
  }

  @AfterEach
  void tearDown() {
    server.stopServer();
  }

  @ParameterizedTest
  @CsvSource({
      "1,2",
      "10,20",
      "-1,-2"
  })
  void check_calculation(int a, int b) throws Exception {
    final URL url = new URL("http://localhost:1234/sum-post");
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");

    final String requestBody = "{\"arg1\": " + a + ", \"arg2\": " + b +"}";
    try (final OutputStream outputStream = connection.getOutputStream()) {
      outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
    }

    try (final InputStream stream = connection.getInputStream()) {
      final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      final String response = reader.readLine();

      final String expectedResult = "{\"result\":" + (a + b) + "}";
      assertEquals(expectedResult, response);
    }
  }
}