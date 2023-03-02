package view3d.lib;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean value) {
		return (value != null && value) ? "1" : "0";
	}

	@Override
	public Boolean convertToEntityAttribute(String value) {
		return "1".equals(value);
	}
}