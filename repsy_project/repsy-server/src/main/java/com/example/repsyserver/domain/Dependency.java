package com.example.repsyserver.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Dependency {
    private String packageName;
    private String version;
}
