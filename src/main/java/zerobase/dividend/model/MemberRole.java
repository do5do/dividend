package zerobase.dividend.model;

import zerobase.dividend.model.constant.Authority;
import zerobase.dividend.persist.entity.MemberRoleEntity;

import java.util.List;

public record MemberRole(
        Authority role) {

    public static MemberRole fromEntity(MemberRoleEntity memberRoleEntity) {
        return new MemberRole(memberRoleEntity.getRole());
    }

    public static List<MemberRole> toListFromEntity(List<MemberRoleEntity> roles) {
        return roles.stream()
                .map(MemberRole::fromEntity)
                .toList();
    }
}
