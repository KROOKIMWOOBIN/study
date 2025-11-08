package kwb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Item {
    @Id
    private final String name;
    private final String category;
    private int count;
    private int price;
    private boolean block;

    public Item(String name, String category, int count, int price) {
        checkItemName(name);
        checkItemCategory(category);
        checkItemCount(count);
        checkItemPrice(price);
        this.name = name;
        this.category = category;
        this.count = count;
        this.price = price;
    }

    public void updateBlock() {
        this.block = !this.block;
    }

    public void updateCount(int count) {
        checkItemCount(count);
        this.count = count;
    }

    public void updatePrice(int price) {
        checkItemPrice(price);
        this.price = price;
    }

    private void checkItemName(String name) {
        if (name.isEmpty()) {
            throw new RuntimeException("상품명은 비어있을 수 없습니다.");
        }
    }

    private void checkItemCategory(String category) {
        if (category.isEmpty()) {
            throw new RuntimeException("비정상적인 항목입니다.");
        }
    }

    private void checkItemCount(int count) {
        if (count < 0) {
            throw new RuntimeException("상품 수량은 0개 이상이어야 합니다.");
        }
    }

    private void checkItemPrice(int price) {
        if (count < 0) {
            throw new RuntimeException("상품 가격은 0원 이상이어야 합니다.");
        }
    }

}
