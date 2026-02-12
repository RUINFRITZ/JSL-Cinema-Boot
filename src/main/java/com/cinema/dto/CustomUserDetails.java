package com.cinema.dto;

import com.cinema.domain.Member;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/** **
 * Spring Security 認証用ユーザー詳細クラス (DTO)
 * {@link UserDetails} インターフェースを実装し、ドメインエンティティである {@link Member} をラップします。
 * Spring Securityはこのクラスを通じてユーザーの認証情報および権限情報を取得します。
 * 
 * 設計意図 (Design Intent):
 * ドメインモデル (Member) とセキュリティモデル (UserDetails) を分離することで、
 * 「関心の分離 (Separation of Concerns)」を実現し、結合度を低く保ちます。
 ** **/
@Getter
@ToString
public class CustomUserDetails implements UserDetails {

    /*
     * ラップされた会員ドメインオブジェクト
     * 実際の会員情報はこのフィールドに保持されます。
     */
    private final Member member;

    /*
     * コンストラクタ
     * @param member データベースから取得した会員エンティティ
     */
    public CustomUserDetails(Member member) {
        this.member = member;
    }

    /*
     * 権限 (Authorities) の取得
     * ユーザーに付与された権限 (Role) を Spring Security が理解できる形式 (GrantedAuthority) に変換して返します。
     *
     * @return 権限リスト (例: [ROLE_USER, ROLE_ADMIN])
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // DBの role カラム (例: "ROLE_USER") を SimpleGrantedAuthority に変換
        authorities.add(new SimpleGrantedAuthority(member.getRole()));
        return authorities;
    }

    /*
     * パスワードの取得
     * @return 暗号化されたパスワード
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /*
     * ユーザー名 (ログインID) の取得
     * @return ログインID (userid)
     */
    @Override
    public String getUsername() {
        return member.getUserid();
    }

    /*
     * アカウントの有効期限チェック
     * <p>
     * true を返すと「期限切れではない」とみなされます。
     * (本システムでは期限管理を行わないため、常に true を返します)
     * </p>
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /*
     * アカウントのロック状態チェック
     * <p>
     * true を返すと「ロックされていない」とみなされます。
     * </p>
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /*
     * パスワード（資格情報）の有効期限チェック
     * <p>
     * true を返すと「期限切れではない」とみなされます。
     * </p>
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*
     * アカウントの有効状態チェック
     * <p>
     * Member エンティティの enabled フィールドと連動します。
     * false (0) の場合、ログインは拒否されます (論理削除または利用停止)。
     * </p>
     */
    @Override
    public boolean isEnabled() {
        return member.isEnabled();
    }
}