package urlshortenerservice.service.analytic;

import urlshortenerservice.dto.ClickMetaDto;
import urlshortenerservice.model.Analytic;
import urlshortenerservice.model.Url;
import urlshortenerservice.repository.AnalyticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {
    private final AnalyticRepository analyticRepository;

    @Override
    @Async("analyticExecutor")
    public void recordClickAsync(Url url, ClickMetaDto meta) {
        if (meta == null) {
            return;
        }

        if ((meta.ip() == null || meta.ip().isBlank())
                && (meta.userAgent() == null || meta.userAgent().isBlank())) {
            return;
        }

        analyticRepository.save(
                Analytic.builder()
                        .url(url)
                        .ipAddress(meta.ip())
                        .userAgent(meta.userAgent())
                        .build()
        );
    }
}
