package com.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/** **
 * Spring Security 設定クラス
 * アプリケーション全体のセキュリティポリシーを定義します。
 * 最新の Spring Security 6 (Lambda DSL) スタイルで記述されています。
 ** **/
@Configuration
@EnableWebSecurity 		// Spring Security のウェブセキュリティサポートを有効化
@EnableMethodSecurity 	// @PreAuthorize などのメソッドレベルのセキュリティアノテーションを有効化
public class SecurityConfig {

    /*
     * パスワードエンコーダーの Bean 定義
     * BCrypt ハッシュ関数を使用してパスワードを暗号化します。
     * 強力なハッシュ化により、万が一 DB が漏洩してもパスワードの解読を困難にします。
     * @return BCryptPasswordEncoder インスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * セキュリティフィルターチェーンの定義
     * HTTP リクエストに対するセキュリティルール (認証・認可、ログイン、ログアウト) を設定します。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http
            // 1. CSRF (Cross-Site Request Forgery) 保護設定
            // 開発中は無効化することもありますが、本番環境ではセキュリティのため有効化が推奨されます。
            // ここでは一旦無効化 (disable) して開発を進めやすくします。
            .csrf(csrf -> csrf.disable())

            // 2. HTTP リクエストの認可設定 (URLごとのアクセス権限)
            .authorizeHttpRequests(auth -> auth
                // CSS, JS, 画像などの静的リソースは認証なしでアクセス許可 (permitAll)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/upload/**").permitAll()
                
                // 公開ページ (メイン、ログイン、会員登録) は誰でもアクセス可能
                .requestMatchers("/", "/member/login", "/member/register", "/movie/**", "/api/review/list/**", "/support/**").permitAll()
                .requestMatchers("/ticket/**", "/member/update").authenticated()
                // URLが '/admin/' で始まるリクエストは 'ADMIN' 権限を持つユーザーのみアクセス可能
                // 一般ユーザーがアクセスしようとすると 403 (Forbidden) エラーまたはログイン画面へ転送されます
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // その他のすべてのリクエストは認証 (ログイン) が必要
                .anyRequest().authenticated()
            )

            // 3. フォームログイン設定
            .formLogin(form -> form
                .loginPage("/member/login")                 // カスタムログインページの URL
                .loginProcessingUrl("/member/login")        // ログイン処理を実行する URL (HTMLフォームの action と一致させる)
                .defaultSuccessUrl("/?welcome=true", true)	// ログイン成功時のリダイレクト先
                .failureUrl("/member/login?error=true")     // ログイン失敗時のリダイレクト先
                .usernameParameter("userid")        		// ログインフォームのユーザー名 input の name 属性
                .passwordParameter("password")     			// ログインフォームのパスワード input の name 属性
                .permitAll()
            )

            // 4. ログアウト設定
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // ログアウト処理の URL
                .logoutSuccessUrl("/member/login?logout=true") // ログアウト成功時のリダイレクト先
                .invalidateHttpSession(true)            	// セッションを無効化
                .deleteCookies("JSESSIONID")         		// JSESSIONID クッキーを削除
                .permitAll()
            );

        return http.build();
    }
}