package org.zerock.review.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "review")
public class Photo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pnum;

    private String fileName;

    private String uploadPath;

    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    public void makeInvalid(){
        this.review = null;
    }
}
