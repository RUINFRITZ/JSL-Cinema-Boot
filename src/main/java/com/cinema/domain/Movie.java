package com.cinema.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/** **
 * 映画情報ドメイン (Movie Domain)
 * データベースの 'movie' テーブルとマッピングされるクラスです。
 * 設計されたスキーマに基づいてフィールドを定義しています。
 ** **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    
    /* 映画番号 (Primary Key) */
    private Long mno;           // mno (INT -> Long)

    /* 映画タイトル */
    private String title;       

    /* 映画の内容・あらすじ */
    private String content;     // content (TEXT)

    /* ポスター画像のファイル名 */
    private String poster;      // poster (Default: default.jpg)

    /* 上映時間 (分単位) */
    private Integer runtime;    

    /* 公開日 (YYYY-MM-DD) */
    private String opendate;    

    /* キャッチコピー (宣伝文句) */
    private String catchphrase; 
    
    /* 登録日時 */
    private String regdate;  
    
    /* 平均評価 */
    private Double avgRating;
}