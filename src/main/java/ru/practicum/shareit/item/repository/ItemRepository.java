package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(Integer id);

    @Query("select i from Item i where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            "or lower(i.description) like lower(concat('%', ?1, '%'))) and is_available = true")
    List<Item> search(String text);

    @Query("SELECT i FROM Item i WHERE i.request.id IN :requestIds")
    List<Item> findAllByRequestIdIn(List<Integer> requestIds);

    List<Item> findAllByRequestId(int requestId);
}
