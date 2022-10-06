package com.santander.pricing.hello;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class RecieverProcessor {

    @Autowired
    public RecieverProcessor(Receiver receiver) {
        receiver.registerCallback(email -> process(email));
    }

    private void process(String email) {
        System.out.println("Processing <" + email + ">");
        try {

            //this is in a test only
            MockWebServer webServer = new MockWebServer();
            webServer.start(8080);
            webServer.enqueue(new MockResponse().setResponseCode(200));


            HttpClient client = HttpClient.newHttpClient();
            //check we should be doing a post
            HttpRequest postRequest = HttpRequest.newBuilder(new URI("http://localhost:8080/prices"))
                    .POST(HttpRequest.BodyPublishers.ofString(email)).build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            RecordedRequest recordedRequest = webServer.takeRequest();
            System.out.println(recordedRequest.getPath());
            System.out.println(recordedRequest.getBody().toString());
            System.out.println(recordedRequest.getMethod());

        } catch (Exception e) {

            System.out.println(e);
        }
    }
}
