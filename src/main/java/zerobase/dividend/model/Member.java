package zerobase.dividend.model;

import zerobase.dividend.persist.entity.MemberEntity;

import java.util.List;

public record Member(
        String username,
        String password,
        List<MemberRole> memberRoles) {

    public static Member fromEntity(MemberEntity memberEntity) {
        return new Member(memberEntity.getUsername(),
                memberEntity.getPassword(),
                MemberRole.toListFromEntity(memberEntity.getRoles()));
    }
}
