package org.zerock.review.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.zerock.review.entity.Review;

import java.util.Optional;


public interface ReviewRepository  extends JpaRepository<Review, Long>,
        QuerydslPredicateExecutor<Review> {


    @EntityGraph(attributePaths = "photos" , type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Review r ")
    Page<Review> getList(PageRequest pageRequest);

    @EntityGraph(attributePaths = "photos", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Review> findByRnum(Long rnum);

}
