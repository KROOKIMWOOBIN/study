package kwb.stock.repository;

import kwb.stock.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {
    List<StockEntity> findStockAndTop5();
}
