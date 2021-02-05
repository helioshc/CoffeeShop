
package coffeeshop.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// import java.util.Date;

//@FeignClient(name="kitchen", url="${api.url.kitchen}")     // phone82 ÂüÁ¶
@FeignClient(name="kitchen", url="http://kitchen:8080") // origin Src
public interface ProductService {

    @RequestMapping(method= RequestMethod.PUT, path="/products/{id}")
    public void cancel(@PathVariable("id") Long productId, @RequestBody Product product);

}
