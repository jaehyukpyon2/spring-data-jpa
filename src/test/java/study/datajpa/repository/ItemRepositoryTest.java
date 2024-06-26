package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void save() throws Exception {
        Item item = new Item("Hello");
        Item savedItem = itemRepository.save(item);
        System.out.println("item == savedItem ? " + (item == savedItem)); // false
    }
}