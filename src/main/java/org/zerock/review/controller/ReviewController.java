package org.zerock.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.review.dto.PageRequestDTO;
import org.zerock.review.dto.PageResultDTO;
import org.zerock.review.dto.ReviewDTO;
import org.zerock.review.entity.Review;
import org.zerock.review.service.ReviewService;

@Controller
@RequestMapping("/review/")
@RequiredArgsConstructor
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/register")
    public void register(){

        log.info("register get..");

    }

    @PostMapping("/register")
    public String registerPost(ReviewDTO reviewDTO, RedirectAttributes redirectAttributes){

        log.info("register post reviewDTO: " + reviewDTO);

        Long rnum = reviewService.register(reviewDTO);

        redirectAttributes.addFlashAttribute("msg", rnum);

        return "redirect:/review/list";
    }

    @GetMapping(value = {"/read","/modify"})
    public void read(Long rnum,
                     @ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO,
                     Model model){

        log.info("read rnum:  " + rnum);

        ReviewDTO reviewDTO = reviewService.get(rnum);

        model.addAttribute("dto",reviewDTO );

    }

    @PostMapping("/remove")
    public String remove(Long rnum, RedirectAttributes redirectAttributes){

        log.info("remove: " + rnum);

        reviewService.remove(rnum);

        redirectAttributes.addFlashAttribute("msg","removed");


        return "redirect:/review/list";
    }


    @PostMapping("/modify")
    public String modifyPost(ReviewDTO reviewDTO,
                             PageRequestDTO pageRequestDTO,
                             RedirectAttributes redirectAttributes){

        log.info("modify post reviewDTO: " + reviewDTO);

        Long rnum = reviewService.modify(reviewDTO);

        redirectAttributes.addAttribute("rnum", rnum);
        redirectAttributes.addAttribute("page", pageRequestDTO.getPage());

        return "redirect:/review/read";
    }

    @GetMapping("/")
    public String index(){
        return"redirect:/review/list";
    }

}











