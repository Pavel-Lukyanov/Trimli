package urlshortenerservice.service.analytic;

import urlshortenerservice.model.Url;
import jakarta.servlet.http.HttpServletRequest;

public interface AnalyticService {
    void recordClickAsync(Url url, HttpServletRequest request);
}
