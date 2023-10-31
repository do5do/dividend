package zerobase.dividend.persist.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zerobase.dividend.model.constant.Authority;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "member_role")
public class MemberRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Authority role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    private MemberRoleEntity(Authority role) {
        this.role = role;
    }

    public static MemberRoleEntity of(Authority role) {
        return new MemberRoleEntity(role);
    }

    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }
}
