package coffeeshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import java.util.Optional;

 @RestController
 public class StockController {

    @Autowired
    StockRepository stockRepository;

    // 상품 입고 (기존 재고에 추가)
    @RequestMapping(method=RequestMethod.PATCH, path="/receipt")
    public Boolean stockRestocked(@RequestBody Stock inputStock) {

        try {
                Thread.sleep((long) (1000 * 6));
           	} catch (InterruptedException e) {
                e.printStackTrace();
    	    }

        // Repository 에서 입력 받은 ID로 찾는다.
        Optional<Stock> stockOptional = stockRepository.findById(inputStock.getId());

	    if (!stockOptional.isPresent()) {
            // ID로 못 찾았으면 제품명으로 다시 찾는다.
            stockOptional = stockRepository.findByProductName(inputStock.getProductName());
        }

	    if (stockOptional.isPresent()) {
    	    Stock stock = stockOptional.get();

            // 기존 재고 내역을 찾은 경우 기존 재고에 수량을 더한 후 저장한다.
            stock.setQty(stock.getQty() + inputStock.getQty());
            stockRepository.save(stock);
        } else {  
            // 기존 재고가 없는 경우 입력된 재고 내역을 저장한다.
            stockRepository.save(inputStock);
        }
    }

    // 주문에 의해 재고 출고
    @RequestMapping(method=RequestMethod.PATCH, path="/stocks/reduce")
    public Boolean stockReduced(@RequestBody Stock inputStock) {

        try {
                Thread.sleep((long) (1000 * 6));
           	} catch (InterruptedException e) {
                e.printStackTrace();
    	    }

	    Optional<Stock> stockOptional = stockRepository.findByProductName(inputStock.getProductName());

	    if (stockOptional.isPresent()) {
    	    Stock stock = stockOptional.get();

	        // 주문 숫자가 재고 숫자보다 클 때(재고 부족) 0을 리턴
	        if(stock.getQty() < inputStock.getQty() ) {
	    	    return false;
	        } else {  
                // 재고 차감 후 주문 수량(차감 수량)을 리턴한다.
		        stock.setQty( stock.getQty() - inputStock.getQty() );
                stockRepository.save(stock);

                return true;
	        }
	    } else {
	        // 재고 목록에 없을 때 0을 리턴
	        return false;
	    }
    }

    // 재고 전체 조회
    @RequestMapping(method=RequestMethod.GET, path="/stocks")
    public Iterable<Stock> getAll() {
    	return stockRepository.findAll();
    }

    // Id로 재고 조회
    @RequestMapping(method=RequestMethod.GET, path="/stocks/{id}")
    public Optional<Stock> getOne(@PathVariable("id") Long id) {
    	return stockRepository.findById(id);
    }

    // productName으로 재고 조회
    @RequestMapping(method=RequestMethod.GET, path="/stocks/{productName}")
    public Optional<Stock> getOne(@PathVariable("productName") String productName) {
    	return stockRepository.findByProductName(productName);
    }

    // 재고 입력
    @RequestMapping(method=RequestMethod.POST, path="/stocks")
    public Stock post(@RequestBody Stock stock) {
    	return stockRepository.save(stock);
    }

    // Id로 재고 수정
    @RequestMapping(method=RequestMethod.PATCH, path="/stocks/{id}")
    public Stock patch(@PathVariable("id") Long id, @RequestBody Stock inputStock) {
	    Optional<Stock> stockOptional = stockRepository.findById(id);

    	if(!stockOptional.isPresent()) return null;

	    Stock stock = stockOptional.get();
	    stock.setQty(inputStock.getQty());

	    return stockRepository.save(stock);
    }

    // Id로 재고 삭제
    @RequestMapping(method=RequestMethod.DELETE, path="/stocks/{id}")
    public void delete(@PathVariable("id") Long id) {
    	Optional<Stock> stockOptional = stockRepository.findById(id);

    	if(!stockOptional.isPresent()) return;

    	stockRepository.delete(stockOptional.get());
    }

 }
