package notes.project.filesystem.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.plaf.nimbus.AbstractRegionPainter;

import notes.project.filesystem.file.impl.ZipManagerImpl;
import notes.project.filesystem.model.Cluster;
import notes.project.filesystem.model.CreatedFile;
import notes.project.filesystem.model.Directory;
import notes.project.filesystem.utils.ApplicationPropertiesUtils;
import notes.project.filesystem.utils.DbUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static notes.project.filesystem.utils.TestDataConstants.*;

@ExtendWith(MockitoExtension.class)
class ZipManagerImplTest extends FileSystemTest {

    @Mock
    private FileManager fileManager;

    private ZipManager zipManager;

    @BeforeEach
    void init() {
        zipManager = new ZipManagerImpl(
            ApplicationPropertiesUtils.applicationPropertiesForZipManager(),
            fileManager
        );
    }


    @Test
    void zipDirectorySuccess() throws IOException {
        when(fileManager.readFile(any())).thenReturn(FILE_CONTENT);
        Directory directory = DbUtils.directoryWithFiles();

        createClusterPath(ARCHIVE_ROOT_PATH);

        zipManager.zipDirectory(directory);

        assertFileCreated(ZIPPED_CREATED_FILE_PATH);

        Files.delete(ZIPPED_CREATED_FILE_PATH);
        Files.delete(ARCHIVE_ROOT_PATH);
    }

    @Test
    void zipCreatedFileSuccess() throws IOException {
        when(fileManager.readFile(any())).thenReturn(FILE_CONTENT);
        CreatedFile createdFile = DbUtils.createdFile();

        createClusterPath(ARCHIVE_ROOT_PATH);

        zipManager.zipCreatedFile(createdFile);

        assertFileCreated(ZIPPED_CREATED_FILE_PATH);

        Files.delete(ZIPPED_CREATED_FILE_PATH);
        Files.delete(ARCHIVE_ROOT_PATH);
    }

    @Test
    void zipClusterSuccess() throws IOException {
        when(fileManager.readFile(any())).thenReturn(FILE_CONTENT);
        Cluster cluster = DbUtils.clusterWithFiles();

        createClusterPath(ARCHIVE_ROOT_PATH);

        zipManager.zipCluster(cluster);

        assertFileCreated(ZIPPED_CREATED_FILE_PATH);

        Files.delete(ZIPPED_CREATED_FILE_PATH);
        Files.delete(ARCHIVE_ROOT_PATH);
    }

    @Test
    void zipFileForUpdateSuccess() throws IOException {
        createFileWithContent();
        createClusterPath(ARCHIVE_ROOT_PATH);
        when(fileManager.readFile(DbUtils.createdFile())).thenReturn(FILE_CONTENT);

        zipManager.zipFileForUpdate(DbUtils.createdFile(), FILE_VERSION_UUID);

        assertFileCreated(ZIPPED_FILE_PATH_FOR_UPDATE);
        FileUtils.deleteDirectory(new File(ARCHIVE_ROOT));
    }
}
