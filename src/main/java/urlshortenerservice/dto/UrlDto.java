package urlshortenerservice.dto;

import java.time.Instant;

public record UrlDto(
        Long id,
        String hash,
        String originalUrl,
        Instant expiresAt,
        Instant createdAt
) {
}