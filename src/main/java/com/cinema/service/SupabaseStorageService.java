package com.cinema.service;

import com.cinema.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String uploadFile(String bucket, String path, byte[] bytes, String contentType) {
        try {
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, path);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucket, path);
            } else {
                throw new BadRequestException(
                        "Supabase upload failed: HTTP " + response.statusCode() + " - " + response.body());
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload to Supabase: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Upload interrupted: " + e.getMessage());
        }
    }
}
