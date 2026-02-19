package com.cinema.domain;

import lombok.Data;
import java.time.LocalDateTime;

/** **
 * 上映スケジュール ドメイン
 * 映画とスクリーンを繋ぎ、上映日時を管理します。
 ** **/
@Data
public class Schedule {
    private Long sno;           // スケジュール番号	(PK)
    private Long mno;           // 映画番号 		(FK)
    private Long tno;           // スクリーン番号 	(FK)
    private String sdate;       // 上映開始日時 (HTMLのdatetime-localに合わせてStringで受けるか、LocalDateTimeを使用)
    
    // JOIN時の追加情報用 (登録時には不要ですが、後でリスト表示する際に便利です)
    private String movieTitle;
    private String theaterName;
}