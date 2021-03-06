
package coffeeshop.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

//@FeignClient(name="kitchen", url="${api.url.kitchen}")
//@FeignClient(name="kitchen", url="http://kitchen:8080") // origin Src
@FeignClient(name="kitchen", url="http://localhost:8082") // origin Src
public interface ProductService {

    //@RequestMapping(method= RequestMethod.POST, path="/products/{id}")
    //public void cancel(@PathVariable ("id") Long productId, @RequestBody Product product);
    @RequestMapping(method= RequestMethod.POST, path="/products")
    public void cancel(@RequestBody Product product);
}
