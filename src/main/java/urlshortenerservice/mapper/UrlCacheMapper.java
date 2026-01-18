package urlshortenerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import urlshortenerservice.dto.UrlDto;
import urlshortenerservice.model.Url;

@Mapper(componentModel = "spring")
public interface UrlCacheMapper {
    UrlDto toDto(Url url);

    @Mapping(target = "analytics", ignore = true)
    Url toEntity(UrlDto dto);
}