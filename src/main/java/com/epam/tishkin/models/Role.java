package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum Role {
    ADMINISTRATOR,
    VISITOR
}
