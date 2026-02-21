package com.cinema.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** **
 * 予約マッパー
 * 予約情報のデータベース操作(CRUD)および排他制御を担当しますle。
 ** **/
@Mapper
public interface ReservationMapper {

    /*
     * 新規予約情報を登録します。
     *
     * @param sno スケジュール番号
     * @param userid ユーザーID
     * @param seatInfo 選択された座席情報 (例: "A-1,A-2")
     * @param price 決済金額
     */
    void insertReservation(@Param("sno") Long sno, 
                           @Param("userid") String userid, 
                           @Param("seatInfo") String seatInfo, 
                           @Param("price") int price);
    
    /*
     * ログインユーザーの予約履歴(チケット一覧)を取得します。
     *
     * @param userid ユーザーID
     * @return チケット情報のマップリスト
     */
    List<Map<String, Object>> selectMyTickets(@Param("userid") String userid);
    
    /*
     * 指定されたスケジュールにおいて、要請された座席が既に予約されているかを確認します（排他制御）。
     * 悲観的ロック(FOR UPDATE)を適用し、同一座席への重複予約を防止します。
     *
     * @param sno スケジュール番号
     * @param seatList 検証対象の座席リスト
     * @return 競合する予約件数
     */
    int checkExistingReservationForUpdate(@Param("sno") Long sno, @Param("seatList") List<String> seatList);
    
}