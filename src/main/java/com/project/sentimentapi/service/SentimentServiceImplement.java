package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SentimentServiceImplement implements SentimentService {

    public RestTemplate getRestemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    // SocketException
    public Optional<ResponseDto> consultaSentiment(String texto) {
        try {
            String urlPython = "http://127.0.0.1:8000/sentiment";
            Map<String, String> request = new HashMap<>();
            request.put("text", texto);
            ResponseDto responseDto = getRestemplate().postForObject(urlPython, request, ResponseDto.class);
            System.out.println(responseDto.getEstrellas());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i <= responseDto.getEstrellas(); i++) {
                if (i != responseDto.getEstrellas()) {
                    stringBuilder.append("\u2605" + " ");
                } else {
                    stringBuilder.append("\u2605");
                }
            }
            responseDto.setCalificación(stringBuilder.toString());
            System.out.println(responseDto.getEstrellas());
            return Optional.of(responseDto);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
