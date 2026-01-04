package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ResponseDto;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public interface SentimentService  {
     RestTemplate getRestemplate();
     Optional<ResponseDto> consultaSentiment (String texto);
}
