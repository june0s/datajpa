package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save() {
        // 1. id를 GeneratedValue 사용하는 경우, 이 시점에는 id 가 생성되지 않는다. db 에 persist 해야 id 가 생성된다.
        // 2. id로 uuid 등 임의의 규칙을 갖는 문자열을 사용하는 경우, DB 에 처음 persist 하는 지 알아야 한다.
        Item item = new Item("A");
        itemRepository.save(item);
    }
}
