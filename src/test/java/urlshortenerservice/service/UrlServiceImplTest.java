package urlshortenerservice.service;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.exception.EntityNotFoundException;
import urlshortenerservice.localcache.LocalCache;
import urlshortenerservice.mapper.UrlMapper;
import urlshortenerservice.model.Url;
import urlshortenerservice.repository.UrlCacheRepository;
import urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @Mock
    private LocalCache localCache;
    @Mock
    private UrlMapper urlMapper;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "ttlDays", 30L);
    }

    @Test
    void createShortUrl_shouldSaveUrlAndReturnHash() {
        UrlRequestDto dto = new UrlRequestDto("https://example.com");
        Url mappedUrl = new Url();
        String hash = "abc123";

        when(urlMapper.toModel(dto)).thenReturn(mappedUrl);
        when(localCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(dto);

        assertEquals(hash, result);
        assertEquals(hash, mappedUrl.getHash());
        assertNotNull(mappedUrl.getExpiresAt());
        verify(urlRepository).save(mappedUrl);
        verify(urlCacheRepository).save(hash, mappedUrl, 30L);
    }

    @Test
    void resolve_shouldReturnUrlFromCacheIfPresent() {
        String hash = "abc123";
        Url cachedUrl = new Url();

        when(urlCacheRepository.get(hash)).thenReturn(cachedUrl);

        Url result = urlService.resolve(hash);

        assertSame(cachedUrl, result);
        verify(urlRepository, never()).findByHash(any());
        verify(urlCacheRepository, never()).save(anyString(), any(), anyLong());
    }

    @Test
    void resolve_shouldLoadFromRepoIfCacheMiss() {
        String hash = "abc123";
        Url repoUrl = new Url();

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(repoUrl));

        Url result = urlService.resolve(hash);

        assertSame(repoUrl, result);
        verify(urlCacheRepository).save(hash, repoUrl, 30L);
    }

    @Test
    void resolve_shouldThrowIfNotFound() {
        String hash = "abc123";

        when(urlCacheRepository.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> urlService.resolve(hash));
    }

    @Test
    void createShortUrl_shouldThrowOnInvalidUrls() {
        String[] invalidUrls = {
                null,
                "",
                "   ",
                "ftp://example.com",
                "docs",
                "http://localhost",
                "http://127.0.0.1",
                "http://домен" // нет точки
        };

        for (String url : invalidUrls) {
            UrlRequestDto dto = new UrlRequestDto(url);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> urlService.createShortUrl(dto));
            assertEquals("Введите ссылку", ex.getMessage());
        }
    }

    @Test
    void createShortUrl_shouldAcceptValidUrls() {
        String[] validUrls = {
                "http://example.com",
                "https://example.com",
                "http://sub.домен.ru/path",
                "https://my-site.com:8080/test"
        };

        for (String url : validUrls) {
            UrlRequestDto dto = new UrlRequestDto(url);

            Url mappedUrl = new Url();
            when(urlMapper.toModel(dto)).thenReturn(mappedUrl);
            when(localCache.getHash()).thenReturn("abc123");

            assertDoesNotThrow(() -> urlService.createShortUrl(dto));
        }
    }
}