package urlshortenerservice.service.analytic;


import urlshortenerservice.dto.ClickMetaDto;
import urlshortenerservice.model.Analytic;
import urlshortenerservice.model.Url;
import urlshortenerservice.repository.AnalyticRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class AnalyticServiceImplTest {
    @Mock
    private AnalyticRepository analyticRepository;
    @InjectMocks
    private AnalyticServiceImpl analyticService;

    @Test
    void recordClickAsync_ShouldNotSave_WhenMetaIsNull() {
        Url url = new Url();

        analyticService.recordClickAsync(url, null);

        verify(analyticRepository, never()).save(any());
    }

    @Test
    void recordClickAsync_ShouldNotSave_WhenIpAndUserAgentEmpty() {
        Url url = new Url();
        ClickMetaDto meta = new ClickMetaDto("", "");

        analyticService.recordClickAsync(url, meta);

        verify(analyticRepository, never()).save(any());
    }

    @Test
    void recordClickAsync_ShouldSave_WhenIpOrUserAgentPresent() {
        Url url = new Url();
        ClickMetaDto meta = new ClickMetaDto("127.0.0.1", "Chrome");

        analyticService.recordClickAsync(url, meta);

        ArgumentCaptor<Analytic> captor = ArgumentCaptor.forClass(Analytic.class);
        verify(analyticRepository).save(captor.capture());

        assertTrue(captor.getValue() instanceof Analytic);
        assertTrue(captor.getValue().getIpAddress().equals("127.0.0.1"));
        assertTrue(captor.getValue().getUserAgent().equals("Chrome"));
    }
}