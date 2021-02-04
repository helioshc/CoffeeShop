
package coffeeshop.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// import java.util.Date;

//@FeignClient(name="warehouse", url="${api.warehouse.url}")
@FeignClient(name="warehouse", url="http://warehouse:8080")
public interface StockService {

    @RequestMapping(method= RequestMethod.PATCH, path="/stocks/reduce")
    public Boolean reduce(@RequestBody Stock stock);

}