package com.cinema.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/** **
 * 管理者ダッシュボード データアクセスオブジェクト (DAO)
 * MyBatisのマッパーインターフェースです。
 * ダッシュボードの統計情報およびチャート描画用のデータ抽出を担当します。
 * src/main/resources/mapper/AdminMapper.xml ファイルとマッピングされます。
 ** **/
@Mapper
public interface AdminMapper {

    /*
     * 本日の売上合計を取得
     * 今日の日付で決済完了（PAID）となった予約データの合計金額を算出します。
     * データが存在しない場合のNullPointerExceptionを防ぐため、XML側でCOALESCE処理を行っています。
     *
     * @return 本日の売上合計 (Integer)
     */
    Integer getTodayRevenue();

    /*
     * 総会員数を取得
     * システムに登録されている全会員（member_cinema）の数をカウントします。
     *
     * @return 総会員数 (Integer)
     */
    Integer getTotalMembers();

    /*
     * 登録された映画数を取得
     * 現在管理されている映画（movie）の総数をカウントします。（ダッシュボードの上映中映画数として活用）
     *
     * @return 映画の総数 (Integer)
     */
    Integer getActiveMovies();

    /*
     * 最新の予約リストを取得
     * ダッシュボードの「最新の予約状況」テーブルに表示するデータを抽出します。
     * 予約(reservation)、スケジュール(schedule)、映画(movie)テーブルをJOINし、
     * 作品名を含めた最新の決済履歴5件を取得します。
     *
     * @return 最新の予約データのリスト (List of Map<String, Object>)
     */
    List<Map<String, Object>> getRecentReservations();

    /*
     * 直近7日間の日別売上集計
     * チャート描画用（Chart.js等）の非同期APIで使用されます。
     * 予約テーブルから過去7日間の決済完了（PAID）データを日付ごとにグループ化し、
     * 日付（res_date）と売上合計（daily_total）のマップリストを返します。
     *
     * @return 抽出された日別売上データのリスト (List of Map<String, Object>)
     */
    List<Map<String, Object>> selectWeeklyRevenue();
    
    /*
     * すべての予約リストを取得
     * 管理者向けに全体の決済履歴および予約状況を照会します。
     *
     * @return 全予約データのリスト (List of Map<String, Object>)
     */
    List<Map<String, Object>> getAllReservations();
}