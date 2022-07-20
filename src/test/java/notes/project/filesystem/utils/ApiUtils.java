package notes.project.filesystem.utils;

import lombok.experimental.UtilityClass;
import notes.project.filesystem.dto.*;
import notes.project.filesystem.exception.ExceptionCode;
import notes.project.filesystem.exception.FileSystemException;
import notes.project.filesystem.exception.ValidationException;

import java.util.Collections;

import static notes.project.filesystem.utils.TestDataConstants.*;

@UtilityClass
public class ApiUtils {
    public static ClusterCreationRequestDto clusterCreationRequestDto() {
        return new ClusterCreationRequestDto().setClusterTitle(CREATE_CLUSTER_TITLE);
    }

    public static ClusterCreationResponseDto clusterCreationResponseDto() {
        return new ClusterCreationResponseDto()
                .setTitle(CREATE_CLUSTER_TITLE)
                .setCreateDate(CREATED_CLUSTER_DATE)
                .setExternalId(CREATED_CLUSTER_EXTERNAL_ID);
    }

    public static DirectoryCreationResponseDto directoryCreationResponseDto() {
        return new DirectoryCreationResponseDto()
            .setDirectoryName(CREATE_DIRECTORY_TITLE)
            .setClusterName(CREATE_CLUSTER_TITLE)
            .setExternalId(DIRECTORY_EXTERNAL_ID)
            .setCreationDate(CREATED_DIRECTORY_TIME);
    }

    public static DirectoryCreationRequestDto directoryCreationRequestDto() {
        return new DirectoryCreationRequestDto()
            .setDirectoryName(CREATE_DIRECTORY_TITLE)
            .setClusterName(CREATE_CLUSTER_TITLE);
    }

    public static ErrorDto errorDto() {
        return new ErrorDto()
                .setCode(EXCEPTION_CODE)
                .setMessage(EXCEPTION_MESSAGE);
    }

    public static ValidationErrorDto validationErrorDto() {
        return new ValidationErrorDto(Collections.singletonList(errorDto()));
    }

    public static FileSystemException fileSystemException() {
        return new FileSystemException(ExceptionCode.CREATION_ERROR, "source message");
    }

    public static ValidationException validationException() {
        ValidationException validationException = new ValidationException();
        validationException.addCode(ExceptionCode.CREATION_ERROR);
        return validationException;
    }
}