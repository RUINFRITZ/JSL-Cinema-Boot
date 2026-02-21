package com.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cinema.mapper.MovieMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** **
 * メインコントローラー 
 * 
 * * アプリケーションのメインページ (Home) および、
 * ログイン・会員登録ページへの画面遷移 (Routing) を担当します。
 * ビジネスロジックは含まず、View (HTML) を返却する役割に徹します。
 ** **/
@Controller
@RequiredArgsConstructor // 
@Slf4j                   //
public class MainController {

    // MovieMapperをDI（依存性注入）してデータベースにアクセスします
    // @RequiredArgsConstructor により、コンストラクタ経由で安全に注入されます。
    private final MovieMapper movieMapper;
    
    /*
     * メインページ表示
     * ルートパス ("/") または "/index" への GET リクエストを処理します。
     *
     * @param model 画面にデータを渡すためのModelオブジェクト
     * @return "index" (src/main/resources/templates/index.html)
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        log.info(" - User: Main Page Accessed");

        try {
            // DBから全ての映画リストを取得し、"movieList"というキーでViewに渡します
            model.addAttribute("movieList", movieMapper.selectAllMovies());
        } catch (Exception e) {
            log.error("メイン画面の映画リスト取得中にエラーが発生しました。", e);
        }

        return "index";
    }
}