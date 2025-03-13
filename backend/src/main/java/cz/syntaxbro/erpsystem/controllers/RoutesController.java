package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Routes Controller", description = "Returns all available routes grouped by category")
@EnableMethodSecurity
@PreAuthorize("hasRole('ADMIN')")
public class RoutesController {

    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public RoutesController(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Operation(summary = "Get all available routes grouped by prefix with optional filtering")
    @GetMapping
    public Object getRoutes(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false, defaultValue = "false") boolean groups) {

        List<RouteInfo> routes = handlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> {
                    RequestMappingInfo mappingInfo = entry.getKey();
                    Set<String> patterns = getPatterns(mappingInfo);
                    Set<String> methods = mappingInfo.getMethodsCondition().getMethods().stream()
                            .map(Enum::name)
                            .collect(Collectors.toSet());

                    return patterns.stream()
                            .flatMap(pattern -> methods.stream()
                                    .map(method -> new RouteInfo(method, pattern))
                            );
                })
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<Map<String, String>>> groupedRoutes = groupByPrefix(routes);

        // Aplikujeme filtr, pokud je přítomen
        if (filter != null && !filter.isEmpty()) {
            groupedRoutes = groupedRoutes.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(filter) ||
                            entry.getValue().stream().anyMatch(route -> route.get("path").contains(filter)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, TreeMap::new));
        }

        if (groups) {
            return new ArrayList<>(groupedRoutes.keySet());
        }

        return groupedRoutes;
    }

    private Set<String> getPatterns(RequestMappingInfo requestMappingInfo) {
        if (requestMappingInfo.getPathPatternsCondition() != null) {
            return requestMappingInfo.getPathPatternsCondition().getPatterns().stream()
                    .map(PathPattern::getPatternString)
                    .collect(Collectors.toSet());
        } else if (requestMappingInfo.getPatternsCondition() != null) {
            return requestMappingInfo.getPatternsCondition().getPatterns();
        }
        return Collections.emptySet();
    }

    private Map<String, List<Map<String, String>>> groupByPrefix(List<RouteInfo> routes) {
        Map<String, List<Map<String, String>>> groupedRoutes = new TreeMap<>();

        for (RouteInfo route : routes) {
            String[] parts = route.path.split("/");
            String key = (parts.length > 2) ? "/" + parts[1] + "/" + parts[2] : route.path;
            String relativePath = route.path.replaceFirst("^" + key, "").replaceAll("^/", "");

            if (relativePath.isEmpty()) {
                relativePath = "/";
            }

            groupedRoutes.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(Map.of("method", route.method, "path", relativePath));
        }

        return groupedRoutes;
    }

    private static class RouteInfo {
        String method;
        String path;

        RouteInfo(String method, String path) {
            this.method = method;
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouteInfo routeInfo = (RouteInfo) o;
            return Objects.equals(method, routeInfo.method) && Objects.equals(path, routeInfo.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, path);
        }
    }
}
