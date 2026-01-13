package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SentimentsResponseDto;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

public interface SentimentService  {
      Optional<ResponseDto> consultarSentimiento(String texto);
      Optional<SentimentsResponseDto> consultarSentimientos(String texto);
}
