package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.dto.*;
import kitchenpos.pixture.AcceptanceTest;
import kitchenpos.pixture.RestAssuredApi;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static kitchenpos.acceptance.TableAcceptanceTest.주문_테이블_등록_요청;
import static kitchenpos.domain.OrderStatus.COMPLETION;
import static kitchenpos.domain.OrderStatus.MEAL;
import static kitchenpos.dto.OrderStatusRequest.completion;
import static kitchenpos.dto.OrderStatusRequest.meal;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 인수 테스트")
public class OrderAcceptanceTest extends AcceptanceTest {

    private TableResponse 테이블;
    private TableResponse 빈테이블;
    private OrderLineItemRequest 주문항목1;
    private OrderLineItemRequest 주문항목2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        테이블 = 주문_테이블_등록_요청(TableRequest.from(2)).as(TableResponse.class);
        빈테이블 = 주문_테이블_등록_요청(TableRequest.empty()).as(TableResponse.class);
        주문항목1 = new OrderLineItemRequest(1L, 1L);
        주문항목2 = new OrderLineItemRequest(1L, 1L);
    }

    @Test
    @DisplayName("주문 정상 시나리오")
    void normalScenario() {
        OrderRequest 매콤치킨주문 = new OrderRequest(테이블.getId(), Collections.singletonList(주문항목1));
        OrderRequest 허니치킨주문 = new OrderRequest(테이블.getId(), Collections.singletonList(주문항목2));

        String createdUri1 = 주문_등록됨(주문_등록_요청(매콤치킨주문));
        String createdUri2 = 주문_등록됨(주문_등록_요청(허니치킨주문));

        주문_상태_변경됨(주문_상태_변경_요청(createdUri1, meal()));
        주문_상태_변경됨(주문_상태_변경_요청(createdUri2, completion()));

        ExtractableResponse<Response> response = 주문_목록_조회_요청();
        주문_목록_조회됨(response);
        주문_상태_일치됨(response, Arrays.asList(MEAL.name(), COMPLETION.name()));
    }

    @Test
    @DisplayName("주문 예외 시나리오")
    void exceptionScenario() {
        OrderRequest 주문테이블없음 = new OrderRequest(null, Collections.singletonList(주문항목1));
        OrderRequest 빈주문테이블 = new OrderRequest(빈테이블.getId(), Collections.singletonList(주문항목2));
        OrderRequest 매콤치킨주문 = new OrderRequest(테이블.getId(), Collections.singletonList(주문항목1));

        주문_등록_실패됨(주문_등록_요청(주문테이블없음));
        주문_등록_실패됨(주문_등록_요청(빈주문테이블));

        String createdUri = 주문_등록됨(주문_등록_요청(매콤치킨주문));
        주문_상태_변경됨(주문_상태_변경_요청(createdUri, completion()));
        주문_상태_변경_실패됨(주문_상태_변경_요청(createdUri, meal()));
    }

    private ExtractableResponse<Response> 주문_등록_요청(OrderRequest request) {
        return RestAssuredApi.post("/api/orders", request);
    }

    private ExtractableResponse<Response> 주문_목록_조회_요청() {
        return RestAssuredApi.get("/api/orders");
    }

    private ExtractableResponse<Response> 주문_상태_변경_요청(String uri, OrderStatusRequest request) {
        return RestAssuredApi.put(uri + "/order-status", request);
    }

    private String 주문_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.header("Location");
    }

    private void 주문_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 주문_상태_일치됨(ExtractableResponse<Response> response, List<String> excepted) {
        assertThat(response.jsonPath().getList("orderStatus"))
                .isEqualTo(excepted);
    }

    public static void 주문_등록_실패됨(ExtractableResponse<Response> response) {
        AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 주문_상태_변경됨(ExtractableResponse<Response> response) {
        AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_상태_변경_실패됨(ExtractableResponse<Response> response) {
        AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
