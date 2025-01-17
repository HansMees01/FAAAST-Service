/*
 * Copyright (c) 2021 Fraunhofer IOSB, eine rechtlich nicht selbstaendige
 * Einrichtung der Fraunhofer-Gesellschaft zur Foerderung der angewandten
 * Forschung e.V.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.iosb.ilt.faaast.service.model.api;

import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Model class for a result.
 */
public class Result {

    private boolean success;
    private List<Message> messages;

    public Result() {
        this.success = true;
        this.messages = new ArrayList<>();
    }


    public boolean getSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }


    public List<Message> getMessages() {
        return messages;
    }


    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result result = (Result) o;
        return Objects.equals(success, result.success)
                && Objects.equals(messages, result.messages);
    }


    @Override
    public int hashCode() {
        return Objects.hash(success, messages);
    }


    /**
     * Creates a new instance with given values and sets success flag according to the messageType.
     *
     * @param messageType the messageType to set
     * @param message the message to set
     * @return new instance with given values
     */
    public static Result of(MessageType messageType, String message) {
        return builder()
                .success(messageType == MessageType.INFO || messageType == MessageType.WARNING)
                .message(Message.builder()
                        .messageType(messageType)
                        .text(message)
                        .build())
                .build();
    }


    /**
     * Creates a new instance with given message type INFO and given message.
     *
     * @param message the message to set
     * @return new instance with given values
     */
    public static Result info(String message) {
        return of(MessageType.INFO, message);
    }


    /**
     * Creates a new instance with given message type WARNING and given message.
     *
     * @param message the message to set
     * @return new instance with given values
     */
    public static Result warning(String message) {
        return of(MessageType.WARNING, message);
    }


    /**
     * Creates a new instance with given message type ERROR and given message.
     *
     * @param message the message to set
     * @return new instance with given values
     */
    public static Result error(String message) {
        return of(MessageType.ERROR, message);
    }


    /**
     * Creates a new instance with given message type EXCEPTION and given message.
     *
     * @param message the message to set
     * @return new instance with given values
     */
    public static Result exception(String message) {
        return of(MessageType.EXCEPTION, message);
    }


    public static Builder builder() {
        return new Builder();
    }

    private abstract static class AbstractBuilder<T extends Result, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {

        public B success(boolean value) {
            getBuildingInstance().setSuccess(value);
            return getSelf();
        }


        public B messages(List<Message> value) {
            getBuildingInstance().setMessages(value);
            return getSelf();
        }


        public B message(Message value) {
            getBuildingInstance().getMessages().add(value);
            return getSelf();
        }

    }

    public static class Builder extends AbstractBuilder<Result, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected Result newBuildingInstance() {
            return new Result();
        }
    }
}
