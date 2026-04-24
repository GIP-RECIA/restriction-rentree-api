package fr.recia.restriction_rentree_api.configuration;

import lombok.Data;

import java.util.List;

@Data
public class SecurityProperty {
    private String name;
    private List<String> allowedIps;
    private List<String> allowedPaths;
}
