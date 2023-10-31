package zerobase.dividend.persist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "member")
public class MemberEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL)
    private List<MemberRoleEntity> roles = new ArrayList<>();

    private MemberEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static MemberEntity of(String username, String password) {
        return new MemberEntity(username, password);
    }

    public void addMemberRole(MemberRoleEntity memberRoleEntity) {
        roles.add(memberRoleEntity);
        memberRoleEntity.setMemberEntity(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(o -> new SimpleGrantedAuthority(o.getRole().name()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
