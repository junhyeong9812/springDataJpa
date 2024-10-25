package study.data_jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.data_jpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {
    @Autowired ItemRepository itemRepository;
    @Test
    public void save(){
//        Item item =new Item();
//        itemRepository.save(item);
        //이때 id값을 세팅하지 않으면 null이기 때문에
        //새로운 객체로 판단하고
        //save 이후 제너럴 벨류를 통해 persist이후에 그때 id가 들어가서 null이 아니게 된다.

        //이때 엔티티에서 GeneratedValue를 안쓴다면?
        Item item =new Item("A");
        itemRepository.save(item);
        //이렇게 직접 pk값을 임의로 넣어준다면
        //save를 호출할 때 isNew가 null이 아니게 되서 merge가 호출된다.
        //이때 merge는 DB에 데이터가 있다는 가정하에 동작하게 된다.
        //그래서 우선 select문을 통해 where절로 찾아오고
        //없으면 새것으로 판단해서 새롭게 insert문이 나가서 비효율적이다.
        //또한 데이터의 저장은 persist 변경은 더티체크를 통해 저장하는 게 좋다.
        //그래서 기본적으로 merge를 안쓰는 게 좋다.
        // @GeneratedValue를 못사용할 때가 존재하는데
        // 이럴때는 Persistable인터페이스를 지원한다.

        //Persistable<String>를 추가한다면
        //persist할때 값이 세팅을 isNew를 통해 createdDate를 활용하여 자주 사용한다.
        //될 수 있으면 @GenerateValue를 사용하는 게 좋다.
        //직접 할당해야 된다면 Persisable을 활용하는 게 좋다.



    }
}