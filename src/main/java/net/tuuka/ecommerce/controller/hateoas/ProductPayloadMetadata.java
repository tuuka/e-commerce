package net.tuuka.ecommerce.controller.hateoas;

import net.tuuka.ecommerce.entity.Product;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.mediatype.PropertyUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ProductPayloadMetadata implements AffordanceModel.InputPayloadMetadata {

    private static final List<String> EXCLUDE_LIST = Arrays.asList("created", "lastUpdated", "id");

    private final ResolvableType type = ResolvableType.forClass(Product.class);
    private final SortedMap<String, AffordanceModel.PropertyMetadata> properties =
            new TreeMap<>(PropertyUtils.getExposedProperties(type).stream()
                    .filter(p -> !EXCLUDE_LIST.contains(p.getName()))
                    .collect(Collectors.toMap(AffordanceModel.PropertyMetadata::getName,
                            Function.identity())));

    @Override
    @NonNull
    public <T extends AffordanceModel.PropertyMetadataConfigured<T> & AffordanceModel.Named> T applyTo(T target) {

        AffordanceModel.PropertyMetadata metadata = this.properties.get(target.getName());

        return metadata == null ? target : target.apply(metadata);
    }

    @Override
    @NonNull
    public <T extends AffordanceModel.Named> T customize(T target, @NonNull Function<AffordanceModel.PropertyMetadata, T> customizer) {

        AffordanceModel.PropertyMetadata metadata = this.properties.get(target.getName());

        return metadata == null ? target : customizer.apply(metadata);
    }

    @Override
    @NonNull
    public Stream<AffordanceModel.PropertyMetadata> stream() {
        return properties.values().stream();
    }

    @Override
    @NonNull
    public List<String> getI18nCodes() {

        Class<?> type = this.type.resolve(Object.class);

        return Arrays.asList(type.getName(), type.getSimpleName());
    }

    ResolvableType getType() {
        return this.type;
    }
}
