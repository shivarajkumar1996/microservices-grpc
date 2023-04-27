package com.microservices.grpc.util;

import com.microservices.grpc.config.FileStorageProperties;
import com.microservices.grpc.constants.CommonConstants;
import com.microservices.grpc.exceptions.FileParsingException;
import com.microservices.grpc.exceptions.FileStorageException;
import com.microservices.grpc.exceptions.ResourceNotFoundException;
import com.microservices.grpc.pojo.FilePojo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Log4j2
@Component
public class FileUtility {

    private final Path fileStorageDir;

    @Autowired
    public FileUtility(FileStorageProperties fileStorageProperties) {
        this.fileStorageDir = Path.of(fileStorageProperties.getStorageDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageDir);
        } catch (Exception ex) {
            log.error("Error creating storage dir on the server. Exception: {}", ex);
            throw new FileStorageException("Error occurred while creating the file storage directory.",  Map.of("storage_dir", this.fileStorageDir.toAbsolutePath().toString(), "message", "Error occurred while creating the file storage directory.", "exception", ex.toString()));
        }
    }
    public FilePojo findById(String id){

        File directory = new File(fileStorageDir.toString());
        FileFilter filter = new FileFilter(id + ".");
        String[] fileList = directory.list(filter);

        if (fileList == null || fileList.length == 0) {
            throw new ResourceNotFoundException("Resource not found.",  Map.of("id", id, "message", "Resource Not Found"));
        }

        if(fileList.length > 1){
            log.warn("Multiple files detected for the user with id: {}. This is not an expected behaviour. Pick the first occurrence of the file name.", id);
        }
        String fileName = Path.of(fileStorageDir.toString(),fileList[0] ).toAbsolutePath().toString();
        FilePojo filePojo = parseFile(id, fileName);
        return filePojo;

    }

    public FilePojo parseFile(String id, String fileName){
        FilePojo filePojo = null;
        String extension = FilenameUtils.getExtension(fileName);
        if(extension.equals(CommonConstants.CSV_FILE_EXTENSION)){
            filePojo = parseCSVFile(id, fileName);
        }else{

            filePojo = parseXMLFile(id, fileName);
        }
        return filePojo;
    }

    public FilePojo parseCSVFile(String id, String fileName){
        FilePojo filePojo = null;
        try(
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withHeader().parse(br);
        ) {
            for(CSVRecord record : parser) {
                filePojo = new FilePojo();
                filePojo.setId(Integer.parseInt(record.get(CommonConstants.ID_COLUMN)));
                filePojo.setName(record.get(CommonConstants.NAME_COLUMN));
                filePojo.setDob(record.get(CommonConstants.DOB_COLUMN));
                filePojo.setAge(Integer.parseInt(record.get(CommonConstants.AGE_COLUMN_NAME)));
                filePojo.setSalary(Double.parseDouble(record.get(CommonConstants.SALARY_COLUMN_NAME)));
                break;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Resource not found.",  Map.of("id", id, "message", "Resource Not Found"));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new FileParsingException("Error occurred while parsing the file.",  Map.of("file", fileName, "message", "Error occurred while parsing the file.", "exception", e.toString()));
        }
        return filePojo;

    }

    public FilePojo parseXMLFile(String id, String fileName){
        File xmlFile = new File(fileName);
        FilePojo filePojo = null;
        JAXBContext jaxbContext;
        try
        {
            jaxbContext = JAXBContext.newInstance(FilePojo.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            filePojo = (FilePojo) jaxbUnmarshaller.unmarshal(xmlFile);

        }
        catch (JAXBException e)
        {
            e.printStackTrace();
            throw new FileParsingException("Error occurred while parsing the file.",  Map.of("file", fileName, "message", "Error occurred while parsing the file.", "exception", e.toString()));
        }

        return filePojo;


    }
}
