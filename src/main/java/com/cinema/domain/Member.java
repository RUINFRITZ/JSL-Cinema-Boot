package com.cinema.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** **
 * 会員 (Member) エンティティ
 * データベースの 'member_cinema' テーブルとマッピングされるドメインオブジェクトです。
 * Spring Securityの認証プロセスおよびMyBatisによるデータ永続化に使用されます。
 *
 * @version 1.0
 ** **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    /*
     * 内部管理用ID (PK)
     * 自動採番 (AUTO_INCREMENT) される主キーです。
     * ビジネスロジック上は使用されませんが、データの一意性を保証するために存在します。
     */
    private Long id;

    /*
     * ログインID
     * ユーザーがログイン時に入力する識別子です。
     * データベース上でユニーク制約 (UNIQUE) が設定されています。
     */
    private String userid;

    /*
     * パスワード
     * Spring Securityの BCryptPasswordEncoder によって暗号化された文字列を格納します。
     * セキュリティ強化のため、平文での保存は厳禁です。
     */
    private String password;

    /*
     * 氏名
     * ユーザーのフルネームを保管します。
     */
    private String name;

    /*
     * メールアドレス
     * 連絡用およびパスワードリセット時などに使用されます。
     */
    private String email;

    /*
     * 電話番号
     * ハイフン付き、またはハイフンなしの形式で格納されます。
     */
    private String phone;

    /*
     * 保有ポイント
     * ユーザーが現在保有しているポイント残高です。初期値は0です。
     */
    private int point;

    /*
     * 会員ランクコード (FK)
     * 'member_grade' テーブルの主キーと紐付きます。
     * (例: 1=一般, 2=VIP, 3=VVIP)
     */
    private int mgrade;

    /*
     * 権限ロール
     * Spring Securityの認可プロセスで使用されるロール名です。
     * (例: "ROLE_USER", "ROLE_ADMIN")
     */
    private String role;

    /*
     * アカウント有効ステータス
     * アカウントが有効かどうかを示すフラグです。
     * true (1): 有効, false (0): 無効 (退会済みまたは凍結)
     */
    private boolean enabled;

    /*
     * 登録日時
     * アカウントが作成されたシステム日時です。
     */
    private LocalDateTime regdate;

    /*
     * 更新日時 / 削除日時
     * アカウント情報が更新、または論理削除された日時を記録します。
     * null の場合は有効なアクティブユーザーであることを示します。
     */
    private LocalDateTime deldate;
}