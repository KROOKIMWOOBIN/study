package kwb.stock.stock;

import kwb.stock.entity.StockEntity;

public interface StockService {
    void saveStock(StockEntity stock);
    void getNews();
    void getTop5Stock();
}
