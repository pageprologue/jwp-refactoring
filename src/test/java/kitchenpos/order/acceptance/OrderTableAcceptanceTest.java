package kitchenpos.order.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptance.AcceptanceTest;
import kitchenpos.acceptance.RestAssuredApi;
import kitchenpos.order.dto.OrderTableRequest;
import kitchenpos.order.dto.OrderTableResponse;
import kitchenpos.order.dto.TableGroupRequest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static kitchenpos.order.acceptance.TableGroupAcceptanceTest.테이블_그룹_등록_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 테이블 인수 테스트")
class OrderTableAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("주문 테이블 정상 시나리오")
    void normalScenario() {
        String createdLocationUri1 = 주문_테이블_등록됨(주문_테이블_등록_요청(OrderTableRequest.of(2)));
        String createdLocationUri2 = 주문_테이블_등록됨(주문_테이블_등록_요청(OrderTableRequest.of(4)));

        주문_테이블_비움(createdLocationUri1);
        테이블_손님수_변경(createdLocationUri2, OrderTableRequest.of(3));

        ExtractableResponse<Response> response = 주문_테이블_목록_조회_요청();
        주문_테이블_목록_조회됨(response);
        주문_테이블_손님수_일치됨(response, Arrays.asList(2, 3));
        주문_테이블_상태_일치됨(response, Arrays.asList(true, false));
    }

    @Test
    @DisplayName("주문 테이블 예외 시나리오")
    void exceptionScenario() {
        ExtractableResponse<Response> response1 = 주문_테이블_등록_요청(OrderTableRequest.of(2));
        ExtractableResponse<Response> response2 = 주문_테이블_등록_요청(OrderTableRequest.of(4));

        String createdLocationUri1 = 주문_테이블_등록됨(response1);
        String createdLocationUri2 = 주문_테이블_등록됨(response2);

        주문_테이블_비움(createdLocationUri1);
        주문_테이블_비움(createdLocationUri2);
        테이블_변경_실패됨(테이블_손님수_변경(createdLocationUri2, OrderTableRequest.of(3)));
    }

    public static ExtractableResponse<Response> 주문_테이블_등록_요청(OrderTableRequest request) {
        return RestAssuredApi.post("/api/tables", request);
    }

    private ExtractableResponse<Response> 주문_테이블_비움(String uri) {
        return RestAssuredApi.put(uri + "/empty");
    }

    private ExtractableResponse<Response> 테이블_손님수_변경(String uri, OrderTableRequest request) {
        return RestAssuredApi.put(uri + "/number-of-guests", request);
    }

    private ExtractableResponse<Response> 주문_테이블_목록_조회_요청() {
        return RestAssuredApi.get("/api/tables");
    }

    private String 주문_테이블_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.header("Location");
    }

    private void 주문_테이블_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 주문_테이블_손님수_일치됨(ExtractableResponse<Response> response, List<Integer> excepted) {
        assertThat(response.jsonPath().getList("numberOfGuests", Integer.class))
                .isEqualTo(excepted);
    }

    private void 주문_테이블_상태_일치됨(ExtractableResponse<Response> response, List<Boolean> excepted) {
        assertThat(response.jsonPath().getList("tableState"))
                .isEqualTo(excepted);
    }

    public static void 테이블_변경_실패됨(ExtractableResponse<Response> response) {
        AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 테이블_그룹_등록됨(ExtractableResponse<Response> response1, ExtractableResponse<Response> response2) {
        OrderTableResponse 테이블1 = response1.as(OrderTableResponse.class);
        OrderTableResponse 테이블2 = response2.as(OrderTableResponse.class);
        TableGroupRequest request = new TableGroupRequest(Arrays.asList(테이블1.getId(), 테이블2.getId()));
        테이블_그룹_등록_요청(request);
    }
}
