package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;
import kitchenpos.util.RestAssuredApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("상품 인수 테스트")
class ProductAcceptanceTest extends AcceptanceTest {

    private Product 매콤치킨;
    private Product 치즈볼;
    private Product 사이다;

    @BeforeEach
    void setUp() {
        super.setUp();

        매콤치킨 = new Product("매콤치킨",BigDecimal.valueOf(13000));
        치즈볼 = new Product("치즈볼", BigDecimal.valueOf(2000));
        사이다 = new Product("사이다", BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("상품 기능 정상 시나리오")
    void normalScenario() {
        상품_등록됨(상품_등록_요청(매콤치킨));
        상품_등록됨(상품_등록_요청(치즈볼));
        상품_등록됨(상품_등록_요청(사이다));

        ExtractableResponse<Response> response = 상품_목록_조회_요청();
        상품_목록_조회됨(response);
        상품_목록_일치됨(response, Arrays.asList(매콤치킨, 치즈볼, 사이다));
    }

    private ExtractableResponse<Response> 상품_등록_요청(Product product) {
        return RestAssuredApi.post("/api/products", product);
    }

    private ExtractableResponse<Response> 상품_목록_조회_요청() {
        return RestAssuredApi.get("/api/products");
    }

    private void 상품_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 상품_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 상품_목록_일치됨(ExtractableResponse<Response> response, List<Product> excepted) {
        assertThat(response.jsonPath().getList(".", Product.class))
                .extracting("name")
                .isEqualTo(getProductNames(excepted));
    }

    private List<String> getProductNames(List<Product> excepted) {
        return excepted.stream()
                .map(Product::getName)
                .collect(Collectors.toList());
    }
}
