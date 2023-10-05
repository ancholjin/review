package org.zerock.review.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.review.entity.Photo;
import org.zerock.review.entity.Review;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class ReviewAndPhotoTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    public void insertEach(){
        Review review = Review.builder().title("Test").content("Content").build();

        //Review객체를 먼저 저장 Review의 rnum번호를 얻기 위해
        reviewRepository.save(review);


        IntStream.rangeClosed(1,3).forEach(i -> {
            Photo photo = Photo.builder().fileName("test1.jpg").review(review).build();

            photoRepository.save(photo);
        });

        System.out.println(review);
    }

    @Test
    public void insertWithParent() {
        Review review = Review.builder().title("Test").content("Content").photos(new HashSet<>()).build();

        IntStream.rangeClosed(1, 3).forEach(i -> {
            Photo photo = Photo.builder().fileName("test1.jpg").review(review).build();

            review.addPhoto(photo);
        });

        reviewRepository.save(review);
    }

    @Test
    public void insertDummies() {

        IntStream.rangeClosed(1,100).forEach(i -> {

            Review review = Review.builder().title("한글..."+i).content("Content..." + i).photos(new HashSet<>()).build();

            IntStream.rangeClosed(1, 3).forEach(num -> {
                Photo photo = Photo.builder().fileName("test1.jpg").review(review).build();

                review.addPhoto(photo);
            });
            reviewRepository.save(review);

        });

    }

    @Transactional
    @Test
    public void testLazyLoad() {
        
        //존재하는 번호로 테스트할 것 
        Optional<Review> result = reviewRepository.findById(2L);

        Review review = result.get();
        
        System.out.println(review);

        System.out.println("----------------------");

        //Collection 조회
        System.out.println(review.getPhotos());
        
    }

    @Transactional
    @Test
    public void testLazyLoadPaging() {

        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("rnum").descending());

        Page<Review> result = reviewRepository.findAll(pageRequest);

        result.forEach( review -> {
            System.out.println(review);

            System.out.println(review.getPhotos());

            System.out.println("-------------");
        });

    }

    @Test
    public void testEntityGraph() {

        //존재하는 번호로 테스트할 것
        Optional<Review> result = reviewRepository.findByRnum(2L);

        Review review = result.get();

        System.out.println(review);

        System.out.println("----------------------");

        //Collection 조회
        System.out.println(review.getPhotos());

    }


    @Test
    public void testEntityGraphPaging() {

        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("rnum").descending());

        Page<Review> result = reviewRepository.getList(pageRequest);

        result.forEach( review -> {
            System.out.println(review);

            System.out.println(review.getPhotos());

            System.out.println("-------------");
        });
    }

    @Test
    public void deleteReview(){

        reviewRepository.deleteById(3L);

    }

    @Test
    public void deletePhoto() {

        Review review = reviewRepository.findByRnum(2L).get();

        System.out.println("Before...................");
        System.out.println(review);
        System.out.println(review.getPhotos());

        Photo photo = Photo.builder().pnum(5L).build();
        review.deletePhoto(photo);

        reviewRepository.save(review);

        System.out.println("After...................");
        System.out.println(review);
        System.out.println(review.getPhotos());
    }




}
