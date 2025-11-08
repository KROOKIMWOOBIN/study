package kwb.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    /**
     * @author 김우빈
     * @tudo 아이템 추가
     * @return
     */
    public ResponseEntity<?> addItems() {
        return null;
    }

    /**
     * @author 김우빈
     * @todo 아이템을 삭제하기 전, 사용자에 접근을 방지하기 위해 추가
     * @return
     */
    public ResponseEntity<?> blockItems() {
        return null;
    }

    /**
     * @author 김우빈
     * @tudo 아이템 삭제, 단 block상태인 아이템만 삭제가 가능
     * @return
     */
    public ResponseEntity<?> deleteItems() {
        return null;
    }

}
