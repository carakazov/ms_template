package notes.project.filesystem.service;

import java.util.Optional;
import java.util.UUID;

import io.swagger.annotations.Api;
import notes.project.filesystem.dto.AddFileRequestDto;
import notes.project.filesystem.dto.AddFileResponseDto;
import notes.project.filesystem.dto.ReadCreatedFileDto;
import notes.project.filesystem.file.FileManager;
import notes.project.filesystem.file.ZipManager;
import notes.project.filesystem.mapper.FileCreationMapper;
import notes.project.filesystem.mapper.ReadFileMapper;
import notes.project.filesystem.model.CreatedFile;
import notes.project.filesystem.model.Directory;
import notes.project.filesystem.repository.CreatedFileRepository;
import notes.project.filesystem.service.*;
import notes.project.filesystem.service.impl.CreatedFileServiceImpl;
import notes.project.filesystem.utils.ApiUtils;
import notes.project.filesystem.utils.DbUtils;
import notes.project.filesystem.service.ObjectExistingStatusChanger;
import notes.project.filesystem.validation.BusinessValidator;
import notes.project.filesystem.validation.Validator;
import notes.project.filesystem.validation.impl.FileCreationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static notes.project.filesystem.utils.TestDataConstants.*;

@ExtendWith(MockitoExtension.class)
class CreatedFileServiceImplTest {
    @Mock
    private CreatedFileRepository repository;
    @Mock
    private FileManager fileManager;
    @Mock
    private DirectoryService directoryService;
    @Mock
    private ClusterService clusterService;
    @Mock
    private FileCreationValidator fileCreationValidator;
    @Mock
    private DeleteHistoryService deleteHistoryService;
    @Mock
    private ObjectExistingStatusChanger objectExistingStatusChanger;
    @Mock
    private ZipManager zipManager;
    @Mock
    private Validator<CreatedFile> deleteDirectoryValidator;
    @Mock
    private BusinessValidator<CreatedFile> readFileValidator;

    private CreatedFileService service;


    @BeforeEach
    void init() {
        service = new CreatedFileServiceImpl(
            repository,
            fileManager,
            directoryService,
            Mappers.getMapper(FileCreationMapper.class),
            clusterService,
            fileCreationValidator,
            deleteHistoryService,
            zipManager,
            objectExistingStatusChanger,
            deleteDirectoryValidator,
            Mappers.getMapper(ReadFileMapper.class),
            readFileValidator
        );
    }

    @Test
    void addFileSuccess() {
        when(directoryService.findByExternalId(any())).thenReturn(DbUtils.directory());
        when(repository.save(any())).thenReturn(DbUtils.createdFile());
        AddFileRequestDto request = ApiUtils.addFileRequestDto();
        AddFileResponseDto expected = ApiUtils.addFileResponseDto();

        AddFileResponseDto actual = service.addFile(request);

        assertEquals(expected, actual);

        verify(directoryService).findByExternalId(request.getDirectoryExternalId());
    }

    @Test
    void deleteCreatedFileSuccess() {
        CreatedFile createdFile = DbUtils.createdFile();
        when(repository.findByExternalId(any())).thenReturn(Optional.of(createdFile));

        service.deleteCreatedFile(CREATED_FILE_EXTERNAL_ID);

        verify(deleteHistoryService).createCreatedFileDeleteHistory(createdFile.setDeleted(Boolean.TRUE));
        verify(clusterService).updateClusterLastRequestedTime(createdFile.getDirectory().getCluster());
        verify(zipManager).zipCreatedFile(createdFile);
    }

    @Test
    void readFileSuccess() {
        CreatedFile file = DbUtils.createdFile();
        when(repository.findByExternalId(any())).thenReturn(Optional.of(file));
        when(fileManager.readFile(any())).thenReturn(FILE_CONTENT);
        ReadCreatedFileDto expected = ApiUtils.readCreatedFileDto();

        ReadCreatedFileDto actual = service.readFile(CREATED_FILE_EXTERNAL_ID);

        assertEquals(expected, actual);

        verify(repository).findByExternalId(CREATED_FILE_EXTERNAL_ID);
        verify(fileManager).readFile(file);
    }

}