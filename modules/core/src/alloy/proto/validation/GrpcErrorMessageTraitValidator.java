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

package alloy.proto.validation;

import alloy.proto.GrpcErrorMessageTrait;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.validation.AbstractValidator;
import software.amazon.smithy.model.validation.Severity;
import software.amazon.smithy.model.validation.ValidationEvent;

public final class GrpcErrorMessageTraitValidator extends AbstractValidator {
    public static final String MULTIPLE_GRPC_ERROR_MESSAGE = "GrpcErrorMessageMultipleMembers";

    @Override
    public List<ValidationEvent> validate(Model model) {
        return model.getMemberShapesWithTrait(GrpcErrorMessageTrait.class).stream()
                .collect(Collectors.groupingBy(MemberShape::getContainer))
                .entrySet().stream()
                .flatMap(entry -> validateContainer(model, entry.getKey(), entry.getValue()).stream())
                .collect(Collectors.toList());
    }

    private List<ValidationEvent> validateContainer(Model model, ShapeId containerId, List<MemberShape> members) {
        if (members.size() <= 1) {
            return Collections.emptyList();
        }

        Shape container = model.getShape(containerId).orElse(null);
        return Collections.singletonList(ValidationEvent.builder()
                .id(MULTIPLE_GRPC_ERROR_MESSAGE)
                .severity(Severity.ERROR)
                .shape(container)
                .message("Multiple members are annotated with @grpcErrorMessage; only one is allowed")
                .build());
    }
}
