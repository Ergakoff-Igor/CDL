package ru.docs.construction.manager.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.docs.construction.manager.controller.payload.NewActPayload;
import ru.docs.construction.manager.controller.payload.UpdateActPayload;
import ru.docs.construction.manager.entity.Act;
import ru.docs.construction.manager.entity.ActStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class RestClientActsRestClient implements ActsRestClient {

    private static final ParameterizedTypeReference<List<Act>> ACTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Override
    public List<Act> findAllActs(String filter) {
        return this.restClient
                .get()
                .uri("/catalogue-api/acts?filter={filter}", filter)
                .retrieve()
                .body(ACTS_TYPE_REFERENCE);
    }

    @Override
    public Act createAct(String month, Short year, String section, Double price) {
        try {
            return this.restClient
                    .post()
                    .uri("/catalogue-api/acts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewActPayload(month, year, section, price))
                    .retrieve()
                    .body(Act.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public Optional<Act> findAct(long actId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/catalogue-api/acts/{actId}", actId)
                    .retrieve()
                    .body(Act.class));
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        }
    }

    @Override
    public void updateAct(Long actId, String month, Short year, String section, Double price, ActStatus actStatus) {
        try {
            this.restClient
                    .patch()
                    .uri("/catalogue-api/acts/{actId}", actId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateActPayload(month, year, section, price, actStatus))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteAct(Long actId) {
        try {
            this.restClient
                    .delete()
                    .uri("/catalogue-api/acts/{actId}", actId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }

    @Override
    public void updateActStatus(Long actId, String actStatus) {
        try {
            this.restClient
                    .patch()
                    .uri("/catalogue-api/acts/{actId}/{status}", actId, actStatus)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException(exception);
        }
    }
}
