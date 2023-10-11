package org.zerock.review.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "photos")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rnum;
    private String title;
    private String content;

    private String writer;


    @OneToMany(mappedBy = "review" ,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Set<Photo> photos;

    public void addPhoto(Photo photo){
        photos.add(photo);
    }



    public void deletePhoto(Photo photo){

        Optional<Photo> result = photos.stream()
                .filter(p -> p.getPnum().equals(photo.getPnum()))
                .findFirst();

        if(result.isPresent()){
            Photo target = result.get();
            target.makeInvalid();
            photos.remove(target);
        }
    }
}
