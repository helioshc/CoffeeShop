package coffeeshop;

//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.query.Param;

//import java.util.List;
/*
public interface MyPagesRepository extends CrudRepository<MyPages, Long> {

    List<MyPages> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);

}
*/

import org.springframework.data.repository.PagingAndSortingRepository;

public interface MyPagesRepository extends PagingAndSortingRepository<MyPages, Long>{

    MyPages findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);

}