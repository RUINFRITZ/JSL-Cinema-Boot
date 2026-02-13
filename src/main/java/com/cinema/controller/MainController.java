package com.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * メインコントローラー (Main Controller)
 * 
 * アプリケーションのメインページ (Home) および、
 * ログイン・会員登録ページへの画面遷移 (Routing) を担当します。
 * ビジネスロジックは含まず、View (HTML) を返却する役割に徹します。
 */
@Controller // Spring MVCのコントローラーとしてBean登録
public class MainController {

    /**
     * メインページ表示
     * ルートパス ("/") または "/index" への GET リクエストを処理します。
     *
     * @param model 画面にデータを渡すためのModelオブジェクト (必要に応じて使用)
     * @return "index" (src/main/resources/templates/index.html)
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // ログ出力 (任意): メインページへのアクセスを確認
        // System.out.println("メインページにアクセスしました。");

        // Thymeleaf テンプレートのファイル名を返却 (拡張子 .html は省略)
        return "index";
    }
}