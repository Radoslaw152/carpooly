package bg.fmi.spring.course.project.dao;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RideCoordinatesConverter implements AttributeConverter<List<Coordinates>, String> {
    private static final Gson GSON = new Gson();

    @Override
    public String convertToDatabaseColumn(final List<Coordinates> attribute) {
        return GSON.toJson(attribute);
    }

    @Override
    public List<Coordinates> convertToEntityAttribute(final String dbData) {
        Coordinates[] coordinates = GSON.fromJson(dbData, Coordinates[].class);
        if (coordinates == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(coordinates);
    }
}
