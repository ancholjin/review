package org.zerock.review.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.review.dto.PageRequestDTO;
import org.zerock.review.dto.PageResultDTO;
import org.zerock.review.dto.ReviewDTO;
import org.zerock.review.entity.QReview;
import org.zerock.review.entity.Review;
import org.zerock.review.repository.ReviewRepository;

import java.util.Optional;
import java.util.function.Function;

import static org.zerock.review.entity.QReview.review;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;


    @Override
    public Long register(ReviewDTO reviewDTO) {

        Review review = dtoToEntity(reviewDTO);

        reviewRepository.save(review);

        return review.getRnum();
    }



    @Override
    public ReviewDTO get(Long rnum) {
        Optional<Review> result = reviewRepository.findByRnum(rnum);

        if(result.isPresent()){
            return entityToDTO(result.get());
        }
        return null;
    }


    @Override
    public void remove(Long rnum) {

        log.info("remove " + rnum);

        reviewRepository.deleteById(rnum);

    }

    @Override
    public Long modify(ReviewDTO reviewDTO) {

        Review review = dtoToEntity(reviewDTO);

        log.info("===================================");
        log.info(review);

        reviewRepository.save(review);

        return review.getRnum();
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO){//Querydsl처리
        String type = requestDTO.getType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QReview qReview = review;
        String keyword = requestDTO.getKeyword();
        BooleanExpression expression = qReview.rnum.gt(0L);//rnum >0 조건만 생성
        booleanBuilder.and(expression);
        if(type ==null || type.trim().length() == 0){// 검색조건이 없는 경우
            return booleanBuilder;

        }
        // 검색조건 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if(type.contains("t")){
            conditionBuilder.or(qReview.title.contains(keyword));
        }
        if(type.contains("c")){
            conditionBuilder.or(qReview.content.contains(keyword));
        }
        if(type.contains("w")){
            conditionBuilder.or(qReview.writer.contains(keyword));
        }
        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }

   /* @Override
    public PageResultDTO<ReviewDTO,Review> getListSearch(PageRequestDTO requestDTO){
        Pageable pageable = (Pageable) requestDTO.getPageable(Sort.by("rnum")
                .descending());
        BooleanBuilder booleanBuilder = getSearch(requestDTO);// 검색조건 처리
        Page<Review> result = reviewRepository.findAll(booleanBuilder, pageable);
        Function<Review,ReviewDTO> fn = (entity -> entityToDTO(entity));
        // 엔티티객채들을  DTO의 리스트로 변환하고 화면에 페이지 처리와 필요한 값들을 생성합니다
        return new PageResultDTO<>(result, fn);
    }
*/
   @Override
   public PageResultDTO<ReviewDTO, Review> getListSearch(PageRequestDTO requestDTO) {
       Pageable pageable = (Pageable) requestDTO.getPageable(Sort.by("rnum").descending());

       // 키워드를 가져와서 제목과 내용에 대한 검색 조건을 추가
       String keyword = requestDTO.getKeyword();
       BooleanBuilder booleanBuilder = new BooleanBuilder();
       if (keyword != null) {
           booleanBuilder.andAnyOf(
                   review.title.contains(keyword),
                   review.content.contains(keyword),
                   review.writer.contains(keyword)
           );
       }

       Page<Review> result = reviewRepository.findAll(booleanBuilder, pageable);
       Function<Review, ReviewDTO> fn = (entity -> entityToDTO(entity));

       // 엔티티 객체들을 DTO의 리스트로 변환하고 화면에 페이지 처리와 필요한 값들을 생성합니다
       return new PageResultDTO<>(result, fn);
   }
    @Override
    public PageResultDTO<ReviewDTO, Review> getList(PageRequestDTO pageRequestDTO) {
        log.info(pageRequestDTO);
        Function<Review, ReviewDTO> fn = (en -> entityToDTO(en));
        Pageable pageRequest = pageRequestDTO.getPageable(Sort.by("rnum").descending());
        Page<Review> result = reviewRepository.getList((PageRequest) pageRequest);
        return new PageResultDTO<>(result, fn);

    }

}
