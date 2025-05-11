package org.common.request;

import java.io.Serializable;

public record UserDataRequest(char type, String login, String password) implements Serializable {}
