package kitchenpos.menu.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptance.AcceptanceTest;
import kitchenpos.acceptance.RestAssuredApi;
import kitchenpos.menu.dto.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static kitchenpos.menu.acceptance.MenuGroupAcceptanceTest.메뉴_그룹_등록_요청;
import static kitchenpos.menu.acceptance.ProductAcceptanceTest.상품_등록_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 인수 테스트")
public class MenuAcceptanceTest extends AcceptanceTest {

    private MenuProductRequest 매콤치킨구성;
    private MenuProductRequest 치즈볼구성;

    private MenuGroupResponse 인기메뉴그룹;

    @BeforeEach
    public void setUp() {
        super.setUp();

        ProductResponse 매콤치킨 = 상품_등록_요청(ProductRequest.of("매콤치킨", BigDecimal.valueOf(13000))).as(ProductResponse.class);
        ProductResponse 치즈볼 = 상품_등록_요청(ProductRequest.of("치즈볼", BigDecimal.valueOf(2000))).as(ProductResponse.class);

        매콤치킨구성 = new MenuProductRequest(매콤치킨.getId(), 1L);
        치즈볼구성 = new MenuProductRequest(치즈볼.getId(), 2L);

        인기메뉴그룹 = 메뉴_그룹_등록_요청(new MenuGroupRequest("인기메뉴")).as(MenuGroupResponse.class);
    }

    @Test
    @DisplayName("메뉴 정상 시나리오")
    void normalScenario() {
        MenuRequest 매콤치킨단품 = MenuRequest.of("매콤치킨단품", BigDecimal.valueOf(13000), 인기메뉴그룹.getId(), Collections.singletonList(매콤치킨구성));
        MenuRequest 매콤치즈볼세트 = MenuRequest.of("매콤치즈볼세트", BigDecimal.valueOf(15000), 인기메뉴그룹.getId(), Arrays.asList(매콤치킨구성, 치즈볼구성));

        메뉴_등록됨(메뉴_등록_요청(매콤치킨단품));
        메뉴_등록됨(메뉴_등록_요청(매콤치즈볼세트));

        ExtractableResponse<Response> response = 메뉴_목록_조회_요청();
        메뉴_목록_조회됨(response);
        메뉴_목록_일치됨(response, Arrays.asList("매콤치킨단품", "매콤치즈볼세트"));
    }

    @Test
    @DisplayName("메뉴 예외 시나리오")
    void exceptionScenario() {
        MenuRequest 메뉴가격없음 = MenuRequest.of("매콤치킨단품", null, 인기메뉴그룹.getId(), Collections.singletonList(매콤치킨구성));
        MenuRequest 잘못된메뉴가격 = MenuRequest.of("매콤치킨단품", BigDecimal.valueOf(-1), 인기메뉴그룹.getId(), Collections.singletonList(매콤치킨구성));

        메뉴_등록_실패됨(메뉴_등록_요청(메뉴가격없음));
        메뉴_등록_실패됨(메뉴_등록_요청(잘못된메뉴가격));

        ExtractableResponse<Response> response = 메뉴_목록_조회_요청();
        메뉴_목록_조회됨(response);
        메뉴_목록_일치됨(response, Collections.emptyList());
    }

    public static ExtractableResponse<Response> 메뉴_등록_요청(MenuRequest request) {
        return RestAssuredApi.post("/api/menus", request);
    }

    private ExtractableResponse<Response> 메뉴_목록_조회_요청() {
        return RestAssuredApi.get("/api/menus");
    }

    private void 메뉴_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 메뉴_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 메뉴_목록_일치됨(ExtractableResponse<Response> response, List<String> excepted) {
        assertThat(response.jsonPath().getList("name"))
                .isEqualTo(excepted);
    }

    public static void 메뉴_등록_실패됨(ExtractableResponse<Response> response) {
        AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
