package org.common.request;

import java.io.Serializable;

public record UserData(String login, String password) implements Serializable {}

