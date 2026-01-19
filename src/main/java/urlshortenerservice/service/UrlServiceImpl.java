package urlshortenerservice.service;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.exception.EntityNotFoundException;
import urlshortenerservice.localcache.LocalCache;
import urlshortenerservice.mapper.UrlMapper;
import urlshortenerservice.model.Url;
import urlshortenerservice.repository.UrlCacheRepository;
import urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    @Value("${url.ttl-days:30}")
    private long ttlDays;

    private final LocalCache localCache;
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Override
    @Transactional
    public String createShortUrl(UrlRequestDto dto) {
        validateLink(dto);

        Url url = urlMapper.toModel(dto);
        String hash = localCache.getHash();

        url.setHash(hash);
        url.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        urlRepository.save(url);
        urlCacheRepository.save(hash, url, ttlDays);

        return hash;
    }

    @Override
    public Url resolve(String hash) {
        Url url = urlCacheRepository.get(hash);

        if (url == null) {
            url = urlRepository.findByHash(hash)
                    .orElseThrow(() -> new EntityNotFoundException("URL not found"));
            urlCacheRepository.save(hash, url, ttlDays);
        }
        return url;
    }

    private void validateLink(UrlRequestDto dto) {
        String originalUrl = dto.originalUrl();

        if (originalUrl == null || originalUrl.isBlank()) {
            throw new IllegalArgumentException("Введите ссылку");
        }

        originalUrl = originalUrl.trim();

        String regex = "^(https?://)" +
                "(([\\p{L}0-9-]+\\.)+[\\p{L}]{2,})" +
                "(:\\d+)?(/.*)?$";

        if (!originalUrl.matches(regex)) {
            throw new IllegalArgumentException("Введите ссылку");
        }
    }
}
