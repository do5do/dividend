package zerobase.dividend.model;

import zerobase.dividend.model.constant.Authority;
import zerobase.dividend.persist.entity.MemberRole;

import java.util.List;

public record Role(
        Authority role) {

    public static Role fromEntity(MemberRole memberRole) {
        return new Role(memberRole.getRole());
    }

    public static List<Role> toListFromEntity(List<MemberRole> roles) {
        return roles.stream()
                .map(Role::fromEntity)
                .toList();
    }
}
