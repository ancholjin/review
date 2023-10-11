package org.zerock.review.service;

import org.zerock.review.dto.PageRequestDTO;
import org.zerock.review.dto.PageResultDTO;
import org.zerock.review.dto.PhotoDTO;
import org.zerock.review.dto.ReviewDTO;
import org.zerock.review.entity.Photo;
import org.zerock.review.entity.Review;

import java.util.ArrayList;
import java.util.HashSet;

public interface ReviewService {

    Long register(ReviewDTO reviewDTO);

    PageResultDTO<ReviewDTO,Review> getList(PageRequestDTO pageRequestDTO);
    PageResultDTO<ReviewDTO,Review> getListSearch(PageRequestDTO requestDTO);

    ReviewDTO get(Long rnum);

    void remove(Long rnum);

    Long modify(ReviewDTO reviewDTO);

    default Review dtoToEntity(ReviewDTO reviewDTO){

        Review review = Review.builder()
                .rnum(reviewDTO.getRnum())
                .title(reviewDTO.getTitle())
                .content(reviewDTO.getContent())
                .writer(reviewDTO.getWriter())
                .photos(new HashSet<>())
                .build();

        ArrayList<PhotoDTO> photoDTOList = reviewDTO.getPhotos();

        if(photoDTOList != null && photoDTOList.size() > 0){

            for (PhotoDTO photoDTO : photoDTOList) {
                Photo photo = Photo.builder()
                        .pnum(photoDTO.getPnum())
                        .fileName(photoDTO.getFileName())
                        .uuid(photoDTO.getUuid())
                        .review(review)
                        .uploadPath(photoDTO.getUploadPath()).build();

                review.addPhoto(photo);
            }
        }
        return review;
    }

    default ReviewDTO entityToDTO(Review review) {

        ReviewDTO dto = ReviewDTO.builder()
                .rnum(review.getRnum())
                .content(review.getContent())
                .title(review.getTitle())
                .writer(review.getWriter())
                .regDate(review.getRegDate())
                .modDate(review.getModDate())
                .build();

        if (review.getPhotos() != null && review.getPhotos().size() > 0) {

            ArrayList<PhotoDTO> photoDTOArrayList = new ArrayList<>();

            review.getPhotos().stream().sorted((p1, p2) -> p1.getPnum() > p2.getPnum() ? -1 : 1)
                    .forEach(photo -> {

                        photoDTOArrayList.add(
                                PhotoDTO.builder().pnum(photo.getPnum())
                                        .fileName(photo.getFileName())
                                        .uuid(photo.getUuid())
                                        .uploadPath(photo.getUploadPath())
                                        .build());
                    });
            dto.setPhotos(photoDTOArrayList);
        }
        return dto;
    }



}
