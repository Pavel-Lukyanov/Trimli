package urlshortenerservice.controller;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.exception.EntityNotFoundException;
import urlshortenerservice.mapper.UrlMapper;
import urlshortenerservice.model.Url;
import urlshortenerservice.service.UrlBuilderService;
import urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;
    private final UrlBuilderService urlBuilderService;
    private final UrlMapper urlMapper;

    @PostMapping("/api/v1/urls")
    public UrlResponseDto createShortUrl(@Valid @RequestBody UrlRequestDto dto) {
        String hash = urlService.createShortUrl(dto);
        String shortUrl = urlBuilderService.buildShortUrl(hash);
        return urlMapper.toResponseDto(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash, HttpServletRequest request) {
        try {
            Url url = urlService.getOriginalUrl(hash, request);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", url.getOriginalUrl())
                    .build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}