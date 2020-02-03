package se.company;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.Thread.sleep;

@Component
public class UploadItemId {

        @PostConstruct
        public void query() {
            new Thread(() -> {

                while (true) {
                    try {
                        sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        System.out.println("putting itemId for Service");
                        URL url = new URL("http://127.0.0.1:8761/registerItemId/123456/url.internet.com");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        if (conn.getResponseCode() != 200) {
                            throw new RuntimeException("Failed : HTTP error code : "
                                    + conn.getResponseCode());
                        }

                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                (conn.getInputStream())));

                        String output;
                        while ((output = br.readLine()) != null) {
                            System.out.println(output);
                        }

                        conn.disconnect();
                    } catch (Exception e) {
                        throw new RuntimeException("error", e);
                    }
                }}).start();
        }
}
