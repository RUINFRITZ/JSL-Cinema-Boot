package com.cinema.mapper;

import com.cinema.domain.Movie;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/** **
 * 映画マッパーインターフェース (Movie Mapper Interface)
 * データベース(movie)へのCRUD操作を担当します。
 ** **/
@Mapper
public interface MovieMapper {

    /*
     * 映画情報を新規登録します。
     * @param movie 登録する映画情報 (Movie Domain)
     */
    void insertMovie(Movie movie);

    /*
     * 全ての映画リストを取得します。
     * @return 映画リスト
     */
    List<Movie> selectAllMovies();

    /*
     * 映画番号(mno)で詳細情報を取得します。
     * @param mno 映画番号
     * @return 映画情報
     */
    Movie selectMovieById(Long mno);

    /*
     * 映画情報を更新します。
     * @param movie 更新する映画情報
     */
    void updateMovie(Movie movie);

    /*
     * 指定された映画番号の情報を削除します。
     * @param mno 削除する映画番号
     */
    void deleteMovie(Long mno);
}