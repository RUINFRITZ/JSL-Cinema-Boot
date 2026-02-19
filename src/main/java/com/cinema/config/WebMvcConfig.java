package com.cinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** **
 * Web MVC 設定クラス (Web MVC Configuration)
 * 静的リソースやファイルアップロードパスのマッピング設定を行います。
 * ブラウザからの URL リクエストをサーバーの物理パスに紐付けます。
 ** **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // application.properties から保存パスを取得 (C:/jsl/poster)
    @Value("${file.upload.path}")
    private String uploadPath;

    /*
     * リソースハンドラーの追加 (Add Resource Handlers)
     * URLパス '/upload/**' へのアクセスを、ローカルディスクの物理パスにマッピングします。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ブラウザから '/upload/**' でアクセスされた場合
        registry.addResourceHandler("/upload/**")
                // ローカルの 'file:///C:/jsl/poster/' を参照する
                .addResourceLocations("file:///" + uploadPath + "/");
    }
}