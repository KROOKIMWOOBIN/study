package kwb.store.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

    /**
     * @author 김우빈
     * @todo 전체 아이템 조회
     * @return
     */
    public ResponseEntity<?> getItems() {
        return null;
    }

    /**
     * @author 김우빈
     * @param itemName
     * @todo 특정 아이템 검색
     * @return
     */
    public ResponseEntity<?> findItem(@Param("name") String itemName) {
        return null;
    }

}
