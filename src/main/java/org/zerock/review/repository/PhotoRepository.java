package org.zerock.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.zerock.review.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {


}
