package zerobase.dividend.model;

import zerobase.dividend.persist.entity.CompanyEntity;

public record Company(
        String ticker,
        String name) {

    public static Company fromEntity(CompanyEntity entity) {
        return new Company(entity.getTicker(), entity.getName());
    }
}
