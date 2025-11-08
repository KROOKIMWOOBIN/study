package store;

import jakarta.transaction.Transactional;
import kwb.entity.Item;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class ItemTest {

    @Test
    @Transactional
    public void 아이템생성() {
        Item item = new Item("축구공", "물품", 100, 500);
    }

}
