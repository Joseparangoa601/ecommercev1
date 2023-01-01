package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Review;
import com.convertic.ecommerce.repository.ProductRepository;
import com.convertic.ecommerce.repository.ReviewRepository;
import com.convertic.ecommerce.web.dto.ReviewDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@ApplicationScoped
@Transactional
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ProductRepository productRepository;
    public static ReviewDto mapToDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getTitle(),
                review.getDescription(),
                review.getRating()
        );
    }

    public List<ReviewDto> findReviewsByProductId(Long id){
       return reviewRepository.findReviewsByProductId(id)
                .stream()
                .map(ReviewService::mapToDto)
                .collect(Collectors.toList());
    }

    public ReviewDto findById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewService::mapToDto)
                .orElse(null);
    }

    public ReviewDto create(ReviewDto reviewDto, Long productId){
        log.debug("Request to create Review : {} ofr the Product {}", reviewDto, productId);
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product with ID:" + productId + " was not found !"));

        var savedReview = reviewRepository.save(
                new Review(
                        reviewDto.getTitle(),
                        reviewDto.getDescription(),
                        reviewDto.getRating()
                )
        );
        product.getReviews().add(savedReview);
        productRepository.save(product);
        return mapToDto(savedReview);
    }

    public void delete(Long reviewId) {
        log.debug("Request to delete Review : {}", reviewId);
        var review= reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("Product with ID:" + reviewId + " was not found !"));

        var product = productRepository.findProductByReviewId(reviewId);
        product.getReviews().remove(review);
        this.productRepository.save(product);
        this.reviewRepository.delete(review);
    }
}
