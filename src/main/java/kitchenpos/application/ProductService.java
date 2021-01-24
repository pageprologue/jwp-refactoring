package kitchenpos.application;

import kitchenpos.advice.exception.ProductException;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductDao productDao;

    public ProductService(final ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    public Product create(final Product product) {
        final BigDecimal price = product.getPrice();

        if (Objects.isNull(price) || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException("상품 가격이 없거나 0보다 작습니다", price);
        }

        return productDao.save(product);
    }

    public List<Product> list() {
        return productDao.findAll();
    }

    public Product findById(Long id) {
        return productDao.findById(id).orElseThrow(()->new ProductException("존재하지 않는 상품 id입니다", id));
    }
}