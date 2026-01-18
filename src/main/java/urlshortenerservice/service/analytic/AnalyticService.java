package urlshortenerservice.service.analytic;

import urlshortenerservice.dto.ClickMetaDto;
import urlshortenerservice.model.Url;

public interface AnalyticService {
    void recordClickAsync(Url url, ClickMetaDto meta);
}
