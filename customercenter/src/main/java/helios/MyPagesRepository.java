package helios;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPagesRepository extends CrudRepository<MyPages, Long> {

    List<> findByOrderId(Long orderId);
    List<> findByProductId(Long productId);
    List<> findByOrderId(Long orderId);

        void deleteByOrderId(Long orderId);
}