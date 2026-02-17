/* Copyright 2022 Disney Streaming
 *
 * Licensed under the Tomorrow Open Source Technology License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://disneystreaming.github.io/TOST-1.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alloy.proto;

import java.util.Optional;

import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.NumberNode;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;

public final class GrpcErrorTrait extends AbstractTrait {
    public static final ShapeId ID = ShapeId.from("alloy.proto#grpcError");

    private final int code;
    private final String message;

    private GrpcErrorTrait(Builder builder) {
        super(ID, builder.sourceLocation);
        this.code = builder.code;
        this.message = builder.message;
    }

    public int getCode() {
        return code;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    protected Node createNode() {
        ObjectNode.Builder b =
            ObjectNode.builder().withMember("errorCode", Node.from(code));

        if (message != null) {
            b.withMember("message", Node.from(message));
        }
        return b.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GrpcErrorTrait fromNode(Node value) {
        ObjectNode obj = value.expectObjectNode();
        Node codeNode = obj.expectMember("errorCode");
        int code = toIntEnumCode(codeNode);

        String message = obj.getStringMember("message").map(StringNode::getValue).orElse(null);

        return GrpcErrorTrait.builder()
                .sourceLocation(value.getSourceLocation())
                .code(code)
                .message(message)
                .build();
    }

    private static int toIntEnumCode(Node node) {
        // Handle either numeric (13) or symbolic ("INTERNAL")
        if (node.isNumberNode()) {
            return node.expectNumberNode().getValue().intValue();
        }
        if (node.isStringNode()) {
            String name = node.expectStringNode().getValue();
            switch (name) {
                case "OK":                   return 0;
                case "CANCELLED":            return 1;
                case "UNKNOWN":              return 2;
                case "INVALID_ARGUMENT":     return 3;
                case "DEADLINE_EXCEEDED":    return 4;
                case "NOT_FOUND":            return 5;
                case "ALREADY_EXISTS":       return 6;
                case "PERMISSION_DENIED":    return 7;
                case "RESOURCE_EXHAUSTED":   return 8;
                case "FAILED_PRECONDITION":  return 9;
                case "ABORTED":              return 10;
                case "OUT_OF_RANGE":         return 11;
                case "UNIMPLEMENTED":        return 12;
                case "INTERNAL":             return 13;
                case "UNAVAILABLE":          return 14;
                case "DATA_LOSS":            return 15;
                case "UNAUTHENTICATED":      return 16;
                default:
                    throw new IllegalArgumentException("Unknown ErrorCode name: " + name);
            }
        }
        throw new IllegalArgumentException("Invalid code node type: " + node.getType());
    }

    public static final class Builder {
        private SourceLocation sourceLocation = SourceLocation.NONE;
        private int code;
        private String message;

        public Builder sourceLocation(SourceLocation sourceLocation) {
            this.sourceLocation = sourceLocation;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public GrpcErrorTrait build() {
            return new GrpcErrorTrait(this);
        }
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public GrpcErrorTrait createTrait(ShapeId target, Node value) {
            return GrpcErrorTrait.fromNode(value);
        }
    }
}