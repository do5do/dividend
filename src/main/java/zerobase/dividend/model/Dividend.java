package zerobase.dividend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import zerobase.dividend.persist.entity.DividendEntity;

import java.time.LocalDateTime;

public record Dividend(
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime date,
        String dividend) {

    public static Dividend fromEntity(DividendEntity entity) {
        return new Dividend(entity.getDate(), entity.getDividend());
    }
}
