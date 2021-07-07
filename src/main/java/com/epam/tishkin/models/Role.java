package com.epam.tishkin.models;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "role")
@XmlEnum
public enum Role {
    @XmlEnumValue("ADMINISTRATOR")
    ADMINISTRATOR,
    @XmlEnumValue("VISITOR")
    VISITOR
}
