package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "generator", strategy = "increment")
    @GeneratedValue(generator = "generator")
    private int id;
    @Column(name = "description", length = 300)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;

    public ItemRequest(String description) {
        this.description = description;
    }
}
