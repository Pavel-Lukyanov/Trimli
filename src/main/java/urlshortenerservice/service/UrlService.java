package urlshortenerservice.service;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.model.Url;

public interface UrlService {
    String createShortUrl(UrlRequestDto dto);

    Url resolve(String hash);
}
