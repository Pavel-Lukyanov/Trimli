package urlshortenerservice.service;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.model.Url;
import jakarta.servlet.http.HttpServletRequest;

public interface UrlService {
    String createShortUrl(UrlRequestDto dto);

    Url getOriginalUrl(String hash, HttpServletRequest request);
}
