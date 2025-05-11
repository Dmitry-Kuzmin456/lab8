package org.common.request;

import java.io.Serializable;

public record Request(String name, byte[] data, UserData userData) implements Serializable {}
