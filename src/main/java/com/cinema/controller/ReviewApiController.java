package com.cinema.controller;

import com.cinema.domain.Review;
import com.cinema.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewMapper reviewMapper;

    // レビューリスト取得
    @GetMapping("/list/{mno}")
    public ResponseEntity<List<Map<String, Object>>> getList(@PathVariable("mno") Long mno) {
        return ResponseEntity.ok(reviewMapper.selectReviewsByMno(mno));
    }

    // レビュー登録
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Review review, Principal principal) {
        if (principal == null) return ResponseEntity.status(403).build();
        
        review.setUserid(principal.getName());
        reviewMapper.insertReview(review);
        return ResponseEntity.ok("success");
    }
    
    /*
     * レビューの削除を実行します。
     * ログイン中のユーザーIDをMapperに渡し、本人の書き込みのみ削除されるよう制御します。
     */
    @DeleteMapping("/delete/{revNo}")
    public ResponseEntity<String> delete(@PathVariable("revNo") Long revNo, Principal principal) {
        if (principal == null) return ResponseEntity.status(403).build();

        String userid = principal.getName();
        reviewMapper.deleteReview(revNo, userid);
        
        return ResponseEntity.ok("success");
    }
    
    /*
     * レビューの修正を実行します。
     * PUTメソッドを利用し、サーバー側でも再度本人確認を実施した上でデータを更新します。
     */
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody Review review, Principal principal) {
        // 1. 未ログインユーザーのアクセスを遮断
        if (principal == null) return ResponseEntity.status(403).build();

        // 2. ログイン中のユーザーIDを取得
        String userid = principal.getName();
        
        // 3. Mapperへ渡し、DBの更新を実行 (本人の書き込みのみ更新される)
        reviewMapper.updateReview(review.getRev_no(), userid, review.getStar(), review.getComments());
        
        return ResponseEntity.ok("success");
    }
}