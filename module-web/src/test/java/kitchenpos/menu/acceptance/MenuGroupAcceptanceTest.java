package kitchenpos.menu.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptance.AcceptanceTest;
import kitchenpos.acceptance.RestAssuredApi;
import kitchenpos.menu.dto.MenuGroupRequest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 그룹 인수 테스트")
public class MenuGroupAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("메뉴 그룹 정상 시나리오")
    void normalScenario() {
        MenuGroupRequest 인기메뉴 = new MenuGroupRequest("인기메뉴");
        MenuGroupRequest 타임세일메뉴 = new MenuGroupRequest("타임세일메뉴");

        메뉴_그룹_등록됨(메뉴_그룹_등록_요청(인기메뉴));
        메뉴_그룹_등록됨(메뉴_그룹_등록_요청(타임세일메뉴));

        ExtractableResponse<Response> response = 메뉴_그룹_목록_조회_요청();
        메뉴_그룹_목록_조회됨(response);
        메뉴_그룹_목록_일치됨(response, Arrays.asList("인기메뉴", "타임세일메뉴"));
    }

    @Test
    @DisplayName("메뉴 그룹 예외 시나리오")
    void exceptionScenario() {
        메뉴_그룹_등록_실패됨(메뉴_그룹_등록_요청(new MenuGroupRequest(null)));

        ExtractableResponse<Response> response = 메뉴_그룹_목록_조회_요청();
        메뉴_그룹_목록_조회됨(response);
        메뉴_그룹_목록_일치됨(response, Collections.emptyList());
    }

    public static ExtractableResponse<Response> 메뉴_그룹_등록_요청(MenuGroupRequest request) {
        return RestAssuredApi.post("/api/menu-groups", request);
    }

    private ExtractableResponse<Response> 메뉴_그룹_목록_조회_요청() {
        return RestAssuredApi.get("/api/menu-groups");
    }

    private void 메뉴_그룹_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 메뉴_그룹_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 메뉴_그룹_목록_일치됨(ExtractableResponse<Response> response, List<String> excepted) {
        assertThat(response.jsonPath().getList("name"))
                .isEqualTo(excepted);
    }

    public static void 메뉴_그룹_등록_실패됨(ExtractableResponse<Response> response) {
        AssertionsForClassTypes.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
