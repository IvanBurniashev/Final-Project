package com.example.finalproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favorites {

    @Id
    @Column(name = "FavoriteID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="ProductID", nullable=false)
//    private Products products;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="UserID", nullable=false)
//    private Users users;

}
