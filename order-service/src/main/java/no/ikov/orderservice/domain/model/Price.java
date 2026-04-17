package no.ikov.orderservice.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Price {

    @Column(nullable = false)
    private BigDecimal amount;

    @Convert(converter = Price.CurrencyConverter.class)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Converter(autoApply = true)
    static class CurrencyConverter implements AttributeConverter<Currency, String> {

        @Override
        public String convertToDatabaseColumn(Currency currency) {
            return currency == null ? null : currency.getCurrencyCode();
        }

        @Override
        public Currency convertToEntityAttribute(String code) {
            return code == null ? null : Currency.getInstance(code);
        }
    }
}
