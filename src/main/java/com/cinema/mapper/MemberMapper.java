package com.cinema.mapper;

import com.cinema.domain.Member;
import org.apache.ibatis.annotations.Mapper;

/** **
 * 会員 (Member) データアクセスオブジェクト (DAO)
 * MyBatisのマッパーインターフェースです。
 * データベースに対する CRUD 操作および会員情報の検索を担当します。
 * src/main/resources/mapper/MemberMapper.xml ファイルとマッピングされます。
 ** **/
@Mapper
public interface MemberMapper {

    /*
     * 新規会員登録
     * ユーザーが入力した会員情報をデータベースに保存（INSERT）します。
     * パスワードは必ず暗号化された状態で渡される必要があります。
     *
     * @param  member 保存する会員ドメインオブジェクト
     * @return 処理された行数 (1であれば成功)
     */
    int insertMember(Member member);

    /*
     * ログインIDによる会員情報検索
     * Spring Securityの認証プロセス (UserDetailsService) で使用されます。
     * 指定された userid (ログインID) に一致する会員情報を取得します。
     *
     * @param  userid 検索するログインID (Unique Key)
     * @return 一致する会員情報 (存在しない場合は null)
     */
    Member findByUsername(String userid);

    /*
     * ID重複チェック
     * 会員登録時に、入力されたIDが既に使用されているかを確認します。
     *
     * @param  userid 確認対象のログインID
     * @return 存在する場合は 1、存在しない場合は 0
     */
    int existsById(String userid);
}