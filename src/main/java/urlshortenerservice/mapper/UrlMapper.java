package urlshortenerservice.mapper;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.model.Url;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UrlMapper {
    Url toModel(UrlRequestDto dto);

    default UrlResponseDto toResponseDto(String shortUrl) {
        return new UrlResponseDto(shortUrl);
    }
}
