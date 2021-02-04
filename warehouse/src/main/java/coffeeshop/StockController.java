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

    // ��ǰ �԰� (���� ��� �߰�)
    @RequestMapping(method=RequestMethod.PATCH, path="/receipt")
    public Boolean stockRestocked(@RequestBody Stock inputStock) {

        try {
                Thread.sleep((long) (1000 * 6));
           	} catch (InterruptedException e) {
                e.printStackTrace();
    	    }

        // Repository ���� �Է� ���� ID�� ã�´�.
        Optional<Stock> stockOptional = stockRepository.findById(inputStock.getId());

	    if (!stockOptional.isPresent()) {
            // ID�� �� ã������ ��ǰ������ �ٽ� ã�´�.
            stockOptional = stockRepository.findByProductName(inputStock.getProductName());
        }

	    if (stockOptional.isPresent()) {
    	    Stock stock = stockOptional.get();

            // ���� ��� ������ ã�� ��� ���� ��� ������ ���� �� �����Ѵ�.
            stock.setQty(stock.getQty() + inputStock.getQty());
            stockRepository.save(stock);
        } else {  
            // ���� ��� ���� ��� �Էµ� ��� ������ �����Ѵ�.
            stockRepository.save(inputStock);
        }
    }

    // �ֹ��� ���� ��� ���
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

	        // �ֹ� ���ڰ� ��� ���ں��� Ŭ ��(��� ����) 0�� ����
	        if(stock.getQty() < inputStock.getQty() ) {
	    	    return false;
	        } else {  
                // ��� ���� �� �ֹ� ����(���� ����)�� �����Ѵ�.
		        stock.setQty( stock.getQty() - inputStock.getQty() );
                stockRepository.save(stock);

                return true;
	        }
	    } else {
	        // ��� ��Ͽ� ���� �� 0�� ����
	        return false;
	    }
    }

    // ��� ��ü ��ȸ
    @RequestMapping(method=RequestMethod.GET, path="/stocks")
    public Iterable<Stock> getAll() {
    	return stockRepository.findAll();
    }

    // Id�� ��� ��ȸ
    @RequestMapping(method=RequestMethod.GET, path="/stocks/{id}")
    public Optional<Stock> getOne(@PathVariable("id") Long id) {
    	return stockRepository.findById(id);
    }

    // productName���� ��� ��ȸ
    @RequestMapping(method=RequestMethod.GET, path="/stocks/{productName}")
    public Optional<Stock> getOne(@PathVariable("productName") String productName) {
    	return stockRepository.findByProductName(productName);
    }

    // ��� �Է�
    @RequestMapping(method=RequestMethod.POST, path="/stocks")
    public Stock post(@RequestBody Stock stock) {
    	return stockRepository.save(stock);
    }

    // Id�� ��� ����
    @RequestMapping(method=RequestMethod.PATCH, path="/stocks/{id}")
    public Stock patch(@PathVariable("id") Long id, @RequestBody Stock inputStock) {
	    Optional<Stock> stockOptional = stockRepository.findById(id);

    	if(!stockOptional.isPresent()) return null;

	    Stock stock = stockOptional.get();
	    stock.setQty(inputStock.getQty());

	    return stockRepository.save(stock);
    }

    // Id�� ��� ����
    @RequestMapping(method=RequestMethod.DELETE, path="/stocks/{id}")
    public void delete(@PathVariable("id") Long id) {
    	Optional<Stock> stockOptional = stockRepository.findById(id);

    	if(!stockOptional.isPresent()) return;

    	stockRepository.delete(stockOptional.get());
    }

 }
