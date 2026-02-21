package com.cinema.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** **
 * レビュー ドメインクラス
 * 映画に対する星評価とコメント情報を保持するデータオブジェクトです。
 ** **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    // レビュー番号 (Primary Key, Auto Increment)
    private Long rev_no;

    // 映画番号 (Foreign Key)
    private Long mno;

    // ユーザーID (Foreign Key)
    private String userid;

    // 星評価 (1~5点)
    private Integer star;

    // レビュー内容
    private String comments;

    // 登録日時
    private LocalDateTime regdate;

}