package urlshortenerservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import urlshortenerservice.dto.ClickMetaDto;
import urlshortenerservice.dto.ResolveUrlResponseDto;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.mapper.UrlMapper;
import urlshortenerservice.model.Url;
import urlshortenerservice.service.UrlBuilderService;
import urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import urlshortenerservice.service.analytic.AnalyticService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/urls")
public class UrlController {
    private final UrlService urlService;
    private final UrlBuilderService urlBuilderService;
    private final UrlMapper urlMapper;
    private final AnalyticService analyticService;

    @PostMapping
    public UrlResponseDto createShortUrl(@Valid @RequestBody UrlRequestDto dto) {
        String hash = urlService.createShortUrl(dto);
        String shortUrl = urlBuilderService.buildShortUrl(hash);
        return urlMapper.toResponseDto(shortUrl);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<ResolveUrlResponseDto> resolve(@PathVariable String hash, HttpServletRequest request) {
        Url url = urlService.resolve(hash);

        ClickMetaDto meta = new ClickMetaDto(
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        analyticService.recordClickAsync(url, meta);
        return ResponseEntity.ok(new ResolveUrlResponseDto(url.getOriginalUrl()));
    }
}