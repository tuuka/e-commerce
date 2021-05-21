package net.tuuka.ecommerce.controller.util;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.controller.RootApiController;
import net.tuuka.ecommerce.entity.BaseEntity;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.hateoas.mediatype.alps.Descriptor;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@RequiredArgsConstructor
public class AlpsHelper {

    private final List<String> AVAILABLE_CROSS_LINKS_IN_PATH = Arrays.asList("products", "category");

    private final LinkRelationProvider linkRelationProvider;

    public Descriptor getMethodDescriptor(Method method, Class<? extends BaseEntity> entityClass) {
        String name, href, rt;

        AnnotationAttributes attr = getRequestMappingAttr(method);

        // TODO: check this and remove
        if (attr.size() == 0) return null;

        name = extractNameFromPath(attr);
        name = name != null ? name : getEntityName(entityClass,
                Iterable.class.isAssignableFrom(method.getReturnType()));

        rt = "#" + getRepresentationString(entityClass);
        // if method return instance(s) of referenced entity link to this entity profile is needed
        if (name.equals("products"))
            rt = getHrefToRestMethod(RootApiController.class, "productProfile")
                    + "#" + getRepresentationString(Product.class);
        if (name.equals("category") && method.getDeclaringClass().isAssignableFrom(ProductRestController.class))
            rt = getHrefToRestMethod(RootApiController.class, "categoriesProfile")
                    + "#" + getRepresentationString(ProductCategory.class);

        href = getHrefToRestMethod(method, attr);

        RequestMethod requestMethod = ((RequestMethod[]) attr.get("method"))[0];
        Type type = Type.IDEMPOTENT;
        if (requestMethod.equals(RequestMethod.POST)) type = Type.UNSAFE;
        if (requestMethod.equals(RequestMethod.GET)) type = Type.SAFE;

        method.getParameters();

        return descriptor().id(dashOnCapitals(method.getName()))
                .name(name).href(href).type(type).rt(rt).build();
    }

    private String getEntityName(Class<? extends BaseEntity> entityClass, Boolean plural) {
        return (plural != null && plural) ?
                linkRelationProvider.getCollectionResourceRelFor(entityClass).value()
                : linkRelationProvider.getItemResourceRelFor(entityClass).value();
    }

    public String getRepresentationString(Class<? extends BaseEntity> entityClass) {
        return getEntityName(entityClass, false) + "-representation";
    }

    private String dashOnCapitals(String s) {
        return s.replaceAll("(?<![_-]|^)(?=[A-Z])", "-").toLowerCase();
    }

    private AnnotationAttributes getRequestMappingAttr(Method method) {
        return MergedAnnotations.from(method)
                .get(RequestMapping.class).asAnnotationAttributes();
    }

    private String getHrefToRestMethod(Method method, AnnotationAttributes attr) {
        return linkTo(method.getDeclaringClass()).toUriComponentsBuilder()
                .pathSegment(extractPathFromRequestMappingAttr(attr)).build().toUriString();
    }

    public String getHrefToRestMethod(Class<?> clazz, String methodName) {
        Method method = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().orElse(null);
        return method == null ? "" : getHrefToRestMethod(method, getRequestMappingAttr(method));
    }

    private String extractPathFromRequestMappingAttr(AnnotationAttributes attr) {
        String[] paths = attr.getStringArray("path");
        return paths.length > 0 ? paths[0].substring(1) : "";
    }

    private String extractNameFromPath(AnnotationAttributes attr) {
        String path = extractPathFromRequestMappingAttr(attr);
        String[] segments = path.split("/");
        // may be should check if full path ends with '/' to avoid empty string here
        path = segments[segments.length - 1].toLowerCase();
        return AVAILABLE_CROSS_LINKS_IN_PATH.contains(path) ? path : null;
    }

}
