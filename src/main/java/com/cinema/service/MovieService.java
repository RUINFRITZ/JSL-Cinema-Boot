package com.cinema.service;

import com.cinema.domain.Movie;
import com.cinema.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/** **
 * 映画管理サービス 
 * 映画情報の登録・修正・削除およびポスター画像のアップロード処理を担当します。
 * トランザクション管理 (@Transactional) を適用し、DBとファイルの一貫性を保ちます。
 ** **/
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieMapper movieMapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    /*
     * 映画リスト全件取得
     * @return 映画リスト
     */
    public List<Movie> getMovieList() {
        return movieMapper.selectAllMovies();
    }

    /*
     * 映画詳細情報取得
     * @param mno 映画番号 (PK)
     * @return 映画情報
     */
    public Movie getMovieDetail(Long mno) {
        return movieMapper.selectMovieDetail(mno);
    }

    /*
     * 映画登録 (ファイルアップロード含む)
     * ポスター画像がある場合、サーバーに保存し、そのファイル名をDBに記録します。
     *
     * @param movie 映画情報
     * @param file アップロードファイル (ポスター)
     * @throws IOException ファイル保存失敗時
     */
    @Transactional
    public void registerMovie(Movie movie, MultipartFile file) throws IOException {
        // ファイルが存在する場合、アップロード処理を実行
        if (file != null && !file.isEmpty()) {
            String savedFileName = uploadFile(file);
            movie.setPoster(savedFileName); // UUID_OriginalName -> DB: poster
        } else {
            // ファイルがない場合、デフォルト画像を設定 (DB Default制約があるが、念のため)
            movie.setPoster("default.jpg");
        }

        // DBに映画情報を保存
        movieMapper.insertMovie(movie);
        log.info(" - New Movie Registered: {}", movie.getTitle());
    }

    /*
     * 映画情報修正
     * 新しいファイルがアップロードされた場合、既存のファイルを削除し、新しいファイルを保存します。
     *
     * @param movie 修正する映画情報
     * @param file 新しいポスターファイル (任意)
     * @throws IOException ファイル処理失敗時
     */
    @Transactional
    public void modifyMovie(Movie movie, MultipartFile file) throws IOException {
        // 1. 新しいファイルがあるか確認
        if (file != null && !file.isEmpty()) {
            // 既存の映画情報を取得 (古いファイル名を知るため)
            Movie oldMovie = movieMapper.selectMovieDetail(movie.getMno());
            
            // 既存ファイルがあれば削除 (default.jpg は削除しない)
            if (oldMovie.getPoster() != null && !"default.jpg".equals(oldMovie.getPoster())) {
                deleteFile(oldMovie.getPoster());
            }

            // 新しいファイルをアップロード
            String savedFileName = uploadFile(file);
            movie.setPoster(savedFileName);
        }

        // 2. DB更新
        movieMapper.updateMovie(movie);
        log.info(" - Movie Modified: {}", movie.getMno());
    }

    /*
     * 映画削除
     * DBのデータだけでなく、サーバーに保存されたポスター画像も削除します。
     *
     * @param mno 削除対象の映画番号
     */
    @Transactional
    public void deleteMovie(Long mno) {
        // 削除前にファイル情報を取得
        Movie movie = movieMapper.selectMovieDetail(mno);

        // ファイルが存在すれば物理削除 (default.jpg は削除しない)
        if (movie != null && movie.getPoster() != null && !"default.jpg".equals(movie.getPoster())) {
            deleteFile(movie.getPoster());
        }

        // DBから削除
        movieMapper.deleteMovie(mno);
        log.info(" - Movie Deleted: {}", mno);
    }

    /*
     * [内部メソッド] ファイルアップロード処理
     *
     * @param file アップロードされたファイル
     * @return 保存された一意のファイル名 (UUID_OriginalName)
     * @throws IOException IOエラー
     */
    private String uploadFile(MultipartFile file) throws IOException {
        // アップロードディレクトリの確認・生成
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // ユニークなファイル名を生成 (UUID + 元の拡張子維持)
        String originalName = file.getOriginalFilename();
        // UUID生成
        String uniqueName = UUID.randomUUID().toString() + "_" + originalName;

        // 保存パス生成
        Path savePath = Paths.get(uploadPath, uniqueName);

        // ファイル保存
        file.transferTo(savePath.toFile());

        return uniqueName;
    }

    /*
     * [内部メソッド] ファイル物理削除処理
     *
     * @param fileName 削除するファイル名
     */
    private void deleteFile(String fileName) {
        File file = new File(uploadPath, fileName);
        if (file.exists()) {
        	if (file.delete()) {
                log.info(" - File deleted successfully: {}", fileName);
            } else {
                // 削除に失敗した場合のみ警告ログを出力するように修正
                log.warn(" * Failed to delete file: {}", fileName);
            }
        }
    }
}