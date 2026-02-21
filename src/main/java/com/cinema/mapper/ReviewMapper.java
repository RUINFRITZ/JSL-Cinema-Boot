package com.cinema.mapper;

import com.cinema.domain.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** **
 * レビューマッパー
 * 映画に対する星評価およびコメントの管理を担当します。
 ** **/
@Mapper
public interface ReviewMapper {

    /*
     * 特定の映画に対するレビューリストを照会
     */
    List<Map<String, Object>> selectReviewsByMno(@Param("mno") Long mno);

    /*
     * 新規レビューを登録します。
     */
    void insertReview(Review review);

    /*
     * レビューを削除（本人確認ロジックを含む）
     * @param revNo レビュー番号
     * @param userid ログイン中のユーザーID
     */
    void deleteReview(@Param("revNo") Long revNo, @Param("userid") String userid);
    
    /*
     * レビューを修正（本人確認ロジックを含む）
     * 
     * @param revNo レビュー番号
     * @param userid ログイン中のユーザーID
     * @param star 修正後の星評価
     * @param comments 修正後のコメント
     */
    void updateReview(@Param("revNo") Long revNo, 
                      @Param("userid") String userid, 
                      @Param("star") Integer star, 
                      @Param("comments") String comments);
}