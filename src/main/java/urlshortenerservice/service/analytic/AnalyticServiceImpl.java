package urlshortenerservice.service.analytic;

import urlshortenerservice.model.Analytic;
import urlshortenerservice.model.Url;
import urlshortenerservice.repository.AnalyticRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {
    private final AnalyticRepository analyticRepository;

    @Override
    @Async("analyticExecutor")
    public void recordClickAsync(Url url, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        if ((ip == null || ip.isBlank()) && (userAgent == null || userAgent.isBlank())) {
            return;
        }

        analyticRepository.save(
                Analytic.builder()
                        .url(url)
                        .userAgent(userAgent)
                        .ipAddress(ip)
                        .build()
        );
    }
}
