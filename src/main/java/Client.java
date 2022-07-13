import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    public static final String APIKEY = "Rozl0BMyWY2qK9h91KTpOzYZK8YDNP4x1tPqAYGU";
    //---https://api.nasa.gov/planetary/apod?api_key=Rozl0BMyWY2qK9h91KTpOzYZK8YDNP4x1tPqAYGU

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=" + APIKEY);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            String url = getURLFromJson(body);
            saveToFile(url, httpClient);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public static String getURLFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiNasa apiNasa = objectMapper.readValue(json, ApiNasa.class);
            return apiNasa.getUrl();
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void saveToFile(String url , CloseableHttpClient httpClient) throws IOException {
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(request);
        int index = url.lastIndexOf("/") + 1;
        String name = url.substring(index);
        String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        FileOutputStream fileOutputStream = new FileOutputStream(name);
        byte[] bytes = body.getBytes();
        fileOutputStream.write(bytes,0, bytes.length);
        fileOutputStream.close();
    }
}
