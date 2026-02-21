package com.cinema.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cinema.domain.Member;

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
    
    /*
     * ユーザーの現在の保有ポイントを取得します。
     */
    int selectCurrentPoint(@Param("userid") String userid);
    
    /*
     * 会員等級(member_grade)に基づくポイント積立率を取得します。
     * @param userid ユーザーID
     * @return ポイント積立率 (例: 0.05)
     */
    double selectPointRate(@Param("userid") String userid);

    /*
     * 会員の保有ポイントを更新(加算/減算)します。
     * * @param userid ユーザーID
     * @param amount 変動ポイント (正数:リワード積立, 負数:決済使用)
     */
    void updateMemberPoint(@Param("userid") String userid, @Param("amount") int amount);

    /*
     * ポイント変動履歴をデータベース(point_history)に登録します。
     * @param userid ユーザーID
     * @param amount 変動ポイント
     * @param description 変動内容 (例: "映画チケット決済", "チケット決済に伴うリワード積立")
     */
    void insertPointHistory(@Param("userid") String userid, 
                            @Param("amount") int amount, 
                            @Param("description") String description);
    
    /*
     * 特定ユーザーのポイント変動履歴リストを取得します。
     * 日時の降順(最新順)で返却します。
     * @param userid ユーザーID
     * @return ポイント履歴のマップリスト
     */
    List<Map<String, Object>> selectPointHistory(@Param("userid") String userid);
    
    /*
     * ユーザーの基本情報（氏名、電話番号、メールアドレス）を更新します。
     *
     * @param member 更新する情報が格納されたMemberオブジェクト
     */
    void updateMemberInfo(Member member);
}