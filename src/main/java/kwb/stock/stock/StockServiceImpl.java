package kwb.stock.stock;

import kwb.stock.entity.StockEntity;
import lombok.RequiredArgsConstructor;
import kwb.stock.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService{

    private final StockRepository repository;

    /**
     * @author 김우빈
     * @todo 주식을 저장한다.
     */
    @Override
    public void saveStock(StockEntity stock) {
        try {
            repository.save(stock);
        } catch(Exception e) {
            throw new RuntimeException("saveStock Error : " + e);
        }
    }

    /**
     * @author 김우빈
     * @todo 매일 주식과 관련된 뉴스를 가져온다.
     */
    @Override
    @Scheduled
    public void getNews() {
        
    }

    /**
     * @author 김우빈
     * @todo 상위 탑 5개에 성장 가능성이 높은 주식을 가져온다.
     * @mission 긍정도(당근)를 기반으로
     */
    @Override
    public void getTop5Stock() {
        List<StockEntity> stocks = repository.findStockAndTop5();
    }
}