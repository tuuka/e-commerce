package net.tuuka.ecommerce.controller.util;

import net.tuuka.ecommerce.config.AppProperties;
import net.tuuka.ecommerce.controller.RootApiController;
import net.tuuka.ecommerce.model.BaseEntity;
import net.tuuka.ecommerce.model.Product;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.hateoas.mediatype.alps.Descriptor;
import org.springframework.hateoas.mediatype.alps.Format;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mediatype.PropertyUtils.getExposedProperties;
import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.mediatype.alps.Alps.doc;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class AlpsHelper {

    private final AppProperties appProperties;
    private final LinkRelationProvider linkRelationProvider;
    private final Map<String, String> PROFILE_HREF_RELATIONS;

    public AlpsHelper(AppProperties appProperties, LinkRelationProvider linkRelationProvider) {
        this.appProperties = appProperties;
        this.linkRelationProvider = linkRelationProvider;
        this.PROFILE_HREF_RELATIONS = appProperties.getAlps().getProfileRelations();
    }

    public Descriptor getMethodDescriptor(Method method, Class<? extends BaseEntity> entityClass) {

        String name = getMethodAlpsName(method, entityClass);

        String rt = getMethodAlpsRt(name, entityClass);

        String href = getHrefToRestMethod(method);

        RequestMethod requestMethod = ((RequestMethod[]) getRequestMappingAttr(method).get("method"))[0];
        Type type = Type.IDEMPOTENT;
        if (requestMethod.equals(RequestMethod.POST)) type = Type.UNSAFE;
        if (requestMethod.equals(RequestMethod.GET)) type = Type.SAFE;

        return descriptor().id(dashOnCapitals(method.getName()))
                .name(name).href(href).type(type).rt(rt).descriptor(
                        getRequestParametersDescriptors(method)
                ).build();
    }


    private String getMethodAlpsRt(String name, Class<? extends BaseEntity> entityClass) {

        // if returning entity is the same then return local link
        if (name.equals(getEntityName(entityClass, false)) ||
                name.equals(getEntityName(entityClass, true))
        ) return composeRepresentationString(entityClass);

        // if returning entity is not the same then return link to returning entity's profile
        return PROFILE_HREF_RELATIONS.containsKey(name) ? getProfileRepresentationLink(name) : "";
    }

    private String getProfileRepresentationLink(String methodName) {
        return getHrefToRestMethod(RootApiController.class, PROFILE_HREF_RELATIONS.get(methodName))
                + composeRepresentationString(Product.class);
    }

    private String getEntityName(Class<? extends BaseEntity> entityClass, Boolean plural) {
        return (plural != null && plural) ?
                linkRelationProvider.getCollectionResourceRelFor(entityClass).value()
                : linkRelationProvider.getItemResourceRelFor(entityClass).value();
    }

    public String composeRepresentationString(Class<? extends BaseEntity> entityClass) {
        return "#" + getEntityName(entityClass, false) + "-representation";
    }

    private String dashOnCapitals(String s) {
        return s.replaceAll("(?<![_-]|^)(?=[A-Z])", "-").toLowerCase();
    }

    private AnnotationAttributes getRequestMappingAttr(Method method) {
        return MergedAnnotations.from(method)
                .get(RequestMapping.class).asAnnotationAttributes();
    }

    private String getHrefToRestMethod(Method method) {
        return getHrefToRestMethod(method, getRequestMappingAttr(method));
    }

    public String getHrefToRestMethod(Class<?> clazz, String methodName) {
        Method method = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().orElse(null);
        return method == null ? "" : getHrefToRestMethod(method, getRequestMappingAttr(method));
    }

    private String getHrefToRestMethod(Method method, AnnotationAttributes attr) {
        return linkTo(method.getDeclaringClass()).toUriComponentsBuilder()
                .pathSegment(extractPathFromRequestMappingAttr(attr)).build().toUriString();
    }

    private String extractPathFromRequestMappingAttr(AnnotationAttributes attr) {
        String[] paths = attr.getStringArray("path");
        return paths.length > 0 ? paths[0].substring(1) : "";
    }

    private String getMethodAlpsName(Method method, Class<? extends BaseEntity> entityClass) {

        AnnotationAttributes attr = getRequestMappingAttr(method);
        String path = extractPathFromRequestMappingAttr(attr);
        String[] segments = path.split("/");
        // may be should check if full path ends with '/' to avoid empty string here
        path = segments[segments.length - 1].toLowerCase();

        return PROFILE_HREF_RELATIONS.containsKey(path) ? path : getEntityName(entityClass,
                Iterable.class.isAssignableFrom(method.getReturnType()));
    }

    public List<Descriptor> getClassFieldsDescriptors(Class<?> clazz) {

        return getExposedProperties(clazz).stream()
                .map(property -> {
                    Descriptor.DescriptorBuilder builder = getSemanticDescriptorBuilder(property.getName());
                    if (PROFILE_HREF_RELATIONS.containsKey(property.getName())) {
                        builder.type(Type.SAFE).href(getProfileRepresentationLink(property.getName()));
                    }
                    return builder.build();
                }).collect(Collectors.toList());
    }

    private List<Descriptor> getRequestParametersDescriptors(Method method) {
        List<Descriptor> descriptorList = new LinkedList<>();
        for (Parameter parameter : method.getParameters()) {
            Annotation[] annotations = parameter.getAnnotations();
            for (Annotation annotation : annotations) {
                if (PathVariable.class.isAssignableFrom(annotation.annotationType()) ||
                        RequestParam.class.isAssignableFrom(annotation.annotationType())) {
                    descriptorList.add(getSemanticDescriptorBuilder(AnnotationUtils
                            .getAnnotationAttributes(parameter, annotation)
                            .getString("value")).build()
                    );
                }
                if (RequestBody.class.isAssignableFrom(annotation.annotationType())) {
                    descriptorList.add(descriptor().descriptor(
                            getClassFieldsDescriptors(parameter.getType())).build());
                }
            }
        }
        return descriptorList;
    }

    private Descriptor.DescriptorBuilder getSemanticDescriptorBuilder(String name) {
        Descriptor.DescriptorBuilder builder = descriptor().id(name).type(Type.SEMANTIC);
        String text = appProperties.getAlps().getDoc().get(name);
        if (text != null) builder.doc(doc().format(Format.TEXT).value(text).build());
        return builder;
    }

}
