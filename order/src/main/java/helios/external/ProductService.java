
package helios.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="delivery", url="http://delivery:8080")
public interface ProductService {

    @RequestMapping(method= RequestMethod.POST, path="/products")
    public void cancel(@RequestBody Product product);

}